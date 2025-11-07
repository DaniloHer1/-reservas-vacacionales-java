package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Reserva;

import java.sql.*;
import java.util.ArrayList;

public class ReservaDAO {
    ArrayList<Reserva> reservas;

    public ReservaDAO() {
        reservas = new ArrayList<>();
    }
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
    public int eliminarReserva(Reserva r){
        String sql = "delete from reservas where id_reserva=?;";
        try (Connection con = DataBaseConnection.getInstance().conectarBD(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, r.getId_reserva());
           return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Reserva buscarReservaID(int id){
        try(Connection con = DataBaseConnection.getInstance().conectarBD()){
            String sql = "select * from reservas where id_reserva=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeQuery();
            ResultSet rs = pst.getResultSet();
            if (rs.next()){
                return getReservaFromResultSet(rs);
            }else {
                return null;
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
