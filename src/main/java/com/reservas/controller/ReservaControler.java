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
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * <h1>Controlador de Reservas</h1>
 * Controlador principal encargado de la gestión de la vista de <b>Reservas</b> dentro del sistema.
 * Coordina la comunicación entre la interfaz JavaFX y la capa de acceso a datos {@link ReservaDAO},
 * permitiendo realizar operaciones CRUD sobre las reservas registradas.
 *
 * <h2>Funciones principales:</h2>
 * <ul>
 *     <li>Cargar y mostrar todas las reservas en la tabla principal.</li>
 *     <li>Buscar reservas por su identificador único (ID).</li>
 *     <li>Añadir nuevas reservas mediante el formulario.</li>
 *     <li>Editar o eliminar reservas existentes.</li>
 * </ul>
 *
 * @author Pablo Armas
 * @since 31/11/2025
 */
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

    /**
     * Inicializa la vista de reservas configurando las columnas,
     * cargando los datos desde la base de datos y mostrando el total actual.
     */
    @FXML
    public void initialize() {

        reservaDAO = new ReservaDAO();
        ObservableList<Reserva> reservas = FXCollections.observableArrayList(reservaDAO.getReservas());
        configurarColumnas();
        tableView.setItems(reservas);
        totalLabel.setText(totalLabel.getText() + reservas.size());

    }

    /**
     * Configura las columnas de la tabla enlazándolas con las propiedades del modelo {@link Reserva}.
     * También formatea las fechas de inicio y fin para mostrarlas como texto legible.
     */
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

    /**
     * Abre el formulario de creación de una nueva reserva mediante la vista
     * {@code reservas-form-view.fxml}.
     * <p>
     * Este formulario se abre en una nueva ventana, y al cerrarse,
     * la tabla de reservas se actualiza automáticamente.
     * </p>
     */
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
        stage.setTitle("Gestión Reservas");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/IconoPrincipal.png"))));
        stage.show();

    }

    /**
     * Elimina la reserva seleccionada en la tabla previa confirmación del usuario.
     * Si no hay ninguna seleccionada, se muestra una alerta de advertencia.
     */
    public void eliminarReserva() {

        if ((tableView.getSelectionModel().getSelectedItem()) == null) {

            MainController.mostrarAlerta("Selecciona una reserva", "Por favor, selecciona una reserva para eliminarla",
                    Alert.AlertType.WARNING);

        } else {

            if (reservaDAO.eliminarReserva(tableView.getSelectionModel().getSelectedItem()) == 1) {

                MainController.mostrarAlerta("Reserva eliminada", "Reserva eliminada correctamente", Alert.AlertType.INFORMATION);
                initialize();

            } else {

                MainController.mostrarAlerta("Error", "La reserva seleccionada no existe. Por favor, actualiza la tabla.",
                        Alert.AlertType.ERROR);

            }
        }
    }

    /**
     * Abre el formulario de edición para la reserva seleccionada.
     * <p>Si no se selecciona ninguna reserva, se mostrará una alerta de advertencia.</p>
     */
    public void editarReservaForm() {

        if (tableView.getSelectionModel().getSelectedItem() == null) {

            MainController.mostrarAlerta("Selecciona una reserva", "Por favor, selecciona una reserva para modificarla.",
                    Alert.AlertType.WARNING);

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
            stage.setTitle("Gestión Reservas");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/IconoPrincipal.png"))));
            stage.show();
        }
    }

    /**
     * Busca una reserva en la base de datos por su identificador único (ID).
     * <ul>
     *     <li>Si se deja el campo vacío, se recargan todas las reservas.</li>
     *     <li>Si el ID no es un número válido, se muestra un mensaje de error.</li>
     *     <li>Si no se encuentra ninguna reserva con ese ID, se muestra una alerta informativa.</li>
     * </ul>
     */
    public void buscarIDReserva() {

        String texto = txtFieldBuscar.getText();

        if (texto == null || texto.isBlank()) {

            initialize();
            return;
        }
        Reserva r;
        try {

            r = reservaDAO.buscarReservaID(Integer.parseInt(texto));

        } catch (NumberFormatException e) {

            MainController.mostrarAlerta("Error", "Por favor, introduce un número", Alert.AlertType.ERROR);
            txtFieldBuscar.clear();
            return;
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
