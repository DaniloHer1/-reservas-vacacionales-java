package com.reservas.controller;

import com.reservas.config.DataBaseConnection;
import com.reservas.dao.PagoDAO;
import com.reservas.model.Pago;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <h1>Controlador del formulario de Pagos</h1>
 *
 * Gestiona la ventana de creación o edición de pagos, controlando la validación
 * de datos, la interacción con {@link PagoDAO} y la comunicación con la base de datos.
 * Permite registrar nuevos pagos o modificar los existentes de forma segura.
 *
 * <h2>Funciones principales:</h2>
 * <ul>
 *     <li>Validación de campos antes del guardado.</li>
 *     <li>Inserción o actualización de registros de pagos.</li>
 *     <li>Autogeneración de referencias de transacción.</li>
 *     <li>Gestión de estados y métodos de pago.</li>
 * </ul>
 *
 * @author Daniel Hernando
 * @since 31/11/2025
 */
public class PagoFormController {

    @FXML private ComboBox<String> cbReserva;

    @FXML private TextField txtMonto;

    @FXML private RadioButton rbTarjeta;
    @FXML private RadioButton rbEfectivo;
    @FXML private RadioButton rbTransferencia;
    @FXML private RadioButton rbPaypal;
    @FXML private RadioButton rbStripe;

    @FXML private ComboBox<String> cbEstado;

    @FXML private TextField txtFecha;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @FXML private TextField txtReferencia;

    @FXML private Label txtTitulo;

    private PagoDAO pagoDAO;
    private Pago pagoAEditar = null;
    private boolean modoEdicion = false;

    /**
     * Inicializa el formulario de pagos con los valores y listas necesarias.
     */
    @FXML
    public void initialize() {

        Connection connection = DataBaseConnection.getInstance().conectarBD();
        pagoDAO = new PagoDAO(connection);

        pagoDAO.mostrarTodosPagos();
        pagoDAO.mostrarTodosIdReservas();

        cbEstado.getItems().addAll("COMPLETADO", "PENDIENTE", "RECHAZADO");
        cbEstado.setValue("PENDIENTE");

        pagoDAO.getListaReservasID().forEach(reserva -> {
            cbReserva.getItems().add(String.valueOf(reserva));
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(LocalDateTime.now().format(formatter));

        txtReferencia.setEditable(false);

        if (!modoEdicion) {

            String referencia = pagoDAO.generarSiguienteReferencia();
            txtReferencia.setText(referencia);

        }

        cbReserva.setOnAction(event -> {

            Integer reservaId = Integer.valueOf(cbReserva.getValue());

            if (reservaId != null) {
                pagoDAO.cargarMontoDeReserva(reservaId, txtMonto);
            }

        });
    }

    /**
     * Verifica que todos los campos del formulario sean válidos antes de procesar el pago.
     *
     * @return {@code true} si todos los campos son correctos, {@code false} si hay errores.
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
     * Guarda un nuevo pago o actualiza uno existente según el modo de edición actual.
     * <p>
     *     Guarda la información mediante {@link PagoDAO}.
     * </p>
     */
    @FXML
    public void guardarPago() {

        if (!validarFormulario()) { return; }

        Pago pago;
        boolean operacionExitosa;
        String mensaje = "";

        if (modoEdicion) {

            pago = pagoAEditar;
            mensaje = "Pago actualizado correctamente";

        } else {

            pago = new Pago();
            pago.setReserva(Integer.parseInt(cbReserva.getValue()));
            pago.setMonto(Double.parseDouble(txtMonto.getText().trim().replace(",", ".")));
            pago.setFechaPago(LocalDateTime.now());

            String referencia = pagoDAO.generarSiguienteReferencia();
            pago.setReferenciaTransaccion(referencia);
            txtReferencia.setText(referencia);

            mensaje = "Pago registrado correctamente";

        }

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

        if (operacionExitosa) {

            mostrarAlerta("Éxito", mensaje, Alert.AlertType.INFORMATION);
            cerrarVentana();

        } else {

            mostrarAlerta("Error", "No se pudo guardar el pago", Alert.AlertType.ERROR);

        }
    }

    /**
     * Carga los datos de un pago existente para su modificación.
     *
     * @param pago Pago a editar.
     * @param event Evento que activa la carga (clic en botón "MODIFICAR ✔").
     */
    public void cargarPagoParaEditar(Pago pago, ActionEvent event) {

        modoEdicion = true;
        pagoAEditar = pago;

        txtTitulo.setText("Modificacion de Pagos");

        cbReserva.setValue(String.valueOf(pago.getReserva()));
        cbReserva.setDisable(true);

        txtMonto.setText(String.valueOf(pago.getMonto()));
        txtMonto.setEditable(false);

        txtReferencia.setText(pago.getReferenciaTransaccion());
        txtReferencia.setDisable(true);

        switch (pago.getMetodoPago()) {

            case TARJETA -> rbTarjeta.setSelected(true);
            case EFECTIVO -> rbEfectivo.setSelected(true);
            case TRANSFERENCIA -> rbTransferencia.setSelected(true);
            case PAYPAL -> rbPaypal.setSelected(true);
            case STRIPE -> rbStripe.setSelected(true);

        }

        cbEstado.setValue(pago.getEstadoPago().name());
        btnGuardar.setText("ACTUALIZAR ✔");

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
     * Muestra una alerta de tipo genérico con el título, mensaje y tipo especificado.
     *
     * @param titulo título de la alerta.
     * @param mensaje texto del contenido.
     * @param tipo tipo de alerta (informativa, advertencia, error...).
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }

}
