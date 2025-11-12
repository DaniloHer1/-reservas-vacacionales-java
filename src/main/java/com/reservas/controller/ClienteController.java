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
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Controlador de Clientes</h1>
 * Controlador principal encargado de gestionar la vista de <b>Clientes</b> dentro del sistema de reservas.<br>
 * Se encarga de coordinar la comunicación entre la interfaz JavaFX y la capa de datos {@link ClienteDAO},
 * permitiendo administrar las operaciones CRUD sobre los clientes registrados.
 *
 * <h2>Funciones principales:</h2>
 * <ul>
 *     <li>Cargar y mostrar los clientes en la tabla.</li>
 *     <li>Buscar clientes por su ID.</li>
 *     <li>Crear, editar o eliminar clientes existentes.</li>
 *     <li>Refrescar los datos desde la base de datos.</li>
 * </ul>
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
     * Inicializa la vista de clientes, configurando las columnas,
     * los eventos de interacción y la carga inicial de los datos desde la base de datos.
     */
    @FXML
    public void initialize() {

        configurarColumnas();
        configurarDobleClickFila();
        cargarClientes();

    }

    /**
     * Configura las columnas de la tabla, vinculándolas con las propiedades del
     * modelo {@link Cliente}.
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

                } else { setText(item.format(FECHA_HORA)); }

            }

        });

    }

    /**
     * Configura el evento de doble clic sobre una fila para abrir el formulario de edición
     * del cliente seleccionado.
     * <p>
     * {@code clientes-form-view.fxml},gestionada por {@link ClienteController}
     * </p>
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
     * Carga todos los clientes desde la base de datos en la tabla.
     */
    public void cargarClientes() {

        listaClientes.clear();
        List<Cliente> clientes = clienteDAO.leerClientes();
        listaClientes.addAll(clientes);
        tableClientes.setItems(listaClientes);
        actualizarTotalClientes();

    }

    /**
     * Refresca la tabla con los datos actuales de la base de datos.
     *
     * @param event evento disparado al hacer clic en el botón "ACTUALIZAR 🔁".
     */
    @FXML
    private void refrescarTabla(ActionEvent event) {

        cargarClientes();

    }

    /**
     * Manejo del evento generado al pulsar el botón Añadir en la vista principal.
     *
     * @param event evento de acción disparado al hacer clic en el botón "AÑADIR ➕".
     */
    @FXML
    private void guardarCliente(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/clientes-form-view.fxml"));
            Parent root = loader.load();

            ClienteFormController controller = loader.getController();
            controller.setTitulo("Nuevo Cliente");

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión Clientes");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/IconoPrincipal.png"))));
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
     * @param event evento de acción disparado al hacer clic en el botón "MODIFICAR ✔".
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
            stage.setTitle("Gestión Clientes");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/IconoPrincipal.png"))));
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
     * @param event evento de acción disparado al hacer clic en el botón "ELIMINAR ✖".
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
     * @param event evento generado al pulsar el botón de búsqueda (🔍).
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
                    "Pulsa «ACTUALIZAR» si quieres cargarlo.").show();

        }

    }

    /**
     * Actualiza la etiqueta con el número total de clientes cargados.
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
     * Muestra una alerta en pantalla con el tipo, título y mensaje especificados.
     *
     * @param tipo tipo de alerta (información, error, advertencia, etc.).
     * @param titulo texto del título de la alerta.
     * @param mensaje mensaje principal del cuadro de diálogo.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }

}
