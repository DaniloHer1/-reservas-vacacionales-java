package com.reservas.dao;

import com.reservas.config.*;
import com.reservas.model.Pago;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {


    private Connection conexion;

    private List<Pago> PagosDisponibles=new ArrayList<>();
    private List<Integer> listaReservasID=new ArrayList<>();



    public PagoDAO( Connection conexion){
        this.conexion=conexion;
    }

    public void mostrarTodosIdReservas(){
        listaReservasID.clear();
        try {
            Statement stm = conexion.createStatement();

            String sql = "SELECT id_reserva from reservas";

            ResultSet result = stm.executeQuery(sql);

            while (result.next()) {

              listaReservasID.add(result.getInt("id_reserva"));

            }
            stm.close();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    public void cargarMontoDeReserva(int reservaId, TextField txtMonto) {
        try {

            String sql = "SELECT precio_total FROM reservas WHERE id_reserva = ?";
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setInt(1, reservaId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double monto = rs.getDouble("precio_total");
                txtMonto.setText(String.format("%.2f", monto));
                txtMonto.setEditable(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertarPago(Pago pago){

        String sql= """
                insert into pagos(id_reserva, fecha_pago, monto, metodo_pago, estado_pago)
                values(?,?,?,?,?)
                """;

        // el return generated keys nos permite recuperar el valor autoincremantal de la base de datos
        try(PreparedStatement preparedStatement =conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

                preparedStatement.setInt(1,pago.getReserva());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
                preparedStatement.setDouble(3,pago.getMonto());
                preparedStatement.setString(4,pago.getMetodoPago().name().toLowerCase());
                preparedStatement.setString(5,pago.getEstadoPago().name().toLowerCase());

                int filasAfectadas=preparedStatement.executeUpdate();

                if (filasAfectadas>0){
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()){
                        pago.setId(generatedKeys.getInt(1));
                    }
                    return true;
                }
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void mostrarTodosPagos(){
        PagosDisponibles.clear();

            try {
                Statement stm = conexion.createStatement();

                String sql = "SELECT * from pagos";

                ResultSet result = stm.executeQuery(sql);

                while (result.next()) {

                        Pago pago = new Pago();

                    pago.setId(result.getInt("id_pago"));
                    pago.setReserva(result.getInt("id_reserva"));
                    pago.setFechaPago(result.getTimestamp("fecha_pago").toLocalDateTime());
                    pago.setMonto(result.getDouble("monto"));
                    String metodo = result.getString("metodo_pago").toUpperCase().trim();
                    pago.setMetodoPago(Pago.MetodoPago.valueOf(metodo));
                    String estado = result.getString("estado_pago").toUpperCase().trim();
                    pago.setEstadoPago(Pago.EstadoPago.valueOf(estado));

                        PagosDisponibles.add(pago);
                    }


                stm.close();

            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
        }
    }


    public List<Integer> getListaReservasID() {
        return listaReservasID;
    }

    public List<Pago> getPagosDisponibles() {
        return PagosDisponibles;
    }

}
