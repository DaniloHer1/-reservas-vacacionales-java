package com.reservas.dao;

import com.reservas.config.DataBaseConnection;
import com.reservas.model.Reserva;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ReservaDAO {
    Connection connection;
    ArrayList<Reserva> reservas;

    public ReservaDAO() {
        connection = DataBaseConnection.getInstance().conectarBD();
        reservas = new ArrayList<>();
    }
    public ArrayList<Reserva> getReservas(){
        String sql = "select id_reserva, id_cliente, id_propiedad, fecha_inicio," +
                " fecha_fin, num_personas, estado, precio_total, motivo_cancelacion from reservas";
        try {
            Statement statement = connection.createStatement();
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
}
