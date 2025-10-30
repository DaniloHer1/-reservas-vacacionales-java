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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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



    private PagoDAO sacarDatos;

    @FXML
    public void initialize() {
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        sacarDatos=new PagoDAO(connection);
        configurarColumnas();
        cargarPagos();
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReserva.setCellValueFactory(new PropertyValueFactory<>("reserva"));
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaTexto())
        );
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }
    public void cargarPagos(){

        sacarDatos.mostrarTodosPagos();
        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(
                sacarDatos.getPagosDisponibles()
        );
        table.setItems(listaPagos);


    }
    @FXML
    public void añadirPago() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.getStackTrace();
        }

        Stage stage =new Stage();

        stage.setTitle("Añadir Pago");
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    public void editarPago() {

        Pago pago=table.getSelectionModel().getSelectedItem();

        if (pago==null){
            mostrarAlerta("Error","Debe selecionar un pago si quiere actualizarlo", Alert.AlertType.ERROR);
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene =  new Scene(fxmlLoader.load());

            PagoFormController pagoController=fxmlLoader.getController();

            pagoController.guardarPago(pago);
            Stage stage =new Stage();

            stage.setTitle("Añadir Pago");
            stage.setScene(scene);
            stage.show();
            cargarPagos();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void borrarPago(){
        Pago pago=table.getSelectionModel().getSelectedItem();
        if (pago==null){
            mostrarAlerta("Error","Debe selecionar un pago si quiere eliminarlo", Alert.AlertType.ERROR);
            return;
        }
        boolean confirmacion= mostrarAlertaBorrar("Confirmacion","Seguro que quieres borrar el pago "+pago.getId());
        if (confirmacion){
            boolean eliminado = sacarDatos.borrarPago(pago);
            cargarPagos();
            if (eliminado){
                mostrarAlerta("Exito","Pago "+ pago.getId() + " Borrado", Alert.AlertType.INFORMATION);
            }else {
                mostrarAlerta("Error","No se puede eliminar el pago", Alert.AlertType.ERROR);
            }
        }

    }

    @FXML
    private void actualizarTabla(){
      cargarPagos();
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean mostrarAlertaBorrar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return  alert.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent();
    }

}
