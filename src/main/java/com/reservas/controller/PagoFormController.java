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

/**
 * @author
 * Daniel Hernando
 */
public class PagoFormController {

    @FXML
    private ComboBox<String> cbReserva;

    @FXML
    private TextField txtMonto;

    @FXML
    private RadioButton rbTarjeta;

    @FXML
    private RadioButton rbEfectivo;

    @FXML
    private RadioButton rbTransferencia;

    @FXML
    private RadioButton rbPaypal;

    @FXML
    private RadioButton rbStripe;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TextField txtFecha;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    @FXML
    private TextField txtReferencia;

    @FXML
    private Label txtTitulo;

    private PagoDAO pagoDAO;
    private Pago pagoAEditar = null;
    private boolean modoEdicion = false;


    @FXML
    public void initialize() {
        // Conexión a la base de datos y carga inicial de datos
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        pagoDAO = new PagoDAO(connection);

        // Cargar datos iniciales
        pagoDAO.mostrarTodosPagos();
        pagoDAO.mostrarTodosIdReservas();

        // Configuro los estados disponibles para el ComboBox
        cbEstado.getItems().addAll("COMPLETADO", "PENDIENTE", "RECHAZADO");
        cbEstado.setValue("PENDIENTE");

        // Cargo los IDs de reservas disponibles
        pagoDAO.getListaReservasID().forEach(reserva -> {
            cbReserva.getItems().add(String.valueOf(reserva));
        });

        // formateo la fecha actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(LocalDateTime.now().format(formatter));

        // Campo de referencia no editable, se genera automáticamente
        txtReferencia.setEditable(false);
        if (!modoEdicion) {
            String referencia = pagoDAO.generarSiguienteReferencia();
            txtReferencia.setText(referencia);
        }

        // Cuando el usuario selecciona una reserva, se carga automáticamente el monto
        cbReserva.setOnAction(event -> {
            Integer reservaId = Integer.valueOf(cbReserva.getValue());
            if (reservaId != null) {
                pagoDAO.cargarMontoDeReserva(reservaId, txtMonto);
            }
        });
    }

    /**
     * Valida los campos del formulario antes de guardar o actualizar el pago.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarFormulario() {

        if (cbReserva.getValue() == null) {
            mostrarAlerta("Validación", "Selecciona una reserva", Alert.AlertType.WARNING);
            return false;
        }

        if (txtMonto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El monto no puede estar vacío", Alert.AlertType.WARNING);
            return false;
        }

        try {
            double monto = Double.parseDouble(txtMonto.getText().trim().replace(",", "."));
            if (monto <= 0) {
                mostrarAlerta("Validación", "El monto no puede ser menor o igual a 0", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El formato del monto es inválido", Alert.AlertType.ERROR);
            return false;
        }

        if (cbEstado.getValue() == null) {
            mostrarAlerta("Validación", "Selecciona un estado", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    /**
     * Guarda o actualiza el pago en la base de datos según el modo actual.
     */
    @FXML
    public void guardarPago() {
        if (!validarFormulario()) {
            return;
        }

        Pago pago;
        boolean operacionExitosa;
        String mensaje = "";

        if (modoEdicion) {
            // Si está en modo edición, se actualiza el pago existente
            pago = pagoAEditar;
            mensaje = "Pago actualizado correctamente";

        } else {
            // Si es un nuevo pago, se crea un objeto nuevo
            pago = new Pago();
            pago.setReserva(Integer.parseInt(cbReserva.getValue()));
            pago.setMonto(Double.parseDouble(txtMonto.getText().trim().replace(",", ".")));
            pago.setFechaPago(LocalDateTime.now());

            // Genero una nueva referencia única
            String referencia = pagoDAO.generarSiguienteReferencia();
            pago.setReferenciaTransaccion(referencia);
            txtReferencia.setText(referencia);

            mensaje = "Pago registrado correctamente";
        }

        // Asigno el método de pago seleccionado
        if (rbEfectivo.isSelected()) pago.setMetodoPago(Pago.MetodoPago.EFECTIVO);
        if (rbTarjeta.isSelected()) pago.setMetodoPago(Pago.MetodoPago.TARJETA);
        if (rbTransferencia.isSelected()) pago.setMetodoPago(Pago.MetodoPago.TRANSFERENCIA);
        if (rbPaypal.isSelected()) pago.setMetodoPago(Pago.MetodoPago.PAYPAL);
        if (rbStripe.isSelected()) pago.setMetodoPago(Pago.MetodoPago.STRIPE);


        pago.setEstadoPago(Pago.EstadoPago.valueOf(cbEstado.getValue()));


        if (modoEdicion) {
            operacionExitosa = pagoDAO.actualizarPago(pago);
        } else {
            operacionExitosa = pagoDAO.insertarPago(pago);
        }

        // Muestro resultado
        if (operacionExitosa) {
            mostrarAlerta("Éxito", mensaje, Alert.AlertType.INFORMATION);
            cerrarVentana();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el pago", Alert.AlertType.ERROR);
        }
    }

    /**
     * Carga los datos de un pago existente para editarlos.
     * @param pago Pago a editar.
     */
    public void cargarPagoParaEditar(Pago pago) {
        modoEdicion = true;
        pagoAEditar = pago;

        txtTitulo.setText("Modificacion de Pagos");

        // Bloqueo algunos campos que no deberían cambiar
        cbReserva.setValue(String.valueOf(pago.getReserva()));
        cbReserva.setDisable(true);

        txtMonto.setText(String.valueOf(pago.getMonto()));
        txtMonto.setEditable(false);

        txtReferencia.setText(pago.getReferenciaTransaccion());
        txtReferencia.setDisable(true);

        // Selecciono el método de pago correspondiente
        switch (pago.getMetodoPago()) {
            case TARJETA -> rbTarjeta.setSelected(true);
            case EFECTIVO -> rbEfectivo.setSelected(true);
            case TRANSFERENCIA -> rbTransferencia.setSelected(true);
            case PAYPAL -> rbPaypal.setSelected(true);
            case STRIPE -> rbStripe.setSelected(true);
        }

        // Seteo el estado actual
        cbEstado.setValue(pago.getEstadoPago().name());
        btnGuardar.setText("Actualizar");
    }

    /**
     * Cierra la ventana actual del formulario.
     */
    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra una alerta en pantalla.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
