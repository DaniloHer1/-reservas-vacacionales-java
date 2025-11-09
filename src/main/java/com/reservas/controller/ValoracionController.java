package com.reservas.controller;

import com.reservas.config.DataBaseConnection;
import com.reservas.dao.ValoracionDAO;
import com.reservas.model.Valoracion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <h1>Controlador de Valoraciones</h1>
 * Controlador principal encargado de gestionar la vista de <b>Valoraciones</b> dentro del sistema de reservas.<br>
 * Se encarga de coordinar la interacción entre la interfaz JavaFX y la capa DAO ({@link ValoracionDAO}),
 * permitiendo realizar operaciones CRUD sobre las valoraciones de los clientes.
 *
 * <p><b>Funciones principales:</b></p>
 * <ul>
 *   <li>Cargar y mostrar las valoraciones registradas.</li>
 *   <li>Buscar valoraciones por ID de reserva.</li>
 *   <li>Abrir formularios para crear o modificar valoraciones.</li>
 *   <li>Eliminar registros existentes.</li>
 * </ul>
 *
 * @author Sofía Abid
 * @since 03/11/2025
 */
public class ValoracionController {

    @FXML
    private TableView<Valoracion> tablaValoraciones;

    @FXML private TableColumn<Valoracion, Integer> colIdValoracion;
    @FXML private TableColumn<Valoracion, Integer> colIdReserva;
    @FXML private TableColumn<Valoracion, Integer> colPuntuacion;
    @FXML private TableColumn<Valoracion, String> colComentario;
    @FXML private TableColumn<Valoracion, Boolean> colAnonimato;
    @FXML private TableColumn<Valoracion, LocalDateTime> colFechaValoracion;

    @FXML private TextField txtBusquedaReserva;

    private final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ValoracionDAO valoracionDAO;
    private final ObservableList<Valoracion> listaValoracion = FXCollections.observableArrayList();

    /**
     * Inicializa la vista de valoraciones, configurando las columnas,
     * el evento de doble clic y cargando los datos desde la base de datos.
     */
    @FXML
    public void initialize() {

        Connection connection = DataBaseConnection.getInstance().conectarBD();
        valoracionDAO = new ValoracionDAO(connection);
        configurarColumnas();
        configurarDobleClickFila();
        cargarValoraciones();

    }

    /**
     * Configura las columnas de la tabla {@link TableView} para vincularlas con las
     * propiedades del modelo {@link Valoracion}.
     * <p>
     * Además, da formato a la columna de fecha para mostrar valores legibles.
     * </p>
     */
    private void configurarColumnas() {

        colIdValoracion.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdReserva.setCellValueFactory(new PropertyValueFactory<>("reserva"));
        colPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        colAnonimato.setCellValueFactory(new PropertyValueFactory<>("anonimato"));
        colFechaValoracion.setCellValueFactory(new PropertyValueFactory<>("fechaValoracion"));

        colFechaValoracion.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {

                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(FECHA_HORA));

            }

        });

    }

    /**
     * Configura la acción de doble clic sobre una fila de la tabla.
     * <p>
     * Al hacer doble clic con el botón principal del ratón sobre una valoración,
     * se abre automáticamente el formulario de edición.
     * </p>
     */
    private void configurarDobleClickFila() {

        tablaValoraciones.setRowFactory(tv -> {

            TableRow<Valoracion> row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {
                    tablaValoraciones.getSelectionModel().select(row.getItem());
                    modificarValoracion(null);
                }

            });
            return row;
        });

    }

    /**
     * Carga todas las valoraciones desde la base de datos
     * y las muestra en la tabla.
     */
    private void cargarValoraciones() {

        listaValoracion.clear();
        listaValoracion.setAll(valoracionDAO.leerValoraciones());
        tablaValoraciones.setItems(listaValoracion);

    }

    /**
     * Carga y muestra las valoraciones asociadas a un ID de reserva específico.
     *
     * @param id Identificador de la reserva cuyos comentarios se desean visualizar.
     */
    private void cargarValoracionesPorId(int id) {

        listaValoracion.setAll(valoracionDAO.buscarPorIDReserva(id));
        tablaValoraciones.setItems(listaValoracion);

    }

    /**
     * Refresca el contenido de la tabla con las valoraciones más recientes.
     *
     * @param event Evento de acción generado por el botón de "ACTUALIZAR 🔁".
     */
    @FXML private void refrescarTabla(ActionEvent event) {

        cargarValoraciones();

    }

    /**
     * Abre el formulario para registrar una nueva valoración.
     *
     * @param event Evento de acción generado por el botón "AÑADIR ➕".
     */
    @FXML private void guardarValoracion(ActionEvent event) {

        abrirFormulario(null);

    }

    /**
     * Abre el formulario de edición para modificar una valoración existente.
     * Si no se selecciona ninguna, se muestra una advertencia.
     *
     * @param event Evento de acción generado por el botón "MODIFICAR ✔".
     */
    @FXML private void modificarValoracion(ActionEvent event) {

        Valoracion seleccionada = tablaValoraciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una valoración para modificar.");
            return;
        }

        abrirFormulario(seleccionada);

    }


    /**
     * Abre la ventana del formulario de valoración.
     *
     * @param valoracion Objeto {@link Valoracion} que se desea editar o
     *                          {@code null} para crear una nueva.
     */
    private void abrirFormulario(Valoracion valoracion) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/valoraciones-form-view.fxml"));
            Parent root = loader.load();

            ValoracionFormController controller = loader.getController();
            if (valoracion != null) controller.cargarValoracion(valoracion);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarValoraciones();

        } catch (IOException e) {

            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se ha podido abrir el formulario.");

        }
    }

    /**
     * Elimina la valoración seleccionada tras confirmar la acción con el usuario.
     *
     * @param event Evento de acción generado por el botón "ELIMINAR ✖".
     */
    @FXML private void eliminarValoracion(ActionEvent event) {

        Valoracion seleccionada = tablaValoraciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione una valoración para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar la valoración ID: " + seleccionada.getId() +
                "?", ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(res -> {

            if (res == ButtonType.YES) {
                valoracionDAO.eliminarValoracion(seleccionada);
                cargarValoraciones();
            }

        });
    }

    /**
     * Busca valoraciones asociadas a un ID de reserva ingresado en el campo de texto.
     *
     * @param event Evento de acción generado al presionar el botón de búsqueda 🔍.
     */
    @FXML private void buscarPorIDReserva(ActionEvent event) {

        String texto = txtBusquedaReserva.getText().strip();

        if (texto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Introduce un ID de reserva.");
            return;
        }

        try {

            int id = Integer.parseInt(texto);
            List<Valoracion> resultados = valoracionDAO.buscarPorIDReserva(id);

            if (resultados.isEmpty()) {

                mostrarAlerta(Alert.AlertType.INFORMATION, "No se han encontrado valoraciones con ese ID.");

            } else { cargarValoracionesPorId(id); }

        } catch (NumberFormatException e) {

            mostrarAlerta(Alert.AlertType.ERROR, "ID de reserva inválido.");

        }
    }

    /**
     * Muestra una alerta genérica en pantalla.
     *
     * @param tipo Tipo de alerta (informativa, de advertencia, error...).
     * @param mensaje Contenido del mensaje a mostrar.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
}
