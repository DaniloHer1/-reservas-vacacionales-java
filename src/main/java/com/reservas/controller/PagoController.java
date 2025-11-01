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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

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
    private TableColumn<Pago, Double> colMonto;

    @FXML
    private TableColumn<Pago, Pago.MetodoPago> colMetodo;

    @FXML
    private TableColumn<Pago, Pago.EstadoPago> colEstado;

    private PagoDAO pagoDAO;

    @FXML
    public void initialize() {
        // Conecto con la base de datos y preparo el DAO
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        pagoDAO = new PagoDAO(connection);

        // Configuro las columnas y cargo los pagos en la tabla
        configurarColumnasTabla();
        cargarListaPagos();
    }

    /**
     * Configura las columnas de la tabla de pagos.
     */
    private void configurarColumnasTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReserva.setCellValueFactory(new PropertyValueFactory<>("reserva"));
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaTexto())
        );
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }

    /**
     * Carga los pagos desde la base de datos y los muestra en la tabla.
     */
    public void cargarListaPagos() {
        pagoDAO.mostrarTodosPagos();
        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(
                pagoDAO.getPagosDisponibles()
        );
        table.setItems(listaPagos);
    }

    /**
     * Abre el formulario para añadir un nuevo pago.
     */
    @FXML
    public void abrirFormularioNuevoPago() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Añadir Pago");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre el formulario de edición con los datos del pago seleccionado.
     */
    @FXML
    public void abrirFormularioEditarPago() {
        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para poder editarlo.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PagoFormController pagoFormController = fxmlLoader.getController();
            pagoFormController.cargarPagoParaEditar(pagoSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Pago");
            stage.setScene(scene);
            stage.show();

            cargarListaPagos();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Elimina el pago seleccionado después de confirmar la acción.
     */
    @FXML
    public void eliminarPagoSeleccionado() {
        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para eliminarlo.", Alert.AlertType.ERROR);
            return;
        }

        boolean confirmacion = mostrarConfirmacionBorrado("Confirmación", "¿Seguro que deseas eliminar el pago " + pagoSeleccionado.getId() + "?");

        if (confirmacion) {
            boolean eliminado = pagoDAO.borrarPago(pagoSeleccionado);
            cargarListaPagos();

            if (eliminado) {
                mostrarAlerta("Éxito", "El pago " + pagoSeleccionado.getId() + " fue eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el pago.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Refresca los datos de la tabla de pagos.
     */
    @FXML
    private void actualizarTablaPagos() {
        cargarListaPagos();
    }

    /**
     * Muestra un mensaje de alerta genérico.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación para eliminar un pago.
     * @return true si el usuario confirma, false en caso contrario.
     */
    private boolean mostrarConfirmacionBorrado(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent();
    }

}
