package com.reservas.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * <h1>Modelo de Pago</h1>
 *
 * Representa una transacción económica asociada a una reserva del sistema.
 * <p>
 * Contiene información sobre el monto pagado, la fecha del pago, el método utilizado,
 * su estado actual y una referencia opcional de la transacción.
 * </p>
 *
 * <h2>Características principales:</h2>
 * <ul>
 *     <li>Asocia cada pago a una reserva mediante su identificador único.</li>
 *     <li>Soporta múltiples métodos y estados de pago a través de enumeraciones.</li>
 *     <li>Incluye formatos de salida legibles para fecha, monto y estado.</li>
 * </ul>
 *
 * @author Daniel Hernando
 * @since 05/11/2025
 */
public class Pago {

    private int id;
    private int reserva;
    private LocalDateTime fechaPago;
    private double monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
    private String referenciaTransaccion;

    /**
     * Clase ENUM
     * Representa los métodos de pago disponibles en el sistema.
     */
    public enum MetodoPago{
        TARJETA("Tarjeta"),
        EFECTIVO("Efectivo"),
        TRANSFERENCIA("Transferencia"),
        PAYPAL("Paypal"),
        STRIPE("Stripe");

        private final String descripcion;

        MetodoPago(String descripcion){
            this.descripcion=descripcion;
        }
        public String getDescripcion(){
            return descripcion;
        }

    }

    /**
     * Clase ENUM
     * Representa los posibles estados de un pago dentro del sistema.
     */
    public enum EstadoPago{

        COMPLETADO("Completado"),
        PENDIENTE("Pendiente"),
        RECHAZADO("Rechazado");

        private final String descripcion;

        EstadoPago(String descripcion){
            this.descripcion=descripcion;

        }

        public String getDescripcion(){
            return descripcion;
        }

    }

    /**
     * Constructor por defecto. Inicializa la fecha del pago con la fecha actual
     * y deja el resto de campos por defecto.
     */
    public Pago(){
        this.fechaPago =LocalDateTime.now();
    }

    /**
     * Constructor completo para crear una instancia de pago con todos los datos.
     *
     * @param id identificador único del pago.
     * @param reservaId identificador de la reserva asociada.
     * @param fechaPago fecha y hora del pago.
     * @param monto cantidad pagada.
     * @param metodoPago método empleado para el pago.
     * @param estadoPago estado actual del pago.
     * @param referenciaTransaccion referencia o código de la transacción.
     */
    public Pago(int id, int reservaId, LocalDateTime fechaPago, double monto,
                MetodoPago metodoPago, EstadoPago estadoPago, String referenciaTransaccion) {

        this.id = id;
        this.reserva = reservaId;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.referenciaTransaccion = referenciaTransaccion;

    }

    /**
     * Constructor para registrar un nuevo pago en estado pendiente.
     *
     * @param reserva ID de la reserva asociada.
     * @param monto cantidad a pagar.
     * @param metodoPago método de pago utilizado.
     */
    public Pago(int reserva,double monto,MetodoPago metodoPago) {

        this.reserva = reserva;
        this.fechaPago =LocalDateTime.now();
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estadoPago =EstadoPago.PENDIENTE;
    }

    public String getReferenciaTransaccion() {
        return referenciaTransaccion;
    }

    public void setReferenciaTransaccion(String referenciaTransaccion) {
        this.referenciaTransaccion = referenciaTransaccion;
    }

    /**
     * Devuelve el método de pago en formato legible.
     *
     * @return texto descriptivo del método.
     */
    public String getMetodoPagoTexto() {
        return metodoPago.getDescripcion();
    }

    /**
     * Devuelve el estado de pago en formato legible.
     *
     * @return texto descriptivo del estado.
     */
    public String getEstadoPagoTexto() {
        return estadoPago.getDescripcion();
    }

    /**
     * Devuelve la fecha del pago en formato {@code dd/MM/yyyy}.
     *
     * @return cadena formateada con la fecha del pago.
     */
    public String getFechaTexto() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fechaPago.format(formatter);

    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    /**
     * Devuelve el monto del pago acompañado del símbolo de euro.
     *
     * @return texto formateado del monto (por ejemplo: "120.0 €").
     */
    public String getMontoMostrar() {
        return monto + " €";
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getReserva() {
        return reserva;
    }

    public void setReserva(int reserva) {
        this.reserva = reserva;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    @Override
    public String toString() {
        return String.format("Pago #%d - Reserva: %d - Monto: %.2f€ - Método: %s - Estado: %s",
                id,reserva,monto,metodoPago.getDescripcion(),estadoPago.getDescripcion());
    }
}
