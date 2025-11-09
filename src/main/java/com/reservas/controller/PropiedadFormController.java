package com.reservas.controller;

import com.reservas.dao.PropiedadDAO;
import com.reservas.model.Propiedad;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * <h1>Controlador del formulario de Propiedades</h1>
 *
 * Controlador responsable de gestionar la ventana de creación o edición de propiedades.
 * Se encarga de validar los datos, comunicarse con la capa DAO y actualizar la tabla
 * principal tras guardar los cambios.
 *
 * <h2>Funciones principales:</h2>
 * <ul>
 *     <li>Creación y modificación de registros de propiedades.</li>
 *     <li>Validación de campos obligatorios y numéricos.</li>
 *     <li>Comunicación con {@link PropiedadDAO} para operaciones CRUD.</li>
 *     <li>Actualización dinámica de la tabla principal en {@link PropiedadController}.</li>
 * </ul>
 *
 * @author Diego Regueira
 * @since 04/11/2025
 */
public class PropiedadFormController {

    @FXML private Label lblTitulo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private TextField txtPrecioNoche;
    @FXML private Spinner<Integer> spnCapacidad;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private Propiedad propiedad;
    private PropiedadController propiedadController;

    /**
     * Inicializa los componentes de la interfaz del formulario.
     */
    @FXML
    public void initialize() {

        cmbEstado.getItems().addAll("disponible", "ocupada", "mantenimiento");

        SpinnerValueFactory<Integer> vf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        spnCapacidad.setValueFactory(vf);
    }

    /**
     * Valida los campos del formulario y guarda una nueva propiedad o actualiza una existente.
     * <ul>
     *     <li>Verifica que los campos no estén vacíos.</li>
     *     <li>Valida los valores numéricos (precio y capacidad).</li>
     *     <li>Usa {@link PropiedadDAO} para realizar inserción o actualización.</li>
     *     <li>Actualiza la tabla principal tras completar la operación.</li>
     * </ul>
     */
    @FXML
    void guardarPropiedad() {

        try {

            String nombre = txtNombre.getText();
            String direccion = txtDireccion.getText();
            String ciudad = txtCiudad.getText();
            String pais = txtPais.getText();
            float precio = Float.parseFloat(txtPrecioNoche.getText().trim());
            Integer capacidad = spnCapacidad.getValue();
            String descripcion = txtDescripcion.getText();
            String estado = cmbEstado.getValue();

            if (estado == null || estado.isEmpty()) {

                mostrarAlerta("Validación", "Selecciona un estado válido.");
                return;

            }

            if (propiedad == null) {

                Propiedad nueva = new Propiedad(
                        nombre, direccion, ciudad, pais,
                        precio, capacidad, descripcion, estado
                );

                propiedadDAO.agregarPropiedad(nueva);

            } else {

                propiedad.setNombre(nombre);
                propiedad.setDireccion(direccion);
                propiedad.setCiudad(ciudad);
                propiedad.setPais(pais);
                propiedad.setPrecio_noche(precio);
                propiedad.setCapacidad(capacidad);
                propiedad.setDescripcion(descripcion);
                propiedad.setEstado_propiedad(estado);
                propiedadDAO.modificarPropiedadPorId(propiedad);

            }

            if (propiedadController != null) { propiedadController.actualizarTabla(); }

            cerrarVentana();

        } catch (NumberFormatException e) {

            mostrarAlerta("Validación", "Revisa los campos numéricos. El precio por noche debe ser decimal.");

        } catch (IllegalArgumentException e) {

            mostrarAlerta("Validación", e.getMessage());

        }
    }

    /**
     * Cierra la ventana sin guardar cambios.
     */
    @FXML
    void cancelar() {

        cerrarVentana();

    }

    /**
     * Cierra la ventana del formulario.
     */
    private void cerrarVentana() {

        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();

    }

    /**
     * Muestra una alerta de advertencia o validación al usuario.
     *
     * @param titulo Título de la ventana de alerta.
     * @param mensaje Contenido textual del mensaje.
     */
    private void mostrarAlerta(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();

    }

    /**
     * Configura el formulario en modo edición o creación según la propiedad indicada.
     * Si se recibe una propiedad, los campos se rellenan con sus datos.
     *
     * @param propiedad Objeto {@link Propiedad} a editar, o {@code null} para crear una nueva.
     */
    public void setPropiedad(Propiedad propiedad) {

        this.propiedad = propiedad;

        if (propiedad != null) {

            lblTitulo.setText("MODIFICAR PROPIEDAD");
            txtNombre.setText(propiedad.getNombre());
            txtDireccion.setText(propiedad.getDireccion());
            txtCiudad.setText(propiedad.getCiudad());
            txtPais.setText(propiedad.getPais());
            txtPrecioNoche.setText(String.valueOf(propiedad.getPrecio_noche()));

            if (propiedad.getCapacidad() != 0) {

                spnCapacidad.getValueFactory().setValue(propiedad.getCapacidad());

            }

            txtDescripcion.setText(propiedad.getDescripcion());
            cmbEstado.setValue(propiedad.getEstado_propiedad());

        } else { lblTitulo.setText("AÑADIR PROPIEDAD"); }
    }

    /**
     * Define el controlador principal de propiedades, para permitir la actualización
     * de la tabla tras guardar o modificar una propiedad.
     *
     * @param propiedadController instancia del Objeto {@link PropiedadController}.
     */
    public void setPropiedadController(PropiedadController propiedadController) {

        this.propiedadController = propiedadController;

    }
}
