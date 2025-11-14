package com.reservas.dao;

import com.reservas.model.Valoracion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Clase DAO para la gestión de valoraciones</h1>
 *
 * Esta clase se encarga de manejar las operaciones CRUD sobre la tabla {@code valoraciones}.
 * Utiliza el modelo {@link Valoracion}
 * <p>
 * Permite registrar, modificar, eliminar y consultar valoraciones, además de obtener información específica
 * mediante identificadores.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 *     <li>Registrar nuevas valoraciones.</li>
 *     <li>Actualizar valoraciones existentes.</li>
 *     <li>Eliminar valoraciones por ID.</li>
 *     <li>Buscar valoraciones por ID de reserva o de valoración.</li>
 *     <li>Listar todas las valoraciones.</li>
 * </ul>
 *
 * @author Sofía Abid
 * @since 05/11/2025
 */
public class ValoracionDAO {

    private final Connection conexion;

    /**
     * Constructor que recibe una conexión activa a la base de datos.
     *
     * @param conexion objeto {@link Connection} ya inicializado.
     */
    public ValoracionDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Inserta una nueva valoración en la base de datos.
     *
     * @param valoracion Objeto {@link Valoracion} con los datos a insertar.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
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
     * Obtiene todas las valoraciones almacenadas en la base de datos.
     *
     * @return Lista de objetos {@link Valoracion}.
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

                if (fechaTS != null){
                    val.setFechaValoracion(fechaTS.toLocalDateTime());
                }

                valoraciones.add(val);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer las valoraciones: " + e.getMessage());
        }

        return valoraciones;
    }

    /**
     * Elimina una valoración de la base de datos.
     *
     * @param valoracion Objeto {@link Valoracion} con el ID a eliminar.
     * @return {@code true} si se eliminó correctamente, {@code false} en caso contrario.
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
     * Modifica los datos de una valoración existente identificada por su ID.
     *
     * @param valoracion Objeto {@link Valoracion} con la información actualizada.
     * @return {@code true} si la modificación fue exitosa, {@code false} en caso contrario.
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
     * Busca todas las valoraciones asociadas a una reserva específica.
     *
     * @param idReserva ID de la reserva a buscar.
     * @return Lista de valoraciones correspondientes a esa reserva.
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

}
