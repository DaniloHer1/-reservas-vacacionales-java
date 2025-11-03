package com.reservas.controller;

import com.reservas.Main;
import com.reservas.config.DataBaseConnection;
import com.reservas.dao.PagoDAO;
import com.reservas.model.Cliente;
import com.reservas.model.Pago;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

import static com.reservas.controller.MainController.cargarVista;

public class PagoController {

    @FXML
    private TableView<Pago> table;

    @FXML
    private TableColumn<Pago, Integer> colId;

    @FXML
    private TableColumn<Pago, String> colReserva;

    @FXML
    private TableColumn<Pago, String> colFecha;

    @FXML
    private TableColumn<Pago, String> colMonto;

    @FXML
    private TableColumn<Pago, Pago.MetodoPago> colMetodo;

    @FXML
    private TableColumn<Pago, Pago.EstadoPago> colEstado;

    @FXML
    private TextField txtBusquedaId;


    private PagoDAO pagoDAO;

    @FXML
    public void initialize() {
        // Conecto con la base de datos y preparo el DAO
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        pagoDAO = new PagoDAO(connection);

        // Configuro las columnas y cargo los pagos en la tabla
        configurarColumnasTabla();
        cargarListaPagos();
        configurarDobleClickFila();
        mostrarTodosPagos();
    }

    /**
     * Configura las columnas de la tabla de pagos.
     */
    private void configurarColumnasTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReserva.setCellValueFactory(new PropertyValueFactory<>("reserva"));
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFechaTexto())
        );
        colMonto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMontoMostrar())
        );

        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }

    /**
     * Carga los pagos desde la base de datos y los muestra en la tabla.
     */
    public void cargarListaPagos() {
        pagoDAO.mostrarTodosPagos();
        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(
                pagoDAO.getPagosDisponibles()
        );
        table.setItems(listaPagos);
    }

    /**
     * Abre el formulario para añadir un nuevo pago.
     */
    @FXML
    public void abrirFormularioNuevoPago() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Añadir Pago");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre el formulario de edición con los datos del pago seleccionado.
     */
    @FXML
    public void abrirFormularioEditarPago() {
        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para poder editarlo.", Alert.AlertType.ERROR);
            return;
        }

        abrirFormularioEdicion(pagoSeleccionado);
    }

    /**
     * Elimina el pago seleccionado después de confirmar la acción.
     */
    @FXML
    public void eliminarPagoSeleccionado() {
        Pago pagoSeleccionado = table.getSelectionModel().getSelectedItem();

        if (pagoSeleccionado == null) {
            mostrarAlerta("Error", "Debes seleccionar un pago para eliminarlo.", Alert.AlertType.ERROR);
            return;
        }

        boolean confirmacion = mostrarConfirmacionBorrado("Confirmación", "¿Seguro que deseas eliminar el pago " + pagoSeleccionado.getId() + "?");

        if (confirmacion) {
            boolean eliminado = pagoDAO.borrarPago(pagoSeleccionado);
            cargarListaPagos();

            if (eliminado) {
                mostrarAlerta("Éxito", "El pago " + pagoSeleccionado.getId() + " fue eliminado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el pago.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Refresca los datos de la tabla de pagos.
     */
    @FXML
    private void actualizarTablaPagos() {
        cargarListaPagos();
    }

    /**
     * Muestra un mensaje de alerta genérico.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación para eliminar un pago.
     * @return true si el usuario confirma, false en caso contrario.
     */
    private boolean mostrarConfirmacionBorrado(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent();
    }

    private void configurarDobleClickFila() {
        table.setRowFactory(tv -> {
            TableRow<Pago> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY &&
                        event.getClickCount() == 2 &&
                        !row.isEmpty()) {

                    Pago pagoSeleccionado = row.getItem();
                    abrirFormularioEdicion(pagoSeleccionado);

                }
            });

            return row;
        });
    }
    /**
     * Abre el formulario de edición para un pago específico
     */
    private void abrirFormularioEdicion(Pago pago) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PagoFormController pagoFormController = fxmlLoader.getController();
            pagoFormController.cargarPagoParaEditar(pago, null);

            Stage stage = new Stage();
            stage.setTitle("Editar Pago");
            stage.setScene(scene);
            stage.show();

            // Opcional: recargar la tabla cuando se cierre la ventana
            stage.setOnHidden(e -> cargarListaPagos());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de edición", Alert.AlertType.ERROR);
        }
    }

    /**
     * Busca un pago por ID y muestra solo ese resultado en la tabla
     */
    @FXML
    public void buscarPorID(){

        String idTexto=txtBusquedaId.getText();

        if (idTexto.isEmpty()) {
            mostrarAlerta("Validación", "Debes ingresar un ID para buscar", Alert.AlertType.WARNING);
            return;
        }

        try {

            int id = Integer.parseInt(idTexto);

            Pago pagoEncontrado= pagoDAO.buscarPagoPorId(id);

            if (pagoEncontrado!=null){
                ObservableList<Pago> resultado=FXCollections.observableArrayList(pagoEncontrado);
                table.setItems(resultado);
            }else{
                mostrarAlerta("Sin Resultado","No se encontro ningun pago con el Id:  " +idTexto, Alert.AlertType.INFORMATION);
                table.setItems(FXCollections.observableArrayList());
            }

        }catch (Exception e){
            mostrarAlerta("ERROR","El Id debe ser un numero", Alert.AlertType.ERROR);
        }

    }
    /**
     * Muestra todos los pagos en la tabla
     */
    @FXML
    public void mostrarTodosPagos() {
        txtBusquedaId.clear();
        cargarListaPagos();
    }

}
