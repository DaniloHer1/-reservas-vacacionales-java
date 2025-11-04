package com.reservas.controller;

import com.reservas.Main;
import com.reservas.dao.ReservaDAO;
import com.reservas.model.Reserva;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ReservaControler {
    ReservaDAO reservaDAO;
    @FXML
    TableView<Reserva> tableView;
    @FXML
    TableColumn<Reserva, Integer> colID;
    @FXML
    TableColumn<Reserva, Integer> colID_cliente;
    @FXML
    TableColumn<Reserva, Integer> colID_prop;
    @FXML
    TableColumn<Reserva, String> colFechaIni;
    @FXML
    TableColumn<Reserva, String> colFechaFin;
    @FXML
    TableColumn<Reserva, Integer> colNumPersonas;
    @FXML
    TableColumn<Reserva, Reserva.EstadoReserva> colEstado;
    @FXML
    TableColumn<Reserva, Double> colPrecio;
    @FXML
    TableColumn<Reserva, String> colMotivo;
    ObservableList<Reserva> reservas;
    @FXML
    public void initialize(){
        reservaDAO = new ReservaDAO();
        reservas = FXCollections.observableArrayList(reservaDAO.getReservas());
        configurarColumnas();
        tableView.setItems(reservas);
    }
    private void configurarColumnas() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id_reserva"));
        colID_cliente.setCellValueFactory(new PropertyValueFactory<>("id_cliente"));
        colID_prop.setCellValueFactory(new PropertyValueFactory<>("id_propiedad"));
        colFechaIni.setCellValueFactory(reservaDateCellDataFeatures ->
                new SimpleStringProperty(reservaDateCellDataFeatures.getValue().getFechaIniString()));
        colFechaFin.setCellValueFactory(reservaDateCellDataFeatures ->
                new SimpleStringProperty(reservaDateCellDataFeatures.getValue().getFechaFinString()));
        colNumPersonas.setCellValueFactory(new PropertyValueFactory<>("num_personas"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoReserva"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio_total"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo_cancelacion"));
    }
    public void aniadirReservaForm(){
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("reservas-form-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ReservaFormController formController = loader.getController();
        formController.modoEditar = false;
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Añadir Reserva");
        stage.show();
    }
    public void eliminarReserva(){
        if((tableView.getSelectionModel().getSelectedItem()) == null){
            MainController.mostrarAlerta("Selecciona una reserva", "Por favor, selecciona una reserva para eliminarla", Alert.AlertType.WARNING);
        }else{
            if (reservaDAO.eliminarReserva(tableView.getSelectionModel().getSelectedItem())==1){
                MainController.mostrarAlerta("Reserva eliminada", "Reserva eliminada correctamente", Alert.AlertType.INFORMATION);
            }else {
                MainController.mostrarAlerta("Error", "La reserva seleccionada no existe. Por favor, actualiza la tabla.", Alert.AlertType.ERROR);
            }
        }
    }
    public void editarReservaForm(){
        if (tableView.getSelectionModel().getSelectedItem() == null){
            MainController.mostrarAlerta("Selecciona una reserva", "Por favor, selecciona una reserva para modificarla.", Alert.AlertType.WARNING);
        }else {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("reservas-form-view.fxml"));
            Scene scene;
            try {
                scene = new Scene(loader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ReservaFormController formController = loader.getController();
            formController.setModoEditar(true, tableView.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Reserva");
            stage.show();
        }
    }


}
