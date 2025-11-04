package com.reservas.controller;

import com.reservas.dao.ClienteDAO;
import com.reservas.dao.PropiedadDAO;
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
    private TextField idReserva;
    @FXML
    private ComboBox<Integer> idCliente;
    @FXML
    private ComboBox<Integer> idPropiedad;
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
    public Reserva reservaEditar;
    private final ReservaDAO reservaDAO = new ReservaDAO();
    public boolean modoEditar;
    @FXML
    public void initialize(){
        ArrayList<Integer> ids = new ClienteDAO().getIDClientes();
        ArrayList<Integer> idsProp = new PropiedadDAO().getIDPropiedades();
        idCliente.setItems(FXCollections.observableArrayList(ids));
        idPropiedad.setItems(FXCollections.observableArrayList(idsProp));
        ArrayList<Reserva.EstadoReserva> estados = new ArrayList<>();
        estados.add(Reserva.EstadoReserva.CANCELADA);
        estados.add(Reserva.EstadoReserva.CONFIRMADA);
        estados.add(Reserva.EstadoReserva.PENDIENTE);
        estadoCombo.setItems(FXCollections.observableArrayList(estados));
        estadoCombo.getSelectionModel().select(2);
        idReserva.setDisable(true);
    }
    public void setModoEditar(boolean modoEditar, Reserva reservaParaEditar) {
        this.modoEditar = modoEditar;
        this.reservaEditar = reservaParaEditar;
            idCliente.setValue(reservaEditar.getId_cliente());
            idPropiedad.setValue(reservaEditar.getId_propiedad());
            fechaInicio.setValue(reservaEditar.getFecha_inicio().toLocalDate());
            fechaFin.setValue(reservaEditar.getFecha_fin().toLocalDate());
            numPersonas.setText(String.valueOf(reservaEditar.getNum_personas()));
            estadoCombo.setValue(reservaEditar.getEstadoReserva());
            precio.setText(String.valueOf(reservaEditar.getPrecio_total()));
            motivo.setText(reservaEditar.getMotivo_cancelacion());
            idReserva.setText(String.valueOf(reservaEditar.getId_reserva()));

    }
    @FXML
    private boolean aniadirNuevaReserva(){
        if (idCliente.getValue() == null){
            MainController.mostrarAlerta("Error", "El ID Cliente no puede estar vacío.", Alert.AlertType.ERROR);
            return false;
        }
        if (idPropiedad.getValue() == null){
            MainController.mostrarAlerta("Error", "El ID propiedad no puede estar vacío.", Alert.AlertType.ERROR);
            return false;
        }

        if (fechaInicio.getValue() == null){
            MainController.mostrarAlerta("Error", "La fecha no puede estar vacía.", Alert.AlertType.ERROR);
            return false;
        }
        if (fechaFin.getValue() == null){
            MainController.mostrarAlerta("Error", "La fecha no puede estar vacía.", Alert.AlertType.ERROR);
            return false;
        }
        if (numPersonas.getText() == null || numPersonas.getText().isEmpty()){
            MainController.mostrarAlerta("Error", "El número de personas no puede estar vacío.", Alert.AlertType.ERROR);
            return false;
        }
        try{
            Integer.parseInt(numPersonas.getText());
        }catch (NumberFormatException e){
            MainController.mostrarAlerta("Error", "El número de personas debe ser un número.", Alert.AlertType.ERROR);
            return false;
        }
        if (precio.getText() == null || precio.getText().isEmpty()){
            MainController.mostrarAlerta("Error", "El precio no puede estar vacío.", Alert.AlertType.ERROR);
            return false;
        }
        try{
            Double.parseDouble(precio.getText());
        }catch (NumberFormatException e){
            MainController.mostrarAlerta("Error", "El precio debe ser un número.", Alert.AlertType.ERROR);
            return false;
        }
        if (modoEditar){
            Reserva r = new Reserva(Integer.parseInt(idReserva.getText()), idCliente.getValue(), idPropiedad.getValue(), Date.valueOf(fechaInicio.getValue())
                    ,Date.valueOf(fechaFin.getValue()), Integer.parseInt(numPersonas.getText()), estadoCombo.getValue(),
                    Double.parseDouble(precio.getText()) , motivo.getText());
           if(reservaDAO.modificarReserva(r)==1){
               MainController.mostrarAlerta("Reserva añadida", "Reserva añadida correctamente", Alert.AlertType.INFORMATION);
           }else{
               MainController.mostrarAlerta("Error", "La reserva no se pudo añadir, revisa los campos", Alert.AlertType.ERROR);
           }
        }else {
            Reserva r = new Reserva(idCliente.getValue(), idPropiedad.getValue(), Date.valueOf(fechaInicio.getValue())
                    ,Date.valueOf(fechaFin.getValue()), Integer.parseInt(numPersonas.getText()), estadoCombo.getValue(),
                    Double.parseDouble(precio.getText()) , motivo.getText());
            if(reservaDAO.aniadirReserva(r)==1){
                MainController.mostrarAlerta("Reserva añadida", "Reserva añadida correctamente", Alert.AlertType.INFORMATION);
            }else{
                MainController.mostrarAlerta("Error", "La reserva no se pudo añadir, revisa los campos", Alert.AlertType.ERROR);
            }
        }
        this.dispose();
        return true;
    }
    @FXML
    private void dispose(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
