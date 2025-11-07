package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Valoracion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO que gestiona las operaciones CRUD sobre la tabla "valoraciones".
 * Usa JDBC y el modelo {@link Valoracion}.
 *
 * @author
 * Sofía Abid
 */
public class ValoracionDAO {

    private final Connection conexion;

    public ValoracionDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Inserta una valoración en la base de datos.
     */
    public boolean agregarValoracion(Valoracion valoracion) {
        String query = """
                INSERT INTO valoraciones (id_reserva, puntuacion, comentario, anonima, fecha_valoracion)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, valoracion.getReserva());
            ps.setInt(2, valoracion.getPuntuacion());
            ps.setString(3, valoracion.getComentario());
            ps.setBoolean(4, valoracion.isAnonimato());
            ps.setTimestamp(5, Timestamp.valueOf(valoracion.getFechaValoracion()));

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al agregar la valoración: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las valoraciones registradas en la base de datos.
     */
    public List<Valoracion> leerValoraciones() {
        List<Valoracion> valoraciones = new ArrayList<>();

        String query = """
                SELECT id_valoracion, id_reserva, puntuacion, comentario, anonima, fecha_valoracion
                FROM valoraciones;
                """;

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                int idValoracion = rs.getInt("id_valoracion");
                int idReserva = rs.getInt("id_reserva");
                int puntuacion = rs.getInt("puntuacion");
                String comentario = rs.getString("comentario");
                boolean anonima = rs.getBoolean("anonima");
                Timestamp fechaTS = rs.getTimestamp("fecha_valoracion");

                Valoracion val = new Valoracion(idValoracion, idReserva, puntuacion, comentario, anonima);
                if (fechaTS != null)
                    val.setFechaValoracion(fechaTS.toLocalDateTime());

                valoraciones.add(val);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer las valoraciones: " + e.getMessage());
        }

        return valoraciones;
    }

    /**
     * Elimina una valoración existente.
     */
    public boolean eliminarValoracion(Valoracion valoracion) {
        String query = "DELETE FROM valoraciones WHERE id_valoracion = ?;";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, valoracion.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la valoración: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica una valoración existente.
     */
    public boolean modificarValoracionPorId(Valoracion valoracion) {
        String query = """
                UPDATE valoraciones
                SET id_reserva = ?, puntuacion = ?, comentario = ?, anonima = ?
                WHERE id_valoracion = ?;
                """;

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, valoracion.getReserva());
            ps.setInt(2, valoracion.getPuntuacion());
            ps.setString(3, valoracion.getComentario());
            ps.setBoolean(4, valoracion.isAnonimato());
            ps.setInt(5, valoracion.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar la valoración: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca valoraciones por ID de reserva.
     */
    public List<Valoracion> buscarPorIDReserva(int idReserva) {
        List<Valoracion> valoraciones = new ArrayList<>();

        String query = "SELECT * FROM valoraciones WHERE id_reserva = ?;";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idValoracion = rs.getInt("id_valoracion");
                int puntuacion = rs.getInt("puntuacion");
                String comentario = rs.getString("comentario");
                boolean anonima = rs.getBoolean("anonima");
                Timestamp fechaTS = rs.getTimestamp("fecha_valoracion");

                Valoracion val = new Valoracion(idValoracion, idReserva, puntuacion, comentario, anonima);
                if (fechaTS != null)
                    val.setFechaValoracion(fechaTS.toLocalDateTime());

                valoraciones.add(val);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por ID de reserva: " + e.getMessage());
        }

        return valoraciones;
    }

    /**
     * Busca valoraciones por ID de valoración.
     */
    public List<Valoracion> buscarPorIDValoracion(int idValoracion) {
        List<Valoracion> valoraciones = new ArrayList<>();

        String query = "SELECT * FROM valoraciones WHERE id_valoracion = ?;";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, idValoracion);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idReserva = rs.getInt("id_reserva");
                int puntuacion = rs.getInt("puntuacion");
                String comentario = rs.getString("comentario");
                boolean anonima = rs.getBoolean("anonima");
                Timestamp fechaTS = rs.getTimestamp("fecha_valoracion");

                Valoracion val = new Valoracion(idValoracion, idReserva, puntuacion, comentario, anonima);
                if (fechaTS != null)
                    val.setFechaValoracion(fechaTS.toLocalDateTime());

                valoraciones.add(val);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por ID de valoración: " + e.getMessage());
        }

        return valoraciones;
    }
}
