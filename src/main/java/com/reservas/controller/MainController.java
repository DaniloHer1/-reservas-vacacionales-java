package com.reservas.controller;

import com.reservas.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    public void abrirVistaClientes() {
        cargarVista("clientes-view.fxml", "Gestión de clientes");
    }
    @FXML
    public void abrirVistaPropiedades() {
        cargarVista("propiedades-view.fxml", "Gestión de propiedades");
    }
    @FXML
    public void abrirVistaReservas() {
        cargarVista("reservas-view.fxml", "Gestión de reservas");
    }
    @FXML
    public void abrirVistaPagos() {
        cargarVista("pagos-view.fxml", "Gestión de pagos");
    }
    @FXML
    public void abrirVistaValoraciones() {
        cargarVista("valoraciones-view.fxml", "Valoraciones");
    }
    protected static void cargarVista(String ruta, String titulo) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(ruta));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(titulo);
        stage.show();
    }

    /**
     * Muestra un mensaje de alerta genérico.
     */
    protected static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
