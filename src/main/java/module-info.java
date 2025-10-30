module com.example.reservavacaciones {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;

        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
            requires net.synedra.validatorfx;
            requires org.kordamp.ikonli.javafx;
            requires org.kordamp.bootstrapfx.core;
            requires com.almasb.fxgl.all;

    requires java.dotenv;
    requires java.sql;
    requires javafx.graphics;
//    requires com.example.reservavacaciones;
//    requires com.example.reservavacaciones;


    opens com.reservas.controller to javafx.fxml;
    exports com.reservas;
    exports com.reservas.controller;
    exports com.reservas.model;
}