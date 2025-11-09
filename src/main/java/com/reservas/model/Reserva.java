package com.reservas.model;

import java.sql.Date;

/**
 * <h1>Modelo de Reserva</h1>
 *
 * Representa una solicitud de alojamiento realizada por un cliente en el sistema.
 * <p>
 * Contiene información sobre la propiedad reservada, fechas de estadía, número de personas,
 * estado de la reserva, precio total y motivo de cancelación si aplica.
 * </p>
 *
 * <h2>Características principales:</h2>
 * <ul>
 *     <li>Asocia cada reserva a un cliente y una propiedad mediante sus identificadores.</li>
 *     <li>Permite gestionar el estado de la reserva (confirmada, pendiente, cancelada).</li>
 *     <li>Incluye métodos para obtener las fechas en formato legible.</li>
 * </ul>
 *
 * @author Pablo Armas
 * @since 01/11/2025
 */
public class Reserva {

    private int id_reserva;
    private int id_cliente;
    private int id_propiedad;
    private Date fecha_inicio;
    private Date fecha_fin;
    private int num_personas;
    private double precio_total;
    private String motivo_cancelacion;
    private EstadoReserva estadoReserva;

    /**
     * Clase ENUM
     * Representa los posibles estados de una reserva dentro del sistema.
     */
    public enum EstadoReserva {

        CONFIRMADA("confirmada"),

        PENDIENTE("pendiente"),

        CANCELADA("cancelada");

        private final String descripcion;

        EstadoReserva(String descripcion){
            this.descripcion = descripcion;
        }
        public String getDescripcion(){
            return descripcion;
        }

    }

    /**
     * Constructor completo para crear una instancia de reserva con todos los datos.
     *
     * @param id_reserva identificador único de la reserva.
     * @param id_cliente identificador del cliente que realiza la reserva.
     * @param id_propiedad identificador de la propiedad reservada.
     * @param fecha_inicio fecha de inicio de la estadía.
     * @param fecha_fin fecha de finalización de la estadía.
     * @param num_personas número de personas que ocuparán la propiedad.
     * @param estadoReserva estado actual de la reserva.
     * @param precio_total precio total calculado para la estadía.
     * @param motivo_cancelacion motivo de cancelación si aplica.
     */
    public Reserva(int id_reserva, int id_cliente, int id_propiedad, Date fecha_inicio, Date fecha_fin, int num_personas,
                   EstadoReserva estadoReserva, double precio_total, String motivo_cancelacion) {

        this.id_reserva = id_reserva;
        this.id_cliente = id_cliente;
        this.id_propiedad = id_propiedad;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.num_personas = num_personas;
        this.estadoReserva = estadoReserva;
        this.precio_total = precio_total;
        this.motivo_cancelacion = motivo_cancelacion;

    }

    /**
     * Constructor para registrar una nueva reserva sin ID asignado.
     *
     * @param id_cliente identificador del cliente.
     * @param id_propiedad identificador de la propiedad.
     * @param fecha_inicio fecha de inicio.
     * @param fecha_fin fecha de fin.
     * @param num_personas número de huéspedes.
     * @param estadoReserva estado inicial de la reserva.
     * @param precio_total precio total calculado.
     * @param motivo_cancelacion motivo de cancelación si aplica.
     */
    public Reserva(int id_cliente, int id_propiedad, Date fecha_inicio, Date fecha_fin, int num_personas, EstadoReserva estadoReserva,
                   double precio_total, String motivo_cancelacion) {

        this.id_cliente = id_cliente;
        this.id_propiedad = id_propiedad;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.num_personas = num_personas;
        this.estadoReserva = estadoReserva;
        this.precio_total = precio_total;
        this.motivo_cancelacion = motivo_cancelacion;

    }

    /**
     * Constructor por defecto.
     */
    public Reserva(){ }

    /**
     * Getters y setters
     */
    public String getFechaIniString(){
        return fecha_inicio.toString();
    }

    public String getFechaFinString(){
        return fecha_fin.toString();
    }

    public int getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_propiedad() {
        return id_propiedad;
    }

    public void setId_propiedad(int id_propiedad) {
        this.id_propiedad = id_propiedad;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public int getNum_personas() {
        return num_personas;
    }

    public void setNum_personas(int num_personas) {
        this.num_personas = num_personas;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstado(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public double getPrecio_total() {
        return precio_total;
    }

    public void setPrecio_total(double precio_total) {
        this.precio_total = precio_total;
    }

    public String getMotivo_cancelacion() {
        return motivo_cancelacion;
    }

    public void setMotivo_cancelacion(String motivo_cancelacion) {
        this.motivo_cancelacion = motivo_cancelacion;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id_reserva=" + id_reserva +
                ", id_cliente=" + id_cliente +
                ", id_propiedad=" + id_propiedad +
                ", fecha_inicio=" + fecha_inicio +
                ", fecha_fin=" + fecha_fin +
                ", num_personas=" + num_personas +
                ", estado=" + estadoReserva +
                ", precio_total=" + precio_total +
                ", motivo_cancelacion='" + motivo_cancelacion + '\'' +
                '}';
    }
}
