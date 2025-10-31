package com.reservas.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pago {
    private int id;
    private int reserva;
    private LocalDateTime fechaPago;
    private double monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;

    // ENUMS
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


    public Pago(){
        this.fechaPago =LocalDateTime.now();
    }
    public Pago(int id, int reservaId, LocalDateTime fechaPago, double monto,
                MetodoPago metodoPago, EstadoPago estadoPago) {
        this.id = id;
        this.reserva = reservaId;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
    }
    public Pago(int reserva,double monto,MetodoPago metodoPago) {
        this.reserva = reserva;
        this.fechaPago =LocalDateTime.now();
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.estadoPago =EstadoPago.PENDIENTE;
    }


    public String getMetodoPagoTexto() {
        return metodoPago.getDescripcion();
    }

    public String getEstadoPagoTexto() {
        return estadoPago.getDescripcion();
    }

    public String getFechaTexto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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
