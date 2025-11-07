package com.reservas.controller;

import com.reservas.Main;
import com.reservas.dao.ReservaDAO;
import com.reservas.model.Reserva;
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

public class ReservaControler {
    private ReservaDAO reservaDAO;
    @FXML
    private TableView<Reserva> tableView;
    @FXML
    private TextField txtFieldBuscar;
    @FXML
    private TableColumn<Reserva, Integer> colID;
    @FXML
    private TableColumn<Reserva, Integer> colID_cliente;
    @FXML
    private TableColumn<Reserva, Integer> colID_prop;
    @FXML
    private TableColumn<Reserva, String> colFechaIni;
    @FXML
    private TableColumn<Reserva, String> colFechaFin;
    @FXML
    private TableColumn<Reserva, Integer> colNumPersonas;
    @FXML
    private TableColumn<Reserva, Reserva.EstadoReserva> colEstado;
    @FXML
    private TableColumn<Reserva, Double> colPrecio;
    @FXML
    private TableColumn<Reserva, String> colMotivo;
    @FXML
    private Label totalLabel;

    @FXML
    public void initialize() {
        reservaDAO = new ReservaDAO();
        ObservableList<Reserva> reservas = FXCollections.observableArrayList(reservaDAO.getReservas());
        configurarColumnas();
        tableView.setItems(reservas);
        totalLabel.setText(totalLabel.getText() + reservas.size());
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

    public void aniadirReservaForm() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("reservas-form-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ReservaFormController formController = loader.getController();
        formController.modoEditar = false;
        formController.reservaControler = this;
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Añadir Reserva");
        stage.show();
    }

    public void eliminarReserva() {
        if ((tableView.getSelectionModel().getSelectedItem()) == null) {
            MainController.mostrarAlerta("Selecciona una reserva", "Por favor, selecciona una reserva para eliminarla", Alert.AlertType.WARNING);
        } else {
            if (reservaDAO.eliminarReserva(tableView.getSelectionModel().getSelectedItem()) == 1) {
                MainController.mostrarAlerta("Reserva eliminada", "Reserva eliminada correctamente", Alert.AlertType.INFORMATION);
                initialize();
            } else {
                MainController.mostrarAlerta("Error", "La reserva seleccionada no existe. Por favor, actualiza la tabla.", Alert.AlertType.ERROR);
            }
        }
    }

    public void editarReservaForm() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            MainController.mostrarAlerta("Selecciona una reserva", "Por favor, selecciona una reserva para modificarla.", Alert.AlertType.WARNING);
        } else {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("reservas-form-view.fxml"));
            Scene scene;
            try {
                scene = new Scene(loader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ReservaFormController formController = loader.getController();
            formController.setModoEditar(true, tableView.getSelectionModel().getSelectedItem());
            formController.reservaControler = this;
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Reserva");
            stage.show();
        }
    }

    public void buscarIDReserva() {
        String texto = txtFieldBuscar.getText();
        if (texto == null || texto.isBlank()) {
            initialize();
        } else {
            Reserva r = new Reserva();
            try {
                r = reservaDAO.buscarReservaID(Integer.parseInt(texto));
            } catch (NumberFormatException e) {
                MainController.mostrarAlerta("Error", "Por favor, introduce un número", Alert.AlertType.ERROR);
                txtFieldBuscar.clear();
            }
            if (r == null) {
                MainController.mostrarAlerta("Error", "No se encontaron reservas con ese ID.", Alert.AlertType.ERROR);
                txtFieldBuscar.clear();
            } else {
                ObservableList<Reserva> observableList = FXCollections.observableArrayList(r);
                tableView.setItems(observableList);
            }
        }
    }
}
