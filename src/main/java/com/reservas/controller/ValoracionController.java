package com.reservas.controller;

import com.reservas.dao.ClienteDAO;
import com.reservas.dao.ValoracionDAO;
import com.reservas.model.Cliente;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la vista de gestión de valoraciones
 * Gestiona la interacción entre la interfaz JavaFX y la capa DAO
 *
 * @author Sofía Abid
 * @since 05/11/2025
 */

public class ValoracionController {
    @FXML
    private TableView<Valoracion> tablaValoraciones;

    @FXML private TableColumn<Valoracion, Integer> colIdValoracion;
    @FXML private TableColumn<Valoracion, Integer> colIdReserva;
    @FXML private TableColumn<Valoracion, Integer> colPuntuacion;
    @FXML private TableColumn<Valoracion, String> colComentario;
    @FXML private TableColumn<Valoracion, Boolean> colAnonimato;
    @FXML private TableColumn<Cliente, LocalDate> colFechaValoracion;

    @FXML private TextField txtBusquedaReserva;

    private final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ValoracionDAO valoracionDAO = new ValoracionDAO();
    private final ObservableList<Valoracion> listaValoracion = FXCollections.observableArrayList();

    /**
     * Inicializa la vista y carga los datos de las valoraciones
     */
    @FXML
    public void initialize() {

        configurarColumnas();
        configurarDobleClickFila();
        cargarValoraciones();

    }

    /**
     * Configura las columnas de la tabla para enlazarlas con las propiedades del modelo Cliente
     */
    public void configurarColumnas() {

        colIdValoracion.setCellValueFactory(new PropertyValueFactory<>("idValoracion"));
        colIdReserva.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        colAnonimato.setCellValueFactory(new PropertyValueFactory<>("anonimato"));
        colFechaValoracion.setCellValueFactory(new PropertyValueFactory<>("fechaValoracion"));
        colFechaValoracion.setCellFactory(col -> new TableCell<Cliente, LocalDate>() {

            protected void updateItem(LocalDate item, boolean empty) {

                super.updateItem(item, empty);
                if (empty || item == null) {

                    setText(null);

                } else {

                    setText(item.format(FECHA_HORA));

                }

            }

        });

    }

    /**
     * Permite abrir la ventana {@code valoraciones-form-view.fxml}, gestionada por {@link ValoracionController}
     * haciendo doble click sobre la tupla de la valoración que se quiere modificar.
     */
    private void configurarDobleClickFila() {

        tablaValoraciones.setRowFactory(tv -> {

            TableRow<Valoracion> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Valoracion valoracionSeleccionada = row.getItem();

                    modificarValoracion(new ActionEvent());

                }

            });

            return row;

        });

    }

    /**
     * Carga todas las valoraciones desde la base de datos en la tabla.
     */
    public void cargarValoraciones() {

        listaValoracion.clear();
        List<Valoracion> valoraciones = valoracionDAO.leerValoraciones();
        listaValoracion.addAll(valoraciones);
        tablaValoraciones.setItems(listaValoracion);

    }
    /**
     * Carga todos las valoraciones desde la base de datos en la tabla según el id de
     * reserva para la búsqueda.
     */
    public void cargarValoracionesPorId(int id) {

        listaValoracion.clear();
        List<Valoracion> valoraciones = valoracionDAO.buscarPorIDReserva(id);
        listaValoracion.addAll(valoraciones);
        tablaValoraciones.setItems(listaValoracion);

    }

    /**
     * Refresca la tabla con los datos actuales de la base de datos.
     */
    @FXML
    private void refrescarTabla(ActionEvent event) {

        cargarValoraciones();

    }

    /**
     * Maneja el evento generado al pulsar el botón Añadir en la vista principal.
     *
     * @param event evento de acción disparado al hacer clic en el botón Añadir.
     */
    @FXML
    private void guardarValoracion(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/valoraciones-form-view.fxml"));
            Parent root = loader.load();

            ValoracionFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarValoraciones();

        } catch (IOException e) {

            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario de valoraciones.").show();

        }

    }

    /**
     * Maneja el evento generado al pulsar el botón Modificar en la vista principal.
     *
     * @param event evento de acción disparado al hacer clic en el botón Modificar.
     */
    @FXML
    private void modificarValoracion(ActionEvent event) {

        Valoracion valoracionSeleccionada = tablaValoraciones.getSelectionModel().getSelectedItem();

        if (valoracionSeleccionada == null) {

            new Alert(Alert.AlertType.ERROR, "Seleccione una valoración para modificar", ButtonType.OK).show();

            return;

        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/valoraciones-form-view.fxml"));
            Parent root = loader.load();

            ValoracionFormController controller = loader.getController();
            controller.cargarValoracion(valoracionSeleccionada);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarValoraciones();

        } catch (IOException e) {

            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el formulario de valoraciones.").show();

        }

    }

    /**
     * Maneja el evento generado al pulsar el botón Eliminar en la vista principal.
     *
     * @param event evento de acción disparado al hacer clic en el botón Eliminar.
     */
    @FXML
    private void eliminarValoracion(ActionEvent event) {

        Valoracion valoracionSeleccionada = tablaValoraciones.getSelectionModel().getSelectedItem();

        if (valoracionSeleccionada == null) {

            new Alert(Alert.AlertType.ERROR, "Seleccione una valoración para eliminar", ButtonType.OK).show();

            return;

        }

        Alert confirmarEliminar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmarEliminar.setTitle("Confirmar eliminación");
        confirmarEliminar.setHeaderText("¿Desea eliminar la Valoracion?");
        confirmarEliminar.setContentText(
                "¿Estás seguro de que quieres eliminar la valoración?\n\n" +
                        "ID de reserva: " + valoracionSeleccionada.getReseva()
        );

        ButtonType btnSi = new ButtonType("Si", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmarEliminar.getButtonTypes().setAll(btnSi, btnNo);

        var respuesta = confirmarEliminar.showAndWait();

        if(respuesta.get() == btnSi) {

            try {

                valoracionDAO.eliminarValoracion(valoracionSeleccionada);
                cargarValoraciones();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "La valoración ha sido eliminada.");

            } catch (RuntimeException e) {

                mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar la valoración", e.getMessage());

            }

        }

    }

    /**
     * Si se encuentra una valoración, la tabla se posiciona y selecciona en la fila correspondiente.
     * En caso contrario, se muestra una alerta informativa.
     *
     * @param event evento de acción disparado al darle al botón de búsqueda que tiene como símbolo una lupa ( 🔍 ).
     */
    @FXML
    private void buscarPorEmail(ActionEvent event) {

        String idReserva = txtBusquedaReserva.getText().strip();

        if (idReserva.isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Introduce un ID de reserva.").show();

            return;

        }

        int id = Integer.parseInt(idReserva);
        ArrayList<Valoracion> valoraciones = valoracionDAO.buscarPorIDReserva(id);

        if (valoraciones.isEmpty()) {

            new Alert(Alert.AlertType.INFORMATION,"No se encontró ninguna valoración con el ID de reserva: " + id).show();

            return;

        }

        cargarValoracionesPorId(id);

    }

    /**
     * Muestra una alerta emergente en la interfaz con el tipo, título y mensaje especificados.
     *
     * @param tipo  tipo de alerta a mostrar.
     * @param titulo texto que aparecerá en la barra de título de la alerta.
     * @param mensaje contenido principal del mensaje a mostrar en el cuadro de diálogo.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }


}
