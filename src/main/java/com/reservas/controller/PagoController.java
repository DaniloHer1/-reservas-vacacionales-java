package com.reservas.controller;

import com.reservas.Main;
import com.reservas.config.DataBaseConnection;
import com.reservas.dao.PagoDAO;
import com.reservas.model.Cliente;
import com.reservas.model.Pago;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

import static com.reservas.controller.MainController.cargarVista;
import static com.reservas.controller.MainController.mostrarAlerta;

/**
 * Controlador de la vista de gestión de pagos
 * Gestión la interacción entre la interfaz JavaFX y la capa DAO
 *
 * @author Daniel Hernando
 * @since 31/10/2025
 */
public class PagoController {

    @FXML
    private TableView<Pago> table;

    @FXML
    private TableColumn<Pago, Integer> colId;

    @FXML
    private TableColumn<Pago, String> colReserva;

    @FXML
    private TableColumn<Pago, String> colFecha;

    @FXML
    private TableColumn<Pago, String> colMonto;

    @FXML
    private TableColumn<Pago, Pago.MetodoPago> colMetodo;

    @FXML
    private TableColumn<Pago, Pago.EstadoPago> colEstado;

    @FXML
    private TextField txtBusquedaId;

    @FXML
    private Label lblTotalPagos;


    private PagoDAO pagoDAO;

    /**
     * Inicialización de la vista y carga de datos de los pagos
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
     * Configuración de las columnas de la tabla para enlazarlas con las propiedades del modelo Pago
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
     * Método encargado de cargar los pagos desde la base de datos para mostrarlos
     * en la tabla.
     */
    public void cargarListaPagos() {
        pagoDAO.mostrarTodosPagos();
        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(
                pagoDAO.getPagosDisponibles()
        );
        table.setItems(listaPagos);
    }

    /**
     * Método que permite abrir la ventana {@code pagos-form-view.fxml}, gestionada por
     * {@link PagoController} para crear un nuevo pago.
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
     * Comprobación de selección de un pago para posteriormente llamar al método
     * abrirFormularioEdicion() al que se le pasa el objeto Pago seleccionado.
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
     * Método encargado de abrir la ventana {@code pagos-form-view.fxml}, gestionada por
     * {@link PagoController} para modificar un pago seleccionado.
     *
     * @param pago
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
     * Eliminación del pago seleccionado después de confirmar la acción.
     * Comprobación de selección de un pago.
     */
    @FXML
    public void eliminarPagoSeleccionado() {
        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para poder eliminarlo.", Alert.AlertType.ERROR);
            return;
        }

        boolean confirmacion = mostrarConfirmacionBorrado("Confirmación", "¿Seguro que deseas eliminar el pago " + pagoSeleccionado.getId() + "?");

        if (confirmacion) {
            boolean eliminado = pagoDAO.borrarPago(pagoSeleccionado);
            cargarListaPagos();

            if (eliminado) {
                mostrarAlerta("Éxito", "El pago " + pagoSeleccionado.getId() + " ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se ha podido eliminar el pago.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Método que recarga los datos en la tabla de pagos.
     */
    @FXML
    private void actualizarTablaPagos() {

        cargarListaPagos();

    }

    /**
     * Método que se encarga de mostrar un diálogo de confirmación para eliminar un pago.
     * @return true si el usuario confirma la eliminación, false en caso contrario.
     *
     * @param titulo texto que aparecerá en la barra de título de la alerta.
     * @param mensaje contenido principal del mensaje a mostrar en el cuadro de diálogo.
     */
    private boolean mostrarConfirmacionBorrado(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent();
    }

    /**
     * Método que permite abrir la ventana {@code pagos-form-view.fxml}, gestionada por {@link PagoController}
     * haciendo doble click sobre la tupla del cliente que se quiere modificar.
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
     * Método para buscar un pago por ID y muestra únicamente ese resultado en la tabla
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
            }else{
                mostrarAlerta("Sin Resultado","No se ha encontrado ningún pago con el ID:  " +idTexto, Alert.AlertType.INFORMATION);
                table.setItems(FXCollections.observableArrayList());
            }

        }catch (Exception e){
            mostrarAlerta("ERROR","El ID debe ser un número", Alert.AlertType.ERROR);
        }

    }
    /**
     * Método encargado de mostrar todos los pagos en la tabla de pagos
     */
    @FXML
    public void mostrarTodosPagos() {
        txtBusquedaId.clear();
        cargarListaPagos();
    }

    /**
     * Actualización del Label que muestra el total de pagos
     */
    private void actualizarTotalPagos() {

        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(pagoDAO.getPagosDisponibles());

        if (lblTotalPagos != null) {

            int total = listaPagos.size();
            lblTotalPagos.setText("Total de Pagos: " + total);

        }
    }

}
