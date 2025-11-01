package com.reservas.model;

import java.sql.Date;
/**
 * Clase que representa una reserva de un cliente.
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

    public Reserva(int id_reserva, int id_cliente, int id_propiedad, Date fecha_inicio,
                   Date fecha_fin, int num_personas, EstadoReserva estadoReserva, double precio_total, String motivo_cancelacion) {
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
    public Reserva(){
    }
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
