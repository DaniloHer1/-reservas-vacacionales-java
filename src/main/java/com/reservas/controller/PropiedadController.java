package com.reservas.controller;

import com.reservas.dao.PropiedadDAO;
import com.reservas.model.Propiedad;
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
import java.util.List;

import static com.reservas.controller.MainController.mostrarAlerta;

/**
 * Controlador de la vista de gestión de propiedades.
 * Gestiona la interacción entre la interfaz JavaFX y la capa DAO.
 *
 * @author Diego
 * @since 04/11/2025
 */
public class PropiedadController {

    @FXML private TableView<Propiedad> tblPropiedades;
    @FXML private TableColumn<Propiedad, Integer> colIdPropiedad;
    @FXML private TableColumn<Propiedad, String> colNombre;
    @FXML private TableColumn<Propiedad, String> colDireccion;
    @FXML private TableColumn<Propiedad, String> colCiudad;
    @FXML private TableColumn<Propiedad, String> colPais;
    @FXML private TableColumn<Propiedad, Float> colPrecioNoche;
    @FXML private TableColumn<Propiedad, Integer> colCapacidad;
    @FXML private TableColumn<Propiedad, String> colDescripcion;
    @FXML private TableColumn<Propiedad, String> colEstado;

    @FXML private Button btnAgregar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnActualizar;
    @FXML private Button btnBuscar;
    @FXML private TextField txtBusquedaId;
    @FXML private Label lblTotalPropiedades;

    private final javafx.collections.ListChangeListener<Propiedad> contadorListener = c -> actualizarContador();
    private final PropiedadDAO propiedadDAO = new PropiedadDAO();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarPropiedades();

        tblPropiedades.itemsProperty().addListener((obs, oldList, newList) -> {
            if (oldList != null) oldList.removeListener(contadorListener);
            if (newList != null) newList.addListener(contadorListener);
            actualizarContador();
        });

        tblPropiedades.setRowFactory(tv -> {
            TableRow<Propiedad> fila = new TableRow<>();
            fila.setOnMouseClicked(event -> {
                if (!fila.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Propiedad propiedadSeleccionada = fila.getItem();
                    abrirFormularioEdicion(propiedadSeleccionada);
                }
            });
            return fila;
        });
    }

    private void configurarColumnas() {
        colIdPropiedad.setCellValueFactory(new PropertyValueFactory<>("idPropiedad"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        colPrecioNoche.setCellValueFactory(new PropertyValueFactory<>("precio_noche"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado_propiedad"));
    }

    private void cargarPropiedades() {
        List<Propiedad> propiedades = propiedadDAO.leerPropiedades();
        ObservableList<Propiedad> listaPropiedades = FXCollections.observableArrayList(propiedades);
        tblPropiedades.setItems(listaPropiedades);
        if (listaPropiedades != null) {
            listaPropiedades.addListener(contadorListener);
        }
        actualizarContador();
    }

    @FXML
    void agregarPropiedad(ActionEvent event) {
        abrirFormularioEdicion(null);
    }

    @FXML
    void modificarPropiedad(ActionEvent event) {
        Propiedad propiedadSeleccionada = tblPropiedades.getSelectionModel().getSelectedItem();
        if (propiedadSeleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una propiedad para modificar.", Alert.AlertType.WARNING);
            return;
        }
        abrirFormularioEdicion(propiedadSeleccionada);
    }

    @FXML
    void eliminarPropiedad(ActionEvent event) {
        Propiedad propiedadSeleccionada = tblPropiedades.getSelectionModel().getSelectedItem();
        if (propiedadSeleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una propiedad para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación de eliminación");
        confirmacion.setHeaderText("¿Eliminar propiedad?");
        confirmacion.setContentText("¿Seguro que deseas eliminar la propiedad seleccionada?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            propiedadDAO.eliminarPropiedad(propiedadSeleccionada);
            cargarPropiedades();
        }
    }

    private void abrirFormularioEdicion(Propiedad propiedad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservas/propiedades-form-view.fxml"));
            Parent root = loader.load();

            PropiedadFormController controlador = loader.getController();
            controlador.setPropiedad(propiedad);
            controlador.setPropiedadController(this);

            Stage stage = new Stage();
            stage.setTitle(propiedad == null ? "Nueva Propiedad" : "Editar Propiedad");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void actualizarTabla() {
        cargarPropiedades();
        actualizarContador();
    }

    @FXML
    public void buscarPorID() {
        String idTexto = txtBusquedaId.getText();

        if (idTexto == null || idTexto.trim().isEmpty()) {
            cargarPropiedades();
            mostrarAlerta("Información", "Se muestran todas las propiedades.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());
            Propiedad propiedadEncontrada = propiedadDAO.buscarPropiedadPorId(id);

            if (propiedadEncontrada != null) {
                ObservableList<Propiedad> resultado = FXCollections.observableArrayList(propiedadEncontrada);
                tblPropiedades.setItems(resultado);
            } else {
                mostrarAlerta("Sin Resultado", "No se encontró ninguna propiedad con el ID: " + idTexto, Alert.AlertType.INFORMATION);
                cargarPropiedades();
            }
            actualizarContador();

        } catch (NumberFormatException e) {
            mostrarAlerta("ERROR", "El ID debe ser un número válido.", Alert.AlertType.ERROR);
            cargarPropiedades();
        }
    }

    private void actualizarContador() {
        int total = (tblPropiedades.getItems() == null) ? 0 : tblPropiedades.getItems().size();
        lblTotalPropiedades.setText("Total Propiedades: " + total);
    }
}