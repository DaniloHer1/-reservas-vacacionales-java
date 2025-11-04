package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Propiedad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona las operaciones CRUD sobre la tabla propiedades.
 * La identificación de registros se realiza mediante consultas basadas en el campo {@code nombre}, que se considera único.
 *
 * @author Diego
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
     * @return Lista de objetos Propiedad obtenidos desde la tabla propiedades.
     *         Si no existen registros, se devuelve una lista vacía.
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
     * Modifica los datos de una propiedad existente en la base de datos.
     *
     * @param propiedad Objeto Propiedad con los datos actualizados.
     * @return El objeto Propiedad actualizado si la operación fue exitosa, o null} si no se encontró la propiedad.
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
                System.out.println("La propiedad " + propiedad.getNombre() + " se ha actualizado exitosamente");
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
     * Elimina una propiedad existente en la base de datos.
     *
     * @param propiedad Objeto Propiedad con el nombre de la propiedad a eliminar.
     */
    public void eliminarPropiedad(Propiedad propiedad) {

        String query = """
                       DELETE FROM propiedades WHERE id_propiedad = ?;
                       """;

        int idPropiedad = buscarPropiedadPorNombre(propiedad.getNombre());

        if (idPropiedad == -1) {
            System.err.println("No se encontró la propiedad con nombre: " + propiedad.getNombre());
            return;
        }

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, idPropiedad);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Propiedad eliminada correctamente: " + propiedad.getNombre());
            } else {
                System.err.println("No se pudo eliminar la propiedad: " + propiedad.getNombre());
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
     * @return El valor de id_propiedad si existe el registro, o -1 si no se encontró.
     */
    public int buscarPropiedadPorNombre(String nombre) {

        String query = """
                       SELECT id_propiedad FROM propiedades WHERE nombre = ?;
                       """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_propiedad");
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar propiedad: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return -1;
    }

    /**
     * Modifica una propiedad existente en la base de datos utilizando su identificador.
     *
     * @param propiedad Objeto Propiedad con el ID y los datos a modificar.
     * @return true si la operación ha sido exitosa, false} si se produjo un error.
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
     * Obtiene los identificadores de todas las propiedades existentes.
     *
     * @return Lista de los identificadores de las propiedades registradas.
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
