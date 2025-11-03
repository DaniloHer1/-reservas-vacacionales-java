package com.reservas.controller;

import com.reservas.dao.ClienteDAO;
import com.reservas.model.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para la ventana de creación de un nuevo cliente.
 * Realiza validaciones básicas y comunica los datos con la capa DAO.
 *
 * @author Jaime
 * @since 31/10/2025
 */
public class ClienteFormController{

    @FXML private Label lblTitulo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtPais;
    @FXML private TextField txtFecha;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean modoEditar = false;
    private Cliente clienteOriginal;

    @FXML
    public void initialize() {

        LocalDate hoy = LocalDate.now();
        txtFecha.setText(hoy.format(FECHA));
        txtFecha.setEditable(false);

    }

    /**
     * Cambiará la etiqueta del título según el modo en el que se encuentre, modo edición o modo añadir.
     * @param titulo texto que se mostrará en la etiqueta del título del formulario.
     */
    public void setTitulo(String titulo) {

        lblTitulo.setText(titulo);

    }

    /**
     * Guarda un nuevo cliente o actualiza uno existente en la base de datos, aplicando validaciones previas.
     * Si la vista se encuentra en modo creación ({@code !modoEditar}), se valida que el correo no exista ya en la base de datos.
     * En caso de estar en modo edición, se comprueba que el nuevo correo no pertenezca a otro cliente distinto.
     */
    @FXML
    private void guardarCliente() {

        try {

            if (txtNombre.getText().isBlank() || txtApellidos.getText().isBlank() || txtEmail.getText().isBlank()) {

                mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios", "Por favor, completa todos los campos requeridos.");

                return;

            }

            if(!modoEditar) { // Modo crear cliente

                int idExistente = clienteDAO.buscarClientePorEmail(txtEmail.getText().strip());

                if (clienteDAO.buscarClientePorEmail(txtEmail.getText().strip()) != -1) {

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Duplicado","Ya existe un cliente con ese email.");

                    return;

                }

                Cliente cliente = new Cliente( txtNombre.getText().strip(), txtApellidos.getText().strip(), txtEmail.getText().strip(), txtTelefono.getText().strip(), txtPais.getText().strip());

                var hoy = LocalDate.now();
                cliente.setFechaRegistro(hoy);
                txtFecha.setText(hoy.format(FECHA));

                if (clienteDAO.agregarCliente(cliente)) {

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente agregado correctamente.");

                    cerrarVentana();

                } else {

                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo agregar el cliente. Intenta de nuevo.");

                }

            } else { // Modo modificar cliente

                int idActual = clienteOriginal.getIdCliente();
                int idConEseEmail = clienteDAO.buscarClientePorEmail(txtEmail.getText().strip());

                if (idConEseEmail != -1 && idConEseEmail != idActual) {

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Duplicado","Ese email pertenece a otro cliente");

                    return;

                }

                Cliente editado = new Cliente(txtNombre.getText().strip(), txtApellidos.getText().strip(), txtEmail.getText().strip(), txtTelefono.getText().strip(), txtPais.getText().strip());
                editado.setIdCliente(idActual);

                var f = clienteOriginal.getFechaRegistro();

                if (f != null) {

                    editado.setFechaRegistro(f);

                }

                boolean modificado = clienteDAO.modificarClientePorId(editado);

                if (modificado) {

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente actualizado", "Cliente actualizado correctamente");
                    cerrarVentana();

                } else {

                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el cliente");

                }

            }

        } catch (IllegalArgumentException e) {

            mostrarAlerta(Alert.AlertType.ERROR, "Validación fallida", e.getMessage());

        } catch (Exception e) {

            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", e.getMessage());
            e.printStackTrace();

        }
    }

    /**
     * Carga los datos de un cliente existente en los campos del formulario para su edición.
     * @param cliente Objeto {@link Cliente} cuyos datos se mostrarán en el formulario.
     */
    @FXML
    public void cargarCliente(Cliente cliente) {

        if (cliente == null) {

            return;

        }

        modoEditar = true;
        clienteOriginal = cliente;

        txtNombre.setText(cliente.getNombre());
        txtApellidos.setText(cliente.getApellido());
        txtEmail.setText(cliente.getEmail());
        txtTelefono.setText(cliente.getTelefono());
        txtPais.setText(cliente.getPais());


        if (cliente.getFechaRegistro() != null) {

            txtFecha.setText(cliente.getFechaRegistro().format(FECHA));

        } else {

            LocalDate hoy = LocalDate.now();
            txtFecha.setText(hoy.format(FECHA));
            cliente.setFechaRegistro(hoy);

        }

    }

    /**
     * Cierra la ventana del formulario actual sin guardar los cambios realizados.
     */
    @FXML
    private void cerrarFormulario() {

        cerrarVentana();

    }

    /**
     *  Cierra la ventana (escena) actual asociada al formulario de cliente.
     *  Obtiene la ventana desde el componente {@code txtNombre} y ejecuta su método {@code close()},
     *  finalizando el formulario.
     */
    private void cerrarVentana() {

        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();

    }

    /**
     * Muestra una alerta en la interfaz con el tipo, título y mensaje especificados.
     *
     * @param tipo  tipo de alerta a mostrar.
     * @param titulo texto que aparecerá en la barra de título de la ventana de alerta.
     * @param mensaje contenido principal del mensaje que se mostrará al usuario.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
}
