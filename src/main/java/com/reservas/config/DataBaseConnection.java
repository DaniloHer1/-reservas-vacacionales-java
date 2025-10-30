package com.reservas.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Singleton para la gestión de la conexión a la base de datos.
 * Garantiza que solo exista una única instancia activa de conexión durante el ciclo de vida de la aplicación.
 *
 * @author Daniel Hernando
 * @author Jaime Pérez
 * @since 30/10/2025
 */
public class DataBaseConnection {

    private static DataBaseConnection instance;
    private Connection connection;
    private final String url;
    private final String user;
    private final String password;

    private DataBaseConnection() {

        try {

            Dotenv dotenv = Dotenv.load();
            this.url = dotenv.get("SUPABASE_DB_URL");
            this.user = dotenv.get("SUPABASE_DB_USER");
            this.password = dotenv.get("SUPABASE_DB_PASSWORD");

            if (this.url == null || this.user == null || this.password == null) {

                throw new IllegalStateException("Variables .env no encontradas o vacías");

            }

        } catch (Exception e) {

            System.err.println("Error al cargar configuración: " + e.getMessage());
            throw new RuntimeException("Error al cargar configuración: ", e);

        }

    }

    /**
     * Obtiene la instancia única de la clase
     * @return instancia de la conexión
     */
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


    /**
     * Establece una conexión a la base de datos si no existe o está cerrada.
     * @return objeto Connection
     */
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

    /**
     * Cierra la conexión actual si está abierta.
     */
    public void cerrarConexion() {

        try {

            if (connection != null && !connection.isClosed()) {

                connection.close();
                System.out.println("Conexión cerrada exitosamente");

            }

        } catch (SQLException e) {

            System.err.println("Error al cerrar conexion: " + e.getMessage());
            throw new RuntimeException(e);

        }

    }

}