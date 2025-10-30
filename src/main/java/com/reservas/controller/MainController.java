package com.reservas.controller;

import com.reservas.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnPropiedades;

    @FXML
    private Button btnReservas;

    @FXML
    private Button btnPagos;

    @FXML
    private Button btnValoraciones;

    @FXML
    public void abrirVistaClientes() {
        cargarVista("clientes-view.fxml", btnClientes);
    }

    @FXML
    public void abrirVistaPropiedades() {
        cargarVista("propiedades-view.fxml", btnPropiedades);
    }

    @FXML
    public void abrirVistaReservas() {
        cargarVista("reservas-view.fxml", btnReservas);
    }

    @FXML
    public void abrirVistaPagos() {
        cargarVista("pagos-view.fxml", btnPagos);
    }

    @FXML
    public void abrirVistaValoraciones() {
        cargarVista("valoraciones-view.fxml", btnValoraciones);
    }


    protected void cargarVista(String ruta, Button boton){

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(ruta));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();


    }
}
