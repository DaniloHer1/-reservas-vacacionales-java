package com.reservas.controller;

import com.reservas.Main;
import com.reservas.config.DataBaseConnection;
import com.reservas.dao.PagoDAO;
import com.reservas.model.Pago;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

public class PagoController {

    @FXML
    private TableView<Pago> table;

    @FXML
    private TableColumn<Pago, Integer> colId;

    @FXML
    private TableColumn<Pago, String> colReserva;

    @FXML
    private TableColumn<Pago, LocalDateTime> colFecha;

    @FXML
    private TableColumn<Pago, Double> colMonto;


    @FXML
    private TableColumn<Pago, Pago.MetodoPago> colMetodo;

    @FXML
    private TableColumn<Pago, Pago.EstadoPago> colEstado;



    private PagoDAO sacarDatos;

    @FXML
    public void initialize() {
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        sacarDatos=new PagoDAO(connection);
        configurarColumnas();
        cargarPagos();
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReserva.setCellValueFactory(new PropertyValueFactory<>("reserva"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }
    public void cargarPagos(){

        sacarDatos.mostrarTodosPagos();
        ObservableList<Pago> listaPagos = FXCollections.observableArrayList(
                sacarDatos.getPagosDisponibles()
        );
        table.setItems(listaPagos);

        sacarDatos.getPagosDisponibles().forEach((empleado)->{

            System.out.println(empleado);
        });

    }
    @FXML
    public void añadirPago() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pagos-form-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.getStackTrace();
        }

        Stage stage =new Stage();

        stage.setTitle("Añadir Pago");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void actualizarTabla(){
        Connection connection = DataBaseConnection.getInstance().conectarBD();
        sacarDatos=new PagoDAO(connection);
    }


}
