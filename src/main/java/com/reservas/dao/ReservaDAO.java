package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Reserva;

import java.sql.*;
import java.util.ArrayList;

/**
 * <h1>Clase DAO para la gestión de reservas</h1>
 *
 * Esta clase se encarga de manejar las operaciones de acceso a datos (CRUD) sobre la tabla {@code reservas}.
 * <p>
 * Permite registrar, modificar, eliminar y consultar reservas, además de obtener información específica
 * mediante identificadores. Utiliza {@link DataBaseConnection} para establecer la conexión con la base de datos.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 *     <li>Registrar nuevas reservas.</li>
 *     <li>Actualizar datos de reservas existentes.</li>
 *     <li>Eliminar reservas por ID.</li>
 *     <li>Buscar reservas individuales por identificador.</li>
 *     <li>Listar todas las reservas registradas.</li>
 * </ul>
 *
 * @author Pablo Armas
 * @since 04/11/2025
 */
public class ReservaDAO {

    ArrayList<Reserva> reservas;

    /**
     * Constructor que inicializa la lista de reservas.
     */
    public ReservaDAO() {
        reservas = new ArrayList<>();
    }

    /**
     * Obtiene todas las reservas registradas en la base de datos.
     *
     * @return Lista de objetos {@link Reserva}.
     */
    public ArrayList<Reserva> getReservas(){

        String sql = "select id_reserva, id_cliente, id_propiedad, fecha_inicio," +
                " fecha_fin, num_personas, estado, precio_total, motivo_cancelacion from reservas";

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); Statement statement = con.createStatement()){

            statement.executeQuery(sql);
            ResultSet rs = statement.getResultSet();

            while (rs.next()){

                Reserva reserva = getReservaFromResultSet(rs);
                reservas.add(reserva);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return reservas;
    }

    /**
     * Inserta una nueva reserva en la base de datos.
     *
     * @param r Objeto {@link Reserva} con los datos a registrar.
     * @return Número de filas afectadas (1 si se insertó correctamente).
     */
    public int aniadirReserva(Reserva r){

        String sql = "insert into reservas (id_cliente, id_propiedad, fecha_inicio, fecha_fin," +
                "num_personas, estado, precio_total, motivo_cancelacion) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection con = DataBaseConnection.getInstance().conectarBD();PreparedStatement ps = con.prepareStatement(sql)){

            ps.setInt(1, r.getId_cliente());
            ps.setInt(2, r.getId_propiedad());
            ps.setDate(3, r.getFecha_inicio());
            ps.setDate(4, r.getFecha_fin());
            ps.setInt(5, r.getNum_personas());
            ps.setString(6, r.getEstadoReserva().toString().toLowerCase());
            ps.setDouble(7, r.getPrecio_total());
            ps.setString(8, r.getMotivo_cancelacion());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Modifica los datos de una reserva existente.
     *
     * @param r Objeto {@link Reserva} con la información actualizada.
     * @return Número de filas afectadas (1 si la actualización fue exitosa).
     */
    public int modificarReserva(Reserva r){

        String sql = "update reservas set id_propiedad=?, fecha_inicio=?, fecha_fin=?," +
                "num_personas=?, estado=?, precio_total=?, motivo_cancelacion=?, id_cliente=? where id_reserva=?;";

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(sql)){

            ps.setInt(1, r.getId_propiedad());
            ps.setDate(2, r.getFecha_inicio());
            ps.setDate(3, r.getFecha_fin());
            ps.setInt(4, r.getNum_personas());
            ps.setString(5, r.getEstadoReserva().toString().toLowerCase());
            ps.setDouble(6, r.getPrecio_total());
            ps.setString(7, r.getMotivo_cancelacion());
            ps.setInt(8, r.getId_cliente());
            ps.setInt(9, r.getId_reserva());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Elimina una reserva existente en la base de datos.
     *
     * @param r Objeto {@link Reserva} con el ID de la reserva a eliminar.
     * @return Número de filas afectadas (1 si se eliminó correctamente).
     */
    public int eliminarReserva(Reserva r){

        String sql = "delete from reservas where id_reserva=?;";

        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(sql)){

           ps.setInt(1, r.getId_reserva());
           return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Busca una reserva específica por su identificador único.
     *
     * @param id ID de la reserva.
     * @return Objeto {@link Reserva} si se encuentra, o {@code null} si no existe.
     */
    public Reserva buscarReservaID(int id){

        try(Connection con = DataBaseConnection.getInstance().conectarBD()){

            String sql = "select * from reservas where id_reserva=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeQuery();
            ResultSet rs = pst.getResultSet();

            if (rs.next()){

                return getReservaFromResultSet(rs);

            } else { return null; }

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Reserva}.
     *
     * @param rs ResultSet con los datos de la reserva.
     * @return Objeto {@link Reserva} construido a partir de los datos obtenidos.
     * @throws SQLException si ocurre un error al leer los datos.
     */
    private Reserva getReservaFromResultSet(ResultSet rs) throws SQLException {

        Reserva r = new Reserva();

        r.setId_reserva(rs.getInt("id_reserva"));
        r.setId_cliente(rs.getInt("id_cliente"));
        r.setId_propiedad(rs.getInt("id_propiedad"));
        r.setFecha_inicio(rs.getDate("fecha_inicio"));
        r.setFecha_fin(rs.getDate("fecha_fin"));
        r.setNum_personas(rs.getInt("num_personas"));
        r.setEstado(Reserva.EstadoReserva.valueOf(rs.getString("estado").toUpperCase().trim()));
        r.setPrecio_total(rs.getDouble("precio_total"));
        r.setMotivo_cancelacion(rs.getString("motivo_cancelacion"));

        return r;

    }
}
