package com.reservas.controller;

import com.reservas.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * <h1>Controlador principal del sistema de reservas</h1>
 *
 * Este controlador gestiona la navegación entre las distintas vistas principales del sistema:
 * <ul>
 *     <li>Clientes</li>
 *     <li>Propiedades</li>
 *     <li>Reservas</li>
 *     <li>Pagos</li>
 *     <li>Valoraciones</li>
 * </ul>
 *
 * Además, incluye un método genérico para mostrar alertas en pantalla
 * y otro para cargar cualquier vista FXML dentro de una nueva ventana.
 *
 * @author Daniel Hernando
 * @since 31/10/2025
 */
public class MainController {

    /**
     * Abre la vista de gestión de clientes.
     */
    @FXML
    public void abrirVistaClientes() {
        cargarVista("clientes-view.fxml", "Gestión de clientes");
    }

    /**
     * Abre la vista de gestión de propiedades.
     */
    @FXML
    public void abrirVistaPropiedades() {
        cargarVista("propiedades-view.fxml", "Gestión de propiedades");
    }

    /**
     * Abre la vista de gestión de reservas.
     */
    @FXML
    public void abrirVistaReservas() {
        cargarVista("reservas-view.fxml", "Gestión de reservas");
    }

    /**
     * Abre la vista de gestión de pagos.
     */
    @FXML
    public void abrirVistaPagos() {
        cargarVista("pagos-view.fxml", "Gestión de pagos");
    }

    /**
     * Abre la vista de gestión de valoraciones.
     */
    @FXML
    public void abrirVistaValoraciones() {
        cargarVista("valoraciones-view.fxml", "Valoraciones");
    }

    /**
     * Carga una vista FXML específica y la muestra en una nueva ventana.
     *
     * @param ruta ruta del archivo FXML.
     * @param titulo título que se mostrará en la barra de la ventana.
     */
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
     * Muestra una alerta genérica en pantalla.
     * <p>
     *     Este método se puede invocar desde cualquier controlador para unificar el estilo de alertas.
     * </p>
     *
     * @param titulo título del cuadro de diálogo.
     * @param mensaje mensaje que se mostrará en el cuerpo de la alerta.
     * @param tipo tipo de alerta (INFORMATION, WARNING, ERROR, etc.).
     */
    protected static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
