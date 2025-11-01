package com.reservas.controller;

import com.reservas.dao.ReservaDAO;
import com.reservas.model.Reserva;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    public void aniadirReserva(){

    }
}
