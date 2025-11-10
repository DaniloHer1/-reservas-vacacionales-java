package com.reservas.controller;

import com.reservas.Main;
import com.reservas.config.DataBaseConnection;
import com.reservas.dao.PagoDAO;
import com.reservas.model.Pago;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

import static com.reservas.controller.MainController.mostrarAlerta;


/**
 * <h1>Controlador de la vista de gestión de pagos.</h1>
 * <p>
 * Gestiona la interacción entre la interfaz JavaFX y la capa DAO para realizar operaciones
 * de lectura, búsqueda, creación, edición y eliminación de registros de pagos.
 * </p>
 *
 * <h2>Funciones principales:</h2>
 * <ul>
 *     <li>Carga inicial de todos los pagos desde la base de datos.</li>
 *     <li>Búsqueda por ID de pago.</li>
 *     <li>Apertura de formularios de edición o creación.</li>
 *     <li>Eliminación de registros con confirmación.</li>
 *     <li>Actualización del contador de pagos totales.</li>
 * </ul>
 *
 * @author Daniel Hernando
 * @since 31/10/2025
 */
public class PagoController {

    @FXML private TableView<Pago> table;

    @FXML private TableColumn<Pago, Integer> colId;
    @FXML private TableColumn<Pago, String> colReserva;
    @FXML private TableColumn<Pago, String> colFecha;
    @FXML private TableColumn<Pago, String> colMonto;
    @FXML private TableColumn<Pago, Pago.MetodoPago> colMetodo;
    @FXML private TableColumn<Pago, Pago.EstadoPago> colEstado;

    @FXML private TextField txtBusquedaId;

    @FXML private Label lblTotalPagos;

    private PagoDAO pagoDAO;

    /**
     * Inicializa la vista de pagos y carga la información desde la base de datos.
     * Configura las columnas, el doble clic para editar, y muestra el total de registros.
     */
    @FXML
    public void initialize() {

        Connection connection = DataBaseConnection.getInstance().conectarBD();
        pagoDAO = new PagoDAO(connection);

        configurarColumnasTabla();
        cargarListaPagos();
        configurarDobleClickFila();
        mostrarTodosPagos();
        actualizarTotalPagos();

    }

    /**
     * Configura las columnas de la tabla enlazándolas con las propiedades del modelo {@link Pago}.
     */
    private void configurarColumnasTabla() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReserva.setCellValueFactory(new PropertyValueFactory<>("reserva"));
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaTexto())
        );

        colMonto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMontoMostrar())
        );

        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }

    /**
     * Carga todos los pagos desde la base de datos y los muestra en la tabla principal.
     */
    public void cargarListaPagos() {

        pagoDAO.mostrarTodosPagos();
        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(
                pagoDAO.getPagosDisponibles()
        );

        table.setItems(listaPagos);

    }

    /**
     * Abre el formulario para registrar un nuevo pago.
     */
    @FXML
    public void abrirFormularioNuevoPago() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("AÑADIR PAGO");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comprueba si hay un pago seleccionado para edición y, en caso afirmativo,
     * abre el formulario de edición con sus datos precargados.
     */
    @FXML
    public void abrirFormularioEditarPago() {

        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para poder editarlo.", Alert.AlertType.ERROR);
            return;
        }

        abrirFormularioEdicion(pagoSeleccionado);

    }

    /**
     * Abre el formulario de edición del pago seleccionado.
     *
     * @param pago el pago a editar.
     */
    private void abrirFormularioEdicion(Pago pago) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PagoFormController pagoFormController = fxmlLoader.getController();
            pagoFormController.cargarPagoParaEditar(pago, null);

            Stage stage = new Stage();
            stage.setTitle("EDITAR PAGO");
            stage.setScene(scene);
            stage.show();

            // Opcional: recargar la tabla cuando se cierre la ventana
            stage.setOnHidden(e -> cargarListaPagos());

        } catch (IOException e) {

            e.printStackTrace();
            mostrarAlerta("Error", "No se ha podido abrir el formulario de edición", Alert.AlertType.ERROR);

        }
    }


    /**
     * Elimina el pago seleccionado tras confirmar la acción con el usuario.
     */
    @FXML
    public void eliminarPagoSeleccionado() {

        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para poder eliminarlo.", Alert.AlertType.ERROR);
            return;
        }

        boolean confirmacion = mostrarConfirmacionBorrado("Confirmación", "¿Seguro que deseas eliminar el pago "
                + pagoSeleccionado.getId() + "?");

        if (confirmacion) {

            boolean eliminado = pagoDAO.borrarPago(pagoSeleccionado);
            cargarListaPagos();

            if (eliminado) {

                mostrarAlerta("Éxito", "El pago " + pagoSeleccionado.getId() + " ha sido eliminado correctamente.",
                        Alert.AlertType.INFORMATION);

            } else {
                mostrarAlerta("Error", "No se ha podido eliminar el pago.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Actualiza la tabla recargando los datos desde la base de datos.
     */
    @FXML
    private void actualizarTablaPagos() {

        cargarListaPagos();

    }

    /**
     * Muestra un cuadro de confirmación antes de eliminar un registro.
     *
     * @param titulo   título de la alerta.
     * @param mensaje  mensaje a mostrar.
     * @return {@code true} si el usuario confirma, {@code false} en caso contrario.
     */
    private boolean mostrarConfirmacionBorrado(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        return alert.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent();

    }

    /**
     * Configura el evento de doble clic sobre una fila para abrir el formulario de edición del pago.
     */
    private void configurarDobleClickFila() {

        table.setRowFactory(tv -> {

            TableRow<Pago> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY &&
                        event.getClickCount() == 2 &&
                        !row.isEmpty()) {

                    Pago pagoSeleccionado = row.getItem();
                    abrirFormularioEdicion(pagoSeleccionado);

                }

            });

            return row;
        });
    }

    /**
     * Busca un pago por su ID e imprime el resultado en la tabla.
     */
    @FXML
    public void buscarPorID(){

        String idTexto=txtBusquedaId.getText();

        if (idTexto.isEmpty()) {
            mostrarAlerta("Validación", "Debes ingresar un ID para buscar", Alert.AlertType.WARNING);
            return;
        }

        try {

            int id = Integer.parseInt(idTexto);

            Pago pagoEncontrado= pagoDAO.buscarPagoPorId(id);

            if (pagoEncontrado!=null){

                ObservableList<Pago> resultado=FXCollections.observableArrayList(pagoEncontrado);
                table.setItems(resultado);

            } else{

                mostrarAlerta("Sin Resultado","No se ha encontrado ningún pago con el ID:  " +idTexto, Alert.AlertType.INFORMATION);
                table.setItems(FXCollections.observableArrayList());

            }

        } catch (Exception e){

            mostrarAlerta("ERROR","El ID debe ser un número", Alert.AlertType.ERROR);

        }

    }

    /**
     * Muestra todos los pagos disponibles en la base de datos.
     */
    @FXML
    public void mostrarTodosPagos() {

        txtBusquedaId.clear();
        cargarListaPagos();

    }

    /**
     * Actualiza la etiqueta inferior que muestra el número total de pagos cargados.
     */
    private void actualizarTotalPagos() {

        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(pagoDAO.getPagosDisponibles());

        if (lblTotalPagos != null) {

            int total = listaPagos.size();
            lblTotalPagos.setText("Total de Pagos: " + total);

        }
    }

}
