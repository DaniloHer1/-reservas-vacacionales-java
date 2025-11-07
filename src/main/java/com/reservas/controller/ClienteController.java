package com.reservas.controller;

import com.reservas.dao.ClienteDAO;
import com.reservas.model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador de la vista de gestión de clientes
 * Gestión de la interacción entre la interfaz JavaFX y la capa DAO
 *
 * @author Jaime Pérez
 * @since 31/10/2025
 */
public class ClienteController {

    @FXML private TableView<Cliente> tableClientes;

    @FXML private TableColumn<Cliente, Integer> colIdCliente;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colPais;
    @FXML private TableColumn<Cliente, LocalDate> colFechaRegistro;

    @FXML private TextField txtBusquedaEmail;
    @FXML private Label lblTotalClientes;

    private final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    /**
     * Inicialización de la vista y carga de los datos de los clientes
     */
    @FXML
    public void initialize() {

        configurarColumnas();
        configurarDobleClickFila();
        cargarClientes();

    }

    /**
     * Configuración de las columnas de la tabla para enlazarlas con las propiedades del
     * modelo Cliente
     */
    public void configurarColumnas() {

        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colFechaRegistro.setCellFactory(col -> new TableCell<Cliente, LocalDate>() {

            protected void updateItem(LocalDate item, boolean empty) {

                super.updateItem(item, empty);
                if (empty || item == null) {

                    setText(null);

                } else {

                    setText(item.format(FECHA_HORA));

                }

            }

        });

    }

    /**
     * Método el cual permite abrir la ventana {@code clientes-form-view.fxml},
     * gestionada por {@link ClienteController} haciendo doble click sobre la tupla del
     * cliente que se quiere modificar.
     */
    private void configurarDobleClickFila() {

        tableClientes.setRowFactory(tv -> {

            TableRow<Cliente> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Cliente clienteSeleccionado = row.getItem();

                    modificarCliente(new ActionEvent());

                }

            });

            return row;

        });

    }

    /**
     * Método para cargar todos los clientes desde la base de datos en la tabla.
     */
    public void cargarClientes() {

        listaClientes.clear();
        List<Cliente> clientes = clienteDAO.leerClientes();
        listaClientes.addAll(clientes);
        tableClientes.setItems(listaClientes);
        actualizarTotalClientes();

    }

    /**
     * Método que se encarga de refrescar la tabla con los datos actuales de la base de
     * datos.
     */
    @FXML
    private void refrescarTabla(ActionEvent event) {

        cargarClientes();

    }

    /**
     * Manejo del evento generado al pulsar el botón Añadir en la vista principal.
     *
     * @param event evento de acción disparado al hacer clic en el botón Añadir.
     */
    @FXML
    private void guardarCliente(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/clientes-form-view.fxml"));
            Parent root = loader.load();

            ClienteFormController controller = loader.getController();
            controller.setTitulo("Nuevo Cliente");

            Stage stage = new Stage();
            stage.setTitle("Nuevo Cliente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarClientes();

        } catch (IOException e) {

            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se ha podido abrir el formulario de cliente.").show();

        }

    }

    /**
     * Manejo del evento generado al pulsar el botón Modificar en la vista principal comprobando
     * la selección del cliente a modificar.
     *
     * @param event evento de acción disparado al hacer clic en el botón Modificar.
     */
    @FXML
    private void modificarCliente(ActionEvent event) {

        Cliente clienteSeleccionado = tableClientes.getSelectionModel().getSelectedItem();

        if (clienteSeleccionado == null) {

            new Alert(Alert.AlertType.ERROR, "Seleccione un cliente para modificar", ButtonType.OK).show();

            return;

        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/clientes-form-view.fxml"));
            Parent root = loader.load();

            ClienteFormController controller = loader.getController();
            controller.setTitulo("Modificar Cliente");
            controller.cargarCliente(clienteSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Modificar Cliente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarClientes();

        } catch (IOException e) {

            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se ha podido abrir el formulario de cliente.").show();

        }

    }

    /**
     * Manejo del evento generado al pulsar el botón Eliminar en la vista principal comprobando
     * la selección del cliente a modificar.
     *
     * @param event evento de acción disparado al hacer clic en el botón Eliminar.
     */
    @FXML
    private void eliminarCliente(ActionEvent event) {

        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            new Alert(Alert.AlertType.ERROR, "Seleccione un cliente para eliminar", ButtonType.OK).show();

            return;

        }

        String nombreCompleto = seleccionado.getNombre() + " " + seleccionado.getApellido();

        Alert confirmarEliminar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmarEliminar.setTitle("Confirmar eliminación");
        confirmarEliminar.setHeaderText("¿Desea eliminar el Cliente?");
        confirmarEliminar.setContentText(
                "¿Estás seguro de que quieres eliminar al cliente?\n\n" +
                        "Nombre: " + nombreCompleto + "\n" +
                        "Email: "  + seleccionado.getEmail()
        );

        ButtonType btnSi = new ButtonType("Si", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmarEliminar.getButtonTypes().setAll(btnSi, btnNo);

        var respuesta = confirmarEliminar.showAndWait();

        if(respuesta.get() == btnSi) {

            try {

                clienteDAO.eliminarCliente(seleccionado);
                cargarClientes();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "El cliente \"" + nombreCompleto + "\" ha sido eliminado.");

            } catch (RuntimeException e) {

                mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar", e.getMessage());

            }

        }

    }

    /**
     * Método encargado de la búsqueda de un cliente.
     * Si se encuentra un cliente, la tabla se posiciona y selecciona en la fila correspondiente.
     * En caso contrario, se muestra una alerta informativa.
     *
     * @param event evento de acción disparado al darle al botón de búsqueda que tiene como símbolo una lupa ( 🔍 ).
     */
    @FXML
    private void buscarPorEmail(ActionEvent event) {

        String email = txtBusquedaEmail.getText().strip();

        if (email.isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Introduzca un email.").show();
            return;

        }

        int id = clienteDAO.buscarClientePorEmail(email);

        if (id == -1) {

            new Alert(Alert.AlertType.INFORMATION,"No se ha encontrado ningún cliente con el email: " + email).show();
            return;

        }

        int index = -1;

        for (int i = 0; i < listaClientes.size(); i++) {

            if (listaClientes.get(i).getIdCliente() == id) {

                index = i;

                break;

            }
        }

        if (index >= 0) {

            tableClientes.getSelectionModel().clearSelection();
            tableClientes.getSelectionModel().select(index);
            tableClientes.scrollTo(index);

        } else {

            new Alert(Alert.AlertType.INFORMATION,"El cliente existe en la base de datos,\n" + "pero no está en la lista visible. " +
                    "Pulsa «Refrescar» si quieres cargarlo.").show();

        }

    }

    /**
     * Actualización de la etiqueta de la interfaz con el número total de clientes cargados en la tabla.
     * Obtiene el tamaño de la lista {@code listaClientes} y establece el texto del
     * componente {@code lblTotalClientes} para mostrar el total actual al usuario.
     */
    private void actualizarTotalClientes() {

        if (lblTotalClientes != null) {

            int total = listaClientes.size();
            lblTotalClientes.setText("Total clientes: " + total);

        }
    }

    /**
     * Método que muestra una alerta emergente en la interfaz con el tipo, título y mensaje especificados.
     *
     * @param tipo  tipo de alerta a mostrar.
     * @param titulo texto que aparecerá en la barra de título de la alerta.
     * @param mensaje contenido principal del mensaje a mostrar en el cuadro de diálogo.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }

}
