package com.reservas.controller;

import com.reservas.dao.ClienteDAO;
import com.reservas.dao.ReservaDAO;
import com.reservas.model.Reserva;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.ArrayList;

public class ReservaFormController {
    @FXML
    private ComboBox<Integer> idCliente;
    @FXML
    private TextField idPropiedad;
    @FXML
    private DatePicker fechaInicio;
    @FXML
    private DatePicker fechaFin;
    @FXML
    private TextField numPersonas;
    @FXML
    private ComboBox<Reserva.EstadoReserva> estadoCombo;
    @FXML
    private TextField precio;
    @FXML
    private TextArea motivo;
    @FXML
    private Button btnCancelar;
    private final ReservaDAO reservaDAO = new ReservaDAO();
    @FXML
    public void initialize(){
        ArrayList<Integer> ids = new ClienteDAO().getIDClientes();
        idCliente.setItems(FXCollections.observableArrayList(ids));
        ArrayList<Reserva.EstadoReserva> estados = new ArrayList<>();
        estados.add(Reserva.EstadoReserva.CANCELADA);
        estados.add(Reserva.EstadoReserva.CONFIRMADA);
        estados.add(Reserva.EstadoReserva.PENDIENTE);
        estadoCombo.setItems(FXCollections.observableArrayList(estados));
    }
    @FXML
    private void aniadirNuevaReserva(){
        Reserva r = new Reserva(idCliente.getValue(), Integer.parseInt(idPropiedad.getText()), Date.valueOf(fechaInicio.getValue())
        ,Date.valueOf(fechaFin.getValue()), Integer.parseInt(numPersonas.getText()), estadoCombo.getValue(),
                Double.parseDouble(precio.getText()) , motivo.getText());
        reservaDAO.aniadirReserva(r);

    }
    @FXML
    private void dispose(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

}
