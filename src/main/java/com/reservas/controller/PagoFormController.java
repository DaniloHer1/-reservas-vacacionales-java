package com.reservas.controller;

import com.reservas.config.DataBaseConnection;
import com.reservas.dao.PagoDAO;
import com.reservas.model.Pago;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagoFormController {

    @FXML
    ComboBox<String> cbReserva;

    @FXML
    TextField txtMonto;

    @FXML
    private RadioButton rbTarjeta;

    @FXML
    private RadioButton rbEfectivo;

    @FXML
    private RadioButton rbTransferencia;

    @FXML
    private ToggleGroup grupoMetodo;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TextField txtFecha;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    private PagoDAO pagoDAO;

    private Pago pagoAEditar = null;
    private boolean modoEdicion = false;


    @FXML
    public void initialize() {

        Connection connection = DataBaseConnection.getInstance().conectarBD();
        pagoDAO=new PagoDAO(connection);
        pagoDAO.mostrarTodosPagos();
        pagoDAO.mostrarTodosIdReservas();

        cbEstado.getItems().addAll("COMPLETADO","PENDIENTE","RECHAZADO");
        cbEstado.setValue("PENDIENTE");


        pagoDAO.getListaReservasID().forEach((reserva -> {
            cbReserva.getItems().add(String.valueOf(reserva));
        }));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(LocalDateTime.now().format(formatter));

        cbReserva.setOnAction(event -> {
            Integer reservaid= Integer.valueOf(cbReserva.getValue());
            if (reservaid!=null){
                pagoDAO.cargarMontoDeReserva(reservaid,txtMonto);
            }
        });




    }

    private boolean validarCampos(){

        if (cbReserva.getValue() == null){
            mostrarAlerta("Validación", "Selecciona una reserva", Alert.AlertType.WARNING);
            return false;
        }
        if (txtMonto.getText().trim().isEmpty()){
            mostrarAlerta("Validacion","El monto no puede estar vacio",Alert.AlertType.WARNING);
            return false;
        }
        try {
            double monto=Double.parseDouble(txtMonto.getText().trim().replace(",","."));
            if (monto<=0){
                mostrarAlerta("Validacion","El monto no puede ser menor o igual que 0",Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        if(cbEstado.getValue() ==null){
            mostrarAlerta("Validación", "Seleccione un estado", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    public void guardarPagoEnBD() {
        if (!validarCampos()){
            return;
        }

        Pago pago;
        boolean insertado;
        String mensaje="";
        if (modoEdicion){

            pago=pagoAEditar;
            mensaje="Pago actualizado correctamente";

        }else {
            pago = new Pago();

            pago.setReserva(Integer.parseInt(cbReserva.getValue()));
            pago.setMonto(Double.parseDouble(txtMonto.getText().trim().replace(",", ".")));
            pago.setFechaPago(LocalDateTime.now());
            mensaje="Pago insertado correctamente";
        }

        if (rbEfectivo.isSelected()){
            pago.setMetodoPago(Pago.MetodoPago.EFECTIVO);
        }
        if (rbTarjeta.isSelected()){
            pago.setMetodoPago(Pago.MetodoPago.TARJETA);
        }
        if (rbTransferencia.isSelected()){
            pago.setMetodoPago(Pago.MetodoPago.TRANSFERENCIA);
        }

        String estado=cbEstado.getValue();
        pago.setEstadoPago(Pago.EstadoPago.valueOf(estado));

        if (modoEdicion){
            insertado=pagoDAO.actualizarPago(pago);
        }else{
            insertado=pagoDAO.insertarPago(pago);
        }

        if (insertado) {
            mostrarAlerta("Éxito", mensaje, Alert.AlertType.INFORMATION);
            cancelar() ;
        } else {
            mostrarAlerta("Error", "No se pudo guardar el pago", Alert.AlertType.ERROR);
        }
    }

    public void guardarPago(Pago pago){
        modoEdicion=true;
        pagoAEditar=pago;

        cbReserva.setValue(String.valueOf(pago.getReserva()));
        cbReserva.setDisable(true);

        txtMonto.setText(String.valueOf(pago.getMonto()));
        txtMonto.setEditable(false);

        switch (pago.getMetodoPago()){
            case TARJETA -> rbTarjeta.setSelected(true);
            case EFECTIVO -> rbEfectivo.setSelected(true);
            case TRANSFERENCIA -> rbTransferencia.setSelected(true);
        }

        cbEstado.setValue(pago.getEstadoPago().name());

        btnGuardar.setText("Actualizar");

    }


    @FXML
    public void cancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
