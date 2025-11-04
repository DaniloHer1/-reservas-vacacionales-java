package com.reservas.controller;

import com.reservas.dao.PropiedadDAO;
import com.reservas.model.Propiedad;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador para la ventana de creación o edición de propiedades.
 * Realiza validaciones básicas y comunica los datos con la capa DAO.
 *
 * @author Diego
 * @since 04/11/2025
 */
public class PropiedadFormController {

    @FXML private Label lblTitulo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private TextField txtPrecioNoche;
    @FXML private Spinner<Integer> spnCapacidad; // <-- ahora Spinner, igual que en el FXML
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private Propiedad propiedad;
    private PropiedadController propiedadController;

    @FXML
    public void initialize() {
        cmbEstado.getItems().addAll("disponible", "ocupada", "mantenimiento");
        // Configura el spinner (rango razonable)
        SpinnerValueFactory<Integer> vf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        spnCapacidad.setValueFactory(vf);
    }

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

            if (propiedadController != null) {
                propiedadController.actualizarTabla();
            }
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "Revisa los campos numéricos: Precio por noche debe ser decimal.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Validación", e.getMessage());
        }
    }

    @FXML
    void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
        if (propiedad != null) {
            lblTitulo.setText("Editar Propiedad");
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
        } else {
            lblTitulo.setText("Nueva Propiedad");
            // valores por defecto ya están en initialize()
        }
    }

    public void setPropiedadController(PropiedadController propiedadController) {
        this.propiedadController = propiedadController;
    }
}
