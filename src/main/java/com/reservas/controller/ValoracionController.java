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

public class ValoracionController {

    @FXML private TableView<Valoracion> tablaValoraciones;
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

    @FXML
    public void initialize() {
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        valoracionDAO = new ValoracionDAO(connection);
        configurarColumnas();
        configurarDobleClickFila();
        cargarValoraciones();
    }

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

    private void cargarValoraciones() {
        listaValoracion.setAll(valoracionDAO.leerValoraciones());
        tablaValoraciones.setItems(listaValoracion);
    }

    private void cargarValoracionesPorId(int id) {
        listaValoracion.setAll(valoracionDAO.buscarPorIDReserva(id));
        tablaValoraciones.setItems(listaValoracion);
    }

    @FXML private void refrescarTabla(ActionEvent event) {
        cargarValoraciones();
    }

    @FXML private void guardarValoracion(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML private void modificarValoracion(ActionEvent event) {
        Valoracion seleccionada = tablaValoraciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona una valoración para modificar.");
            return;
        }
        abrirFormulario(seleccionada);
    }

    private void abrirFormulario(Valoracion valoracion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/valoraciones-form-view.fxml"));
            Parent root = loader.load();
            ValoracionFormController controller = loader.getController();
            if (valoracion != null) controller.cargarValoracion(valoracion);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Formulario de Valoración");
            stage.showAndWait();

            cargarValoraciones();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo abrir el formulario.");
        }
    }

    @FXML private void eliminarValoracion(ActionEvent event) {
        Valoracion seleccionada = tablaValoraciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione una valoración para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar valoración ID: " + seleccionada.getId() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                valoracionDAO.eliminarValoracion(seleccionada);
                cargarValoraciones();
            }
        });
    }

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
                mostrarAlerta(Alert.AlertType.INFORMATION, "No se encontraron valoraciones con ese ID.");
            } else {
                cargarValoracionesPorId(id);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "ID de reserva inválido.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
