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
            statement.execute(sql);
            ResultSet rs = statement.getResultSet();
            while (rs.next()){
                Reserva reserva = new Reserva();
                reserva.setId_reserva(rs.getInt("id_reserva"));
                reserva.setId_cliente(rs.getInt("id_cliente"));
                reserva.setId_propiedad(rs.getInt("id_propiedad"));
                reserva.setFecha_inicio(rs.getDate("fecha_inicio"));
                reserva.setFecha_fin(rs.getDate("fecha_fin"));
                reserva.setNum_personas(rs.getInt("num_personas"));
                reserva.setEstado(Reserva.EstadoReserva.valueOf(rs.getString("estado").toUpperCase().trim()));
                reserva.setPrecio_total(rs.getDouble("precio_total"));
                reserva.setMotivo_cancelacion(rs.getString("motivo_cancelacion"));
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reservas;
    }
    public void aniadirReserva(Reserva r){
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
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
