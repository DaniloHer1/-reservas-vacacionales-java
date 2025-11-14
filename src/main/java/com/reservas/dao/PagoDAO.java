package com.reservas.dao;

//import com.reservas.config.*;
import com.reservas.config.DataBaseConnection;
import com.reservas.model.Pago;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Clase DAO para la gestión de pagos</h1>
 *
 * Esta clase administra todas las operaciones de acceso a datos (CRUD) relacionadas con la entidad {@link Pago}.
 * <p>
 * Además de las funciones básicas, gestiona la integración con un procedimiento almacenado
 * que registra automáticamente un historial de acciones (inserción, actualización o eliminación)
 * en la tabla <b>historico_pagos</b>.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 *     <li>Insertar nuevos pagos.</li>
 *     <li>Actualizar métodos o estados de pago.</li>
 *     <li>Eliminar pagos con registro automático en histórico.</li>
 *     <li>Obtener la lista de pagos existentes y los IDs de reservas.</li>
 *     <li>Generar referencias de transacción incrementales (TXN001, TXN002...).</li>
 * </ul>
 *
 * @author Daniel Hernando
 * @since 03/11/2025
 */
public class PagoDAO {

    // Procedure creado para introducir datos en la tabla historico_pagos
    /*
            CREATE OR REPLACE PROCEDURE registrar_historial_pago(
            p_id_pago INTEGER,
            p_accion VARCHAR(20),
            p_estado_anterior VARCHAR(20) DEFAULT NULL,
            p_estado_nuevo VARCHAR(20) DEFAULT NULL,
            p_monto_anterior NUMERIC(10,2) DEFAULT NULL,
            p_monto_nuevo NUMERIC(10,2) DEFAULT NULL
        )
        LANGUAGE plpgsql
        AS $$
        BEGIN
            INSERT INTO historico_pagos (
                id_pago,
                accion,
                estado_anterior,
                estado_nuevo,
                monto_anterior,
                monto_nuevo
            )
            VALUES (
                p_id_pago,
                p_accion,
                p_estado_anterior,
                p_estado_nuevo,
                p_monto_anterior,
                p_monto_nuevo
            );
        END;
        $$;
     */

    private Connection conexion;

    private List<Pago> PagosDisponibles = new ArrayList<>();
    private List<Integer> listaReservasID = new ArrayList<>();

    /**
     * Crea un nuevo objeto DAO con una conexión activa.
     *
     * @param conexion conexión establecida con la base de datos.
     */
    public PagoDAO(Connection conexion) {

        this.conexion = conexion;

    }

