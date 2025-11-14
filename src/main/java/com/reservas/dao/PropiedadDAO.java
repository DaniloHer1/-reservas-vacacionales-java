package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Propiedad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Clase DAO para la gestión de propiedades</h1>
 *
 * Esta clase administra todas las operaciones de acceso a datos (CRUD) relacionadas con la entidad {@link Propiedad}.
 * <p>
 * Permite registrar, modificar, eliminar y consultar propiedades en la base de datos. Las búsquedas
 * pueden darse por <b>ID</b>.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 *     <li>Registrar nuevas propiedades.</li>
 *     <li>Actualizar propiedades existentes.</li>
 *     <li>Eliminar propiedades por nombre o ID.</li>
 *     <li>Listar todas las propiedades registradas.</li>
 *     <li>Obtener identificadores de propiedades para formularios o combos.</li>
 * </ul>
 *
 * @author Diego Regueira
 * @since 04/11/2025
 */
public class PropiedadDAO {

    /**
     * Inserta una propiedad en la base de datos.
     *
     * @param propiedad Objeto Propiedad con los datos a registrar.
     * @return true si la operación ha sido exitosa, false si se produjo un error.
     */
    public boolean agregarPropiedad(Propiedad propiedad) {

        String query = """
                       INSERT INTO propiedades(nombre, direccion, ciudad, pais, precio_noche, capacidad, descripcion, estado_propiedad)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                       """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, propiedad.getNombre());
            ps.setString(2, propiedad.getDireccion());
            ps.setString(3, propiedad.getCiudad());
            ps.setString(4, propiedad.getPais());
            ps.setFloat(5, propiedad.getPrecio_noche());
            ps.setInt(6, propiedad.getCapacidad());
            ps.setString(7, propiedad.getDescripcion());
            ps.setString(8, propiedad.getEstado_propiedad());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("Error al agregar la propiedad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las propiedades registradas en la base de datos.
     *
     * @return Lista de objetos {@link Propiedad}. Si no existen registros, se devuelve una lista vacía.
     */
    public List<Propiedad> leerPropiedades() {

        List<Propiedad> propiedades = new ArrayList<>();

        String query = """
                       SELECT id_propiedad, nombre, direccion, ciudad, pais, precio_noche, capacidad, descripcion, estado_propiedad
                       FROM propiedades;
                       """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {

                int idPropiedad = rs.getInt("id_propiedad");
                String nombre = rs.getString("nombre");
                String direccion = rs.getString("direccion");
                String ciudad = rs.getString("ciudad");
                String pais = rs.getString("pais");
                float precio_noche = rs.getFloat("precio_noche");
                int capacidad = rs.getInt("capacidad");
                String descripcion = rs.getString("descripcion");
                String estado_propiedad = rs.getString("estado_propiedad");

                var propiedad = new Propiedad(idPropiedad, nombre, direccion, ciudad, pais,
                        precio_noche, capacidad, descripcion, estado_propiedad);

                propiedades.add(propiedad);

            }

        } catch (SQLException e) {

            System.err.println("Error al leer las propiedades: " + e.getMessage());
            throw new RuntimeException(e);

        }

        return propiedades;
    }

    /**
     * Modifica los datos de una propiedad existente utilizando su nombre como identificador único.
     *
     * @param propiedad Objeto {@link Propiedad} con los datos actualizados.
     * @return El objeto {@link Propiedad} actualizado si la operación fue exitosa,
     * o {@code null} si no se encontró la propiedad.
     */
    public Propiedad modificarPropiedad(Propiedad propiedad) {

        String query = """
                       UPDATE propiedades
                       SET nombre = ?, direccion = ?, ciudad = ?, pais = ?, precio_noche = ?, capacidad = ?, descripcion = ?, estado_propiedad = ?
                       WHERE id_propiedad = ?;
                       """;

        int idPropiedad = buscarPropiedadPorNombre(propiedad.getNombre());

        if (idPropiedad == -1) {

            System.out.println("No existe la propiedad con el nombre: " + propiedad.getNombre());
            return null;

        }

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, propiedad.getNombre());
            ps.setString(2, propiedad.getDireccion());
            ps.setString(3, propiedad.getCiudad());
            ps.setString(4, propiedad.getPais());
            ps.setFloat(5, propiedad.getPrecio_noche());
            ps.setInt(6, propiedad.getCapacidad());
            ps.setString(7, propiedad.getDescripcion());
            ps.setString(8, propiedad.getEstado_propiedad());
            ps.setInt(9, idPropiedad);

