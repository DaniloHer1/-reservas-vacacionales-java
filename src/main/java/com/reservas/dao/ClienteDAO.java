package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Cliente;
import com.reservas.model.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona las operaciones CRUD sobre la tabla clientes.
 * La identificación de registros se realiza mediante consultas basadas en el campo {@code email}, que es único.
 *
 * @author Jaime Pérez
 * @since 30/10/2025
 */
public class ClienteDAO {

    /**
     * Inserta un cliente en la base de datos.
     *
     * @param cliente Objet {@link Cliente} con los datos a registrar.
     * @return {@code true} si la operación ha sido exitosa, {@code false} si se produjo un error.
     */
    public boolean agregarCliente(Cliente cliente) {

        String query = """
                       INSERT INTO clientes(nombre, apellidos, email, telefono, pais)
                       VALUES (?, ?, ?, ?, ?);
                       """;

        try(Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps =  con.prepareStatement(query)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getPais());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.err.println("Error al agregar el cliente: " + e.getMessage());
            return false;

        }

    }

    /**
     * Obtiene todos los clientes registrados en la base de datos.
     *
     * @return Lista de objetos {@link Cliente} obtenidos desde la tabla {@code clientes}.
     *         Si no existen registros, se devuelve una lista vacía.
     */
    public List<Cliente> leerClientes() {

        List<Cliente> clientes = new ArrayList<>();

        String query = """
                       SELECT id_cliente, nombre, apellidos, email, telefono, pais, fecha_registro FROM clientes;
                       """;

        try(Connection con = DataBaseConnection.getInstance().conectarBD(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {

                int id_cliente = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellidos");
                String email = rs.getString("email");
                String telefono = rs.getString("telefono");
                String pais = rs.getString("pais");

                var cliente = new Cliente(nombre, apellido, email, telefono, pais);

                cliente.setIdCliente(id_cliente);

                Date fechaRegistro = rs.getDate("fecha_registro");

                if (fechaRegistro != null) {

                    cliente.setFechaRegistro(fechaRegistro.toLocalDate());

                }

                clientes.add(cliente);

            }

        } catch (SQLException e) {

            System.err.println("Error al leer el cliente: " + e.getMessage());
            throw new RuntimeException(e);

        }

        return clientes;

    }

    /**
     * Modifica los datos de un cliente existente en la base de datos.
     *
     * @param cliente Objeto {@link Cliente} con los datos actualizados.
     * @return El objeto {@link Cliente} actualizado si la operación fue exitosa, o {@code null} si no se encontró el cliente.
     */
    public Cliente modificarCliente(Cliente cliente) {

        String query = """
                       UPDATE clientes
                       SET nombre = ?, apellidos = ?, email = ?, telefono = ?, pais = ?
                       WHERE id_cliente = ?;
                       """;

        int idCliente =buscarClientePorEmail(cliente.getEmail());

        if (idCliente == -1) {

            System.out.println("No existe el cliente con el email: " + cliente.getEmail());

            return null;

        }

        try(Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getPais());
            ps.setInt(6, idCliente);

            int filas = ps.executeUpdate();

            if (filas > 0) {

                System.out.println("El cliente " + cliente.getNombre() + " se ha actualizado exitosamente");
                return cliente;

            } else {

                System.err.println("Error al actualizar el cliente: " + cliente.getEmail());
                return null;

            }

        } catch (SQLException e) {

            System.err.println("Error al modificar el cliente: " + e.getMessage());
            throw new RuntimeException(e);

        }

    }

    /**
     * Elimina un cliente existente en la base de datos.
     *
     * @param cliente Objeto {@link Cliente} con el email del cliente a eliminar.
     */
    public void eliminarCliente(Cliente cliente) {

        String query = """
                       DELETE FROM clientes WHERE id_cliente = ?;
                       """;

        int idCliente = buscarClientePorEmail(cliente.getEmail());

        if (idCliente == -1) {

            System.err.println("No se encontró el cliente con email: " + cliente.getEmail());
            return;

        }

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, idCliente);
            int filas = ps.executeUpdate();

            if (filas > 0) {

                System.out.println("Cliente eliminado correctamente: " + cliente.getEmail());

            } else {

                System.err.println("No se pudo eliminar el cliente: " + cliente.getEmail());

            }

        } catch (SQLException e) {

            System.err.println("Error al eliminar el cliente: " + e.getMessage());
            throw new RuntimeException(e);

        }
    }

    /**
     * Busca el identificador único de un cliente a partir de su dirección de {@code email}.
     *
     * @param email, atributo de carácter único con el que se realiza la búsqueda del usuario.
     * @return El valor de {@code id_cliente} si existe el registro, o {@code -1} si no se encontró.
     */
    public int buscarClientePorEmail (String email) {

        String query = """
                       SELECT id_cliente FROM clientes WHERE email = ?;
                       """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return rs.getInt("id_cliente");

            }

        } catch (SQLException e) {

            System.err.println("Error al buscar cliente: " + e.getMessage());
            throw new RuntimeException(e);

        }

        return -1;

    }

    /**
     * Modifica un cliente existente en la base de datos.
     *
     * @param cliente Objeto {@link Cliente} con el email del cliente a modificar.
     * @return {@code true} si la operación ha sido exitosa, {@code false} si se produjo un error.
     */
    public boolean modificarClientePorId(Cliente cliente) {

        String query = """
                    UPDATE clientes
                       SET nombre = ?, apellidos = ?, email = ?, telefono = ?, pais = ?
                     WHERE id_cliente = ?;
                    """;

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getPais());
            ps.setInt(6, cliente.getIdCliente());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {

            System.err.println("Error al modificar el cliente: " + e.getMessage());
            throw new RuntimeException(e);

        }

    }

    /**
     * Busca en la base de datos e inserta los identificadores de los clientes dentro de un ArrayList.
     *
     * @return lista de los id clientes
     */
    public ArrayList<Integer> getIDClientes(){

        ArrayList<Integer> ids = new ArrayList<>();

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); Statement st = con.createStatement()) {

            st.execute("select id_cliente from clientes;");
            ResultSet rs = st.getResultSet();

            while (rs.next()){

               ids.add(rs.getInt("id_cliente"));

            }

        } catch (SQLException e) {


            throw new RuntimeException(e);

        }

        return ids;

    }
}