    /**
     * Carga todos los identificadores de reservas disponibles en la base de datos.
     * <p>Se utiliza para llenar el {@code ComboBox} en el formulario de creación de pagos.</p>
     */
    public void mostrarTodosIdReservas() {

        listaReservasID.clear();

        try {

            Statement stm = conexion.createStatement();
            String sql = "SELECT id_reserva FROM reservas";
            ResultSet result = stm.executeQuery(sql);

            while (result.next()) {

                listaReservasID.add(result.getInt("id_reserva"));

            }

            stm.close();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Carga el monto total asociado a una reserva específica.
     *
     * @param reservaId identificador de la reserva.
     * @param txtMonto campo de texto donde se colocará el valor formateado.
     */
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

    /**
     * Inserta un nuevo registro de pago en la base de datos.
     * <p>
     * Tras insertar el pago, se registra la operación en el histórico de pagos
     * mediante la llamada al procedimiento almacenado <b>registrar_historial_pago</b>.
     * </p>
     *
     * @param pago objeto {@link Pago} con los datos a registrar.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     */
    public boolean insertarPago(Pago pago) {

        String sql = """
                INSERT INTO pagos(id_reserva, fecha_pago, monto, metodo_pago, estado_pago, referencia_transaccion)
                VALUES(?,?,?,?,?,?)
                """;

        try (PreparedStatement preparedStatement = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, pago.getReserva());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
            preparedStatement.setDouble(3, pago.getMonto());
            preparedStatement.setString(4, pago.getMetodoPago().name().toLowerCase());
            preparedStatement.setString(5, pago.getEstadoPago().name().toLowerCase());
            preparedStatement.setString(6, pago.getReferenciaTransaccion());

            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas > 0) {

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) {

                    int idGenerado = generatedKeys.getInt(1);
                    pago.setId(idGenerado);

                    registrarEnHistorico(
                            idGenerado,
                            "INSERT",
                            null,
                            pago.getEstadoPago().name().toLowerCase(),
                            null,
                            pago.getMonto()
                    );

                }

                return true;
            }

            return false;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }
    }

    /**
     * Actualiza el método o estado de un pago existente.
     * <p>Los demás campos (reserva, monto, fecha) no son modificables.</p>
     *
     * @param pago objeto {@link Pago} con los nuevos datos a aplicar.
     * @return {@code true} si la actualización fue exitosa.
     */
    public boolean actualizarPago(Pago pago) {

        Pago pagoAnterior = buscarPagoPorId(pago.getId());

        String sql = """
        UPDATE pagos
        SET metodo_pago = ?,
            estado_pago = ?            
        WHERE id_pago = ?;
        """;

        try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, pago.getMetodoPago().name().toLowerCase());
            preparedStatement.setString(2, pago.getEstadoPago().name().toLowerCase());
            preparedStatement.setInt(3, pago.getId());

            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas>0){

                registrarEnHistorico(

                        pago.getId(),
                        "UPDATE",
                        pagoAnterior.getEstadoPago().name().toLowerCase(),
                        pago.getEstadoPago().name().toLowerCase(),
                        pagoAnterior.getMonto() != 0 ?
                                pagoAnterior.getMonto() : null,
                         pago.getMonto()

                );
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }
    }

    /**
     * Obtiene todos los registros de pagos almacenados en la base de datos.
     * Los resultados se guardan internamente en {@code pagosDisponibles}.
     */
    public void mostrarTodosPagos() {

        PagosDisponibles.clear();

        try (Statement statement = conexion.createStatement()) {

            String sql = "SELECT * FROM pagos";
            ResultSet result = statement.executeQuery(sql);

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

                pago.setReferenciaTransaccion(result.getString("referencia_transaccion"));

                PagosDisponibles.add(pago);

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Elimina un pago de la base de datos y registra la operación en el histórico.
     *
     * @param pago objeto {@link Pago} que se desea eliminar.
     * @return {@code true} si la eliminación fue exitosa.
     */
    public boolean borrarPago(Pago pago) {

        Pago pagoABorrar = buscarPagoPorId(pago.getId());

        if (pagoABorrar == null) {
            System.err.println("No se encontró el pago para eliminar");
            return false;
        }

        try {

            registrarEnHistorico(
                    pago.getId(),
                    "DELETE",
                    pagoABorrar.getEstadoPago().name().toLowerCase(),
                    null,
                    pagoABorrar.getMonto(),
                    null
            );

            String sql = "DELETE FROM pagos WHERE id_pago = ?";

            try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {

                preparedStatement.setInt(1, pago.getId());
                int filasAfectadas = preparedStatement.executeUpdate();
                return filasAfectadas > 0;

            }

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }
    }

    /**
     * Genera una nueva referencia de transacción incremental.
     *
     * @return Cadena alfanumérica con el formato {@code TXN###}.
     */
    public String generarSiguienteReferencia() {

        try {

            String sql = """
            SELECT referencia_transaccion 
            FROM pagos 
            ORDER BY id_pago DESC LIMIT 1
            """;

            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                String ultimaReferencia = rs.getString("referencia_transaccion");

                String numeroStr = ultimaReferencia.replace("TXN", "");
                int numero = Integer.parseInt(numeroStr);

                numero++;

                return String.format("TXN%03d", numero);

            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return "TXN001";
    }

    /**
     * Busca y devuelve un objeto {@link Pago} según su identificador único.
     *
     * @param idPago identificador del pago.
     * @return el objeto encontrado, o {@code null} si no existe.
     */
    public Pago buscarPagoPorId(int idPago) {

        String sql = """
                    SELECT * 
                    FROM pagos 
                    WHERE id_pago = ?
                    """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPago);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Pago pago = new Pago();
                pago.setId(rs.getInt("id_pago"));
                pago.setReserva(rs.getInt("id_reserva"));
                pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
                pago.setMonto(rs.getDouble("monto"));

                String metodo = rs.getString("metodo_pago").toUpperCase().trim();
                pago.setMetodoPago(Pago.MetodoPago.valueOf(metodo));

                String estado = rs.getString("estado_pago").toUpperCase().trim();
                pago.setEstadoPago(Pago.EstadoPago.valueOf(estado));

                pago.setReferenciaTransaccion(rs.getString("referencia_transaccion"));
                return pago;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Llama al procedimiento almacenado {@code registrar_historial_pago} para registrar cambios sobre un pago.
     *
     * @param accion
     * @param estadoAnterior
     * @param idPago
     * @param estadoNuevo
     * @param montoAnterior
     * @param montoNuevo
     */
    private void registrarEnHistorico(int idPago, String accion, String estadoAnterior, String estadoNuevo,
            Double montoAnterior, Double montoNuevo) {

        String sql = "CALL registrar_historial_pago(?, ?, ?, ?, ?, ?)";

        try (CallableStatement callableStatement = conexion.prepareCall(sql)) {

            callableStatement.setInt(1, idPago);
            callableStatement.setString(2, accion);

            if (estadoAnterior != null) {
                callableStatement.setString(3, estadoAnterior);

            } else {
                callableStatement.setNull(3, Types.VARCHAR);
            }

            if (estadoNuevo != null) {
                callableStatement.setString(4, estadoNuevo);

            } else {
                callableStatement.setNull(4, Types.VARCHAR);
            }

            if (montoAnterior != null) {
                callableStatement.setBigDecimal(5, BigDecimal.valueOf(montoAnterior));

            } else {
                callableStatement.setNull(5, Types.NUMERIC);
            }

            if (montoNuevo != null) {
                callableStatement.setBigDecimal(6, BigDecimal.valueOf(montoNuevo));

            } else {
                callableStatement.setNull(6, Types.NUMERIC);
            }

            callableStatement.execute();

        } catch (SQLException e) {

            System.err.println("Error al registrar en histórico: " + e.getMessage());
            e.printStackTrace();

        }
    }

    /** @return lista de IDs de reservas disponibles. */
    public List<Integer> getListaReservasID() {
        return listaReservasID;
    }

    /** @return lista de pagos cargados desde la base de datos. */
    public List<Pago> getPagosDisponibles() {
        return PagosDisponibles;
    }

}
