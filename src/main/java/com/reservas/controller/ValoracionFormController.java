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
 * <h1>Controlador del formulario de Valoraciones</h1>
 * Controlador encargado de gestionar la interfaz del formulario de valoraciones
 * (creación y edición), conectando la capa visual con la base de datos mediante
 * {@link ValoracionDAO}.
 *
 * <h2>Funciones principales:</h2>
 * <ul>
 *     <li>Crear nuevas valoraciones o editar las existentes.</li>
 *     <li>Validar los campos del formulario antes de guardar.</li>
 *     <li>Asignar puntuaciones mediante botones de radio (1 a 5).</li>
 *     <li>Registrar la fecha de valoración (actual o seleccionada).</li>
 *     <li>Cerrar la ventana tras guardar o cancelar.</li>
 * </ul>
 *
 * @author Sofía Abid
 * @since 05/11/2025
 */

public class ValoracionFormController {


    @FXML private TextField txtIdValoracion;  // ID Valoración (autogenerado)
    @FXML private TextField txtReservaValF;   // ID Reserva
    @FXML private TextArea txtComentarioValF;
    @FXML private CheckBox checkAnonimato;
    @FXML private DatePicker fechaValF;

    @FXML private RadioButton rbtn1Val, rbtn2Val, rbtn3Val, rbtn4Val, rbtn5Val;

    @FXML private Button btnGuardarValF;
    @FXML private Button btnCancelarValF;

    private ToggleGroup grupoPuntuacion;
    private ValoracionDAO valoracionDAO;
    private Valoracion valoracionActual;



    /**
     * Inicializa el formulario configurando sus componentes y comportamiento.
     */
    @FXML
    public void initialize() {

        grupoPuntuacion = new ToggleGroup();
        rbtn1Val.setToggleGroup(grupoPuntuacion);
        rbtn2Val.setToggleGroup(grupoPuntuacion);
        rbtn3Val.setToggleGroup(grupoPuntuacion);
        rbtn4Val.setToggleGroup(grupoPuntuacion);
        rbtn5Val.setToggleGroup(grupoPuntuacion);

        txtIdValoracion.setEditable(false);
        txtIdValoracion.setDisable(true);

        Connection con = DataBaseConnection.getInstance().conectarBD();
        valoracionDAO = new ValoracionDAO(con);

        btnGuardarValF.setOnAction(e -> guardarValoracion());
        btnCancelarValF.setOnAction(e -> cerrarVentana());

    }


    /**
     * Carga los datos de una valoración existente en el formulario para su edición.
     *
     * @param valoracion Valoración a editar o {@code null} para crear una nueva.
     */
    public void cargarValoracion(Valoracion valoracion) {

        this.valoracionActual = valoracion;
        if (valoracion == null) return;

        txtIdValoracion.setText(String.valueOf(valoracion.getId()));
        txtReservaValF.setText(String.valueOf(valoracion.getReserva()));
        txtComentarioValF.setText(valoracion.getComentario());
        checkAnonimato.setSelected(valoracion.isAnonimato());

        if (valoracion.getFechaValoracion() != null) {

            fechaValF.setValue(valoracion.getFechaValoracion().toLocalDate());

        }

        switch (valoracion.getPuntuacion()) {

            case 1 -> grupoPuntuacion.selectToggle(rbtn1Val);
            case 2 -> grupoPuntuacion.selectToggle(rbtn2Val);
            case 3 -> grupoPuntuacion.selectToggle(rbtn3Val);
            case 4 -> grupoPuntuacion.selectToggle(rbtn4Val);
            case 5 -> grupoPuntuacion.selectToggle(rbtn5Val);

        }
    }


    /**
     * Valida los datos del formulario y guarda la valoración en la base de datos.
     * <ul>
     *     <li>Si el campo ID está vacío, se crea una nueva valoración.</li>
     *     <li>Si el campo ID contiene un valor, se modifica la existente.</li>
     *     <li>Incluye validaciones numéricas y de selección de puntuación.</li>
     *     <li>Muestra mensajes de éxito o error según el resultado.</li>
     * </ul>
     */
    private void guardarValoracion() {
        try {

            if (txtReservaValF.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Debe ingresar el ID de la reserva.");
                return;
            }

            RadioButton seleccionado = (RadioButton) grupoPuntuacion.getSelectedToggle();
            if (seleccionado == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Debe seleccionar una puntuación (1 a 5).");
                return;
            }

            int puntuacion = Integer.parseInt(seleccionado.getText());
            int idReserva = Integer.parseInt(txtReservaValF.getText());
            String comentario = txtComentarioValF.getText();
            boolean anonimato = checkAnonimato.isSelected();

            LocalDate fechaSeleccionada = fechaValF.getValue();
            LocalDateTime fechaValoracion = (fechaSeleccionada != null) ? fechaSeleccionada.atStartOfDay() : LocalDateTime.now();

            Valoracion nuevaVal = new Valoracion(idReserva, puntuacion, comentario, anonimato);
            nuevaVal.setFechaValoracion(fechaValoracion);

            boolean exito;

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

                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se ha podido guardar la valoración.");

            }


        } catch (NumberFormatException ex) {

            mostrarAlerta(Alert.AlertType.ERROR, "Formato incorrecto", "El ID de la reserva debe ser numérico.");

        } catch (Exception ex) {

            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", ex.getMessage());

        }
    }

    /**
     * Cierra la ventana actual del formulario de valoración.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelarValF.getScene().getWindow();
        stage.close();
    }


    /**
     * Muestra una alerta genérica en pantalla.
     *
     * @param tipo Tipo de alerta ({@link Alert.AlertType})
     * @param titulo Título de la ventana de alerta
     * @param mensaje Contenido del mensaje mostrado al usuario
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
}

