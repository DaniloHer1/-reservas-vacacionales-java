package com.reservas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * <h1>Aplicación Principal</h1>
 *
 * Punto de entrada para la interfaz gráfica del sistema de gestión de reservas.
 * <p>
 * Carga la vista principal definida en FXML, configura el escenario y muestra la ventana
 * con el título y el icono correspondiente.
 * </p>
 *
 * <h2>Características principales:</h2>
 * <ul>
 *     <li>Extiende la clase {@link javafx.application.Application} de JavaFX.</li>
 *     <li>Inicializa la escena principal desde el archivo <code>main-view.fxml</code>.</li>
 *     <li>Establece el título y el icono de la ventana.</li>
 * </ul>
 *
 * @author Daniel Hernando
 * @since 01/11/2025
 */
public class Main extends Application {
    /**
     * Método de inicio de la aplicación JavaFX.
     * <p>
     * Carga el archivo FXML, crea la escena y configura el escenario principal.
     * </p>
     *
     * @param stage escenario principal de la aplicación.
     * @throws IOException si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Gestión de Reservas");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icono.png"))));
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Método principal que lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }
}