            int filas = ps.executeUpdate();

            if (filas > 0) {

                System.out.println("La propiedad " + propiedad.getNombre() + " se ha actualizado correctamente.");
                return propiedad;

            } else {

                System.err.println("Error al actualizar la propiedad: " + propiedad.getNombre());
                return null;

            }

        } catch (SQLException e) {
            System.err.println("Error al modificar la propiedad: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Elimina una propiedad existente en la base de datos utilizando su nombre.
     *
     * @param propiedad Objeto {@link Propiedad} con el nombre a eliminar.
     */
    public void eliminarPropiedad(Propiedad propiedad) {

        String query = """
                       DELETE FROM propiedades WHERE id_propiedad = ?;
                       """;

        int idPropiedad = buscarPropiedadPorNombre(propiedad.getNombre());

        if (idPropiedad == -1) {

            System.err.println("No se ha encontrado la propiedad con nombre: " + propiedad.getNombre());
            return;

        }

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, idPropiedad);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Propiedad eliminada correctamente: " + propiedad.getNombre());
            } else {
                System.err.println("No se ha podido eliminar la propiedad: " + propiedad.getNombre());
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar la propiedad: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Busca el identificador único de una propiedad a partir de su nombre.
     *
     * @param nombre, atributo con el que se realiza la búsqueda.
     * @return El valor de id_propiedad si existe el registro, o -1 si no se ha encontrado.
     */
    public int buscarPropiedadPorNombre(String nombre) {

        String query = """
                       SELECT id_propiedad FROM propiedades WHERE nombre = ?;
                       """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { return rs.getInt("id_propiedad"); }

        } catch (SQLException e) {

            System.err.println("Error al buscar propiedad: " + e.getMessage());
            throw new RuntimeException(e);

        }

        return -1;
    }

    /**
     * Modifica una propiedad utilizando su identificador.
     *
     * @param propiedad Objeto {@link Propiedad} con el ID y los datos a modificar.
     * @return {@code true} si la operación fue exitosa.
     */
    public boolean modificarPropiedadPorId(Propiedad propiedad) {

        String query = """
                       UPDATE propiedades
                       SET nombre = ?, direccion = ?, ciudad = ?, pais = ?, precio_noche = ?, capacidad = ?, descripcion = ?, estado_propiedad = ?
                       WHERE id_propiedad = ?;
                       """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, propiedad.getNombre());
            ps.setString(2, propiedad.getDireccion());
            ps.setString(3, propiedad.getCiudad());
            ps.setString(4, propiedad.getPais());
            ps.setFloat(5, propiedad.getPrecio_noche());
            ps.setInt(6, propiedad.getCapacidad());
            ps.setString(7, propiedad.getDescripcion());
            ps.setString(8, propiedad.getEstado_propiedad());
            ps.setInt(9, propiedad.getIdPropiedad());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar la propiedad: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene una lista con los identificadores de todas las propiedades existentes.
     *
     * @return Lista de IDs de propiedades.
     */
    public ArrayList<Integer> getIDPropiedades() {

        ArrayList<Integer> ids = new ArrayList<>();

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             Statement st = con.createStatement()) {

            st.execute("SELECT id_propiedad FROM propiedades;");
            ResultSet rs = st.getResultSet();

            while (rs.next()) {
                ids.add(rs.getInt("id_propiedad"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener IDs de propiedades: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return ids;
    }

    /**
     * Busca una propiedad en la base de datos por su identificador.
     *
     * @param idPropiedad ID único de la propiedad.
     * @return Objeto {@link Propiedad} si se encontró, o {@code null} si no existe.
     */
    public Propiedad buscarPropiedadPorId(int idPropiedad) {

        String query = """
                   SELECT * FROM propiedades WHERE id_propiedad = ?;
                   """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, idPropiedad);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Propiedad(
                        rs.getInt("id_propiedad"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("ciudad"),
                        rs.getString("pais"),
                        rs.getFloat("precio_noche"),
                        rs.getInt("capacidad"),
                        rs.getString("descripcion"),
                        rs.getString("estado_propiedad")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar propiedad por ID: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }
}
