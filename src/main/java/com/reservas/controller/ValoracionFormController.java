package com.reservas.controller;


import com.reservas.config.DataBaseConnection;
import com.reservas.dao.ValoracionDAO;
import com.reservas.model.Valoracion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Controlador para la vista del formulario de Valoraciones (añadir/modificar).
 * Gestiona la lógica de guardado, actualización y cancelación.
 *
 * El campo ID de valoración (id_valoracion) se maneja automáticamente por la base de datos,
 * por lo que se muestra desactivado al usuario y solo se usa en modo edición.
 *
 * @author
 * Sofía Abid
 */
public class ValoracionFormController {


    @FXML
    private TextField txtIdValoracion;  // ID Valoración (autogenerado)
    @FXML
    private TextField txtReservaValF;   // ID Reserva
    @FXML
    private TextArea txtComentarioValF;
    @FXML
    private CheckBox checkAnonimato;
    @FXML
    private DatePicker fechaValF;


    // RadioButtons de puntuación
    @FXML
    private RadioButton rbtn1Val, rbtn2Val, rbtn3Val, rbtn4Val, rbtn5Val;


    @FXML
    private Button btnGuardarValF;
    @FXML
    private Button btnCancelarValF;


    private ToggleGroup grupoPuntuacion;
    private ValoracionDAO valoracionDAO;
    private Valoracion valoracionActual;


    // --- MÉTODO DE INICIALIZACIÓN ---
    @FXML
    public void initialize() {
        // Configurar ToggleGroup para puntuación
        grupoPuntuacion = new ToggleGroup();
        rbtn1Val.setToggleGroup(grupoPuntuacion);
        rbtn2Val.setToggleGroup(grupoPuntuacion);
        rbtn3Val.setToggleGroup(grupoPuntuacion);
        rbtn4Val.setToggleGroup(grupoPuntuacion);
        rbtn5Val.setToggleGroup(grupoPuntuacion);


        // El campo de ID no debe poder editarse
        txtIdValoracion.setEditable(false);
        txtIdValoracion.setDisable(true);


        // Crear instancia DAO
        Connection con = DataBaseConnection.getInstance().conectarBD();
        valoracionDAO = new ValoracionDAO(con);


        // Acciones de los botones
        btnGuardarValF.setOnAction(e -> guardarValoracion());
        btnCancelarValF.setOnAction(e -> cerrarVentana());
    }


    /**
     * Carga los datos de una valoración existente en el formulario (modo edición).
     */
    public void cargarValoracion(Valoracion valoracion) {
        this.valoracionActual = valoracion;
        if (valoracion == null) return;


        txtIdValoracion.setText(String.valueOf(valoracion.getId()));
        txtReservaValF.setText(String.valueOf(valoracion.getReserva()));
        txtComentarioValF.setText(valoracion.getComentario());
        checkAnonimato.setSelected(valoracion.isAnonimato());
        if (valoracion.getFechaValoracion() != null)
            fechaValF.setValue(valoracion.getFechaValoracion().toLocalDate());


        // Seleccionar puntuación correspondiente
        switch (valoracion.getPuntuacion()) {
            case 1 -> grupoPuntuacion.selectToggle(rbtn1Val);
            case 2 -> grupoPuntuacion.selectToggle(rbtn2Val);
            case 3 -> grupoPuntuacion.selectToggle(rbtn3Val);
            case 4 -> grupoPuntuacion.selectToggle(rbtn4Val);
            case 5 -> grupoPuntuacion.selectToggle(rbtn5Val);
        }
    }


    /**
     * Recoge los datos del formulario y guarda o modifica una valoración.
     */
    private void guardarValoracion() {
        try {
            // Validar ID de reserva
            if (txtReservaValF.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Debe ingresar el ID de la reserva.");
                return;
            }


            // Validar puntuación seleccionada
            RadioButton seleccionado = (RadioButton) grupoPuntuacion.getSelectedToggle();
            if (seleccionado == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Debe seleccionar una puntuación (1 a 5).");
                return;
            }


            int puntuacion = Integer.parseInt(seleccionado.getText());
            int idReserva = Integer.parseInt(txtReservaValF.getText());
            String comentario = txtComentarioValF.getText();
            boolean anonimato = checkAnonimato.isSelected();


            // Fecha seleccionada o fecha actual
            LocalDate fechaSeleccionada = fechaValF.getValue();
            LocalDateTime fechaValoracion = (fechaSeleccionada != null)
                    ? fechaSeleccionada.atStartOfDay()
                    : LocalDateTime.now();


            // Crear el objeto valoración
            Valoracion nuevaVal = new Valoracion(idReserva, puntuacion, comentario, anonimato);
            nuevaVal.setFechaValoracion(fechaValoracion);


            boolean exito;


            // Si hay un ID en el campo → editar; si no → crear nuevo
            if (txtIdValoracion.getText() != null && !txtIdValoracion.getText().isEmpty()) {
                nuevaVal.setId(Integer.parseInt(txtIdValoracion.getText()));
                exito = valoracionDAO.modificarValoracionPorId(nuevaVal);
            } else {
                exito = valoracionDAO.agregarValoracion(nuevaVal);
            }


            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La valoración se ha guardado correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la valoración.");
            }


        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato incorrecto", "El ID de la reserva debe ser numérico.");
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", ex.getMessage());
        }
    }


    /**
     * Cierra la ventana actual del formulario.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelarValF.getScene().getWindow();
        stage.close();
    }


    /**
     * Muestra una alerta genérica.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

