package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Cliente;
import com.reservas.model.Valoracion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona las operaciones CRUD sobre la tabla Valoraciones.
 * La identificación de registros se realiza mediante consultas basadas en el campo {@code Valoracion.id}, que es único.
 *
 * @author Sofía Abid
 * @since 04/11/2025
 */

public class ValoracionDAO {
    /**
     * Inserta una valoración en la base de datos.
     *
     * @param valoracion Objet {@link com.reservas.model.Valoracion} con los datos a registrar.
     * @return {@code true} si la operación ha sido exitosa, {@code false} si se produjo un error.
     */
    public boolean agregarValoracion(Valoracion valoracion) {

        String query = """
                       INSERT INTO valoraciones(id_reserva, puntuacion, comentario, anonima, fecha_valoracion)
                       VALUES (?, ?, ?, ?, ?);
                       """;

        try(Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps =  con.prepareStatement(query)) {

            ps.setInt(1, valoracion.getReseva());
            ps.setInt(2, valoracion.getPuntuacion());
            ps.setString(3, valoracion.getComentario());
            ps.setBoolean(4, valoracion.isAnonimato());
            ps.setTimestamp(5, Timestamp.valueOf(valoracion.getFechaValoracion()));

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.err.println("Error al agregar la valoracion: " + e.getMessage());
            return false;

        }

    }

    /**
     * Obtiene todos las valoraciones registradas en la base de datos.
     *
     * @return Lista de objetos {@link Valoracion} obtenidos desde la tabla {@code valoraciones}.
     *         Si no existen registros, se devuelve una lista vacía.
     */
    public List<Valoracion> leerValoraciones() {

        List<Valoracion> valoraciones = new ArrayList<>();

        String query = """
                       SELECT id_valoracion, id_reserva, puntuacion, comentario, anonima, fecha_valoracion FROM valoraciones;
                       """;

        try(Connection con = DataBaseConnection.getInstance().conectarBD(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {

                int id_valoracion = rs.getInt("id_valoracion");
                int id_reserva = rs.getInt("id_reserva");
                int puntuacion = rs.getInt("puntuacion");
                String comentario = rs.getString("comentario");
                boolean anonima = rs.getBoolean("anonima");
                LocalDateTime fecha_valoracion = rs.getTimestamp("fecha_valoracion").toLocalDateTime();

                var valoracion = new Valoracion(id_reserva, puntuacion, comentario, anonima);

                if (fecha_valoracion != null) {
                    valoracion.setFechaValoracion(fecha_valoracion);
                }

                valoracion.setId(id_valoracion);

                valoraciones.add(valoracion);

            }

        } catch (SQLException e) {

            System.err.println("Error al leer las valoraciones: " + e.getMessage());
            throw new RuntimeException(e);

        }

        return valoraciones;

    }

    /**
     * Elimina una valoracion existente en la base de datos.
     *
     * @param valoracion Objeto {@link Valoracion} con el id de la valoración a eliminar.
     */
    public void eliminarValoracion(Valoracion valoracion) {

        String query = """
                       DELETE FROM valoraciones WHERE id_valoracion = ?;
                       """;

        int idValoracion = valoracion.getId();

        if (idValoracion == -1) {

            System.err.println("No se encontró la valoración con id: " + valoracion.getId());
            return;

        }

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, idValoracion);
            int filas = ps.executeUpdate();

            if (filas > 0) {

                System.out.println("Valoración eliminada correctamente: " + valoracion.getId());

            } else {

                System.err.println("No se pudo eliminar la valoración: " + valoracion.getId());

            }

        } catch (SQLException e) {

            System.err.println("Error al eliminar la valoración: " + e.getMessage());
            throw new RuntimeException(e);

        }
    }


    /**
     * Modifica una valoración existente en la base de datos.
     *
     * @param valoracion Objeto {@link Valoracion} con id de la valoración a modificar.
     * @return {@code true} si la operación ha sido exitosa, {@code false} si se produjo un error.
     */
    public boolean modificarValoracionPorId(Valoracion valoracion) {

        String query = """
                    UPDATE valoraciones
                       SET id_reserva = ?, puntuacion = ?, comentario = ?, anonima = ?
                     WHERE id_valoracion = ?;
                    """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, valoracion.getReseva());
            ps.setInt(2, valoracion.getPuntuacion());
            ps.setString(3, valoracion.getComentario());
            ps.setBoolean(4, valoracion.isAnonimato());
            ps.setInt(5, valoracion.getId());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {

            System.err.println("Error al modificar la valoración: " + e.getMessage());
            throw new RuntimeException(e);

        }

    }

    /**
     * Busca en la base de datos e inserta las valorciones encontradas dentro de un ArrayList.
     *
     * @return lista de las valoraciones o una vacía si no se ha encontrado nada
     */
    public ArrayList<Valoracion> buscarPorIDReserva(int idReserva) {

        ArrayList<Valoracion> valoraciones = new ArrayList<>();

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); Statement st = con.createStatement()) {

            st.execute("SELECT * from valoraciones where id_reserva = ?;");
            ResultSet rs = st.getResultSet();

            while (rs.next()){

                int id_valoracion = rs.getInt("id_valoracion");
                int id_reserva = rs.getInt("id_reserva");
                int puntuacion = rs.getInt("puntuacion");
                String comentario = rs.getString("comentario");
                boolean anonima = rs.getBoolean("anonima");
                LocalDateTime fecha_valoracion = rs.getTimestamp("fecha_valoracion").toLocalDateTime();

                var valoracion = new Valoracion(id_reserva, puntuacion, comentario, anonima);

                if (fecha_valoracion != null) {
                    valoracion.setFechaValoracion(fecha_valoracion);
                }

                valoracion.setId(id_valoracion);

                valoraciones.add(valoracion);

            }

        } catch (SQLException e) {

            System.err.println("Error al buscar la(s) valoración: " + e.getMessage());
            throw new RuntimeException(e);

        }

        return valoraciones;

    }
}


