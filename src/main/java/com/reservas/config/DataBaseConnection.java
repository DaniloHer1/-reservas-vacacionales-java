package com.reservas.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static DataBaseConnection instance;
    private Connection connection;
    private Dotenv dotenv;
    private String url;
    private String user;
    private String password;


    private DataBaseConnection() {
        try {
            dotenv = Dotenv.load();
            url = dotenv.get("SUPABASE_DB_URL");
            user = dotenv.get("SUPABASE_DB_USER");
            password = dotenv.get("SUPABASE_DB_PASSWORD");


        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static DataBaseConnection getInstance() {
        if (instance == null) {
            synchronized (DataBaseConnection.class) {
                if (instance == null) {
                    instance = new DataBaseConnection();
                }
            }
        }
        return instance;
    }


    public Connection conectarBD() {
        try {

            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println(" Conexión establecida exitosamente");
            }
        } catch (SQLException e) {
            System.err.println(" Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return connection;
    }









}