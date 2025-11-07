package com.reservas.model;

import java.time.LocalDateTime;

/**
 * Modelo de datos para una Valoración
 * Representa una reseña o calificación asociada a una reserva.
 *
 * @author
 * Sofía Abid
 */
public class Valoracion {

    private int id;
    private int reserva;
    private int puntuacion;
    private String comentario;
    private boolean anonimato;
    private LocalDateTime fechaValoracion;

    // Constructor vacío
    public Valoracion() {}

    // Constructor completo
    public Valoracion(int id, int reserva, int puntuacion, String comentario, boolean anonimato, LocalDateTime fechaValoracion) {
        this.id = id;
        this.reserva = reserva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
        this.fechaValoracion = fechaValoracion;
    }

    // Constructor para crear nueva valoración
    public Valoracion(int reserva, int puntuacion, String comentario, boolean anonimato) {
        this.reserva = reserva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
    }

    // Constructor para actualizar una valoración existente
    public Valoracion(int id, int reserva, int puntuacion, String comentario, boolean anonimato) {
        this.id = id;
        this.reserva = reserva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
    }

    // --- Getters y Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReserva() { return reserva; }
    public void setReserva(int reserva) { this.reserva = reserva; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public boolean isAnonimato() { return anonimato; }
    public void setAnonimato(boolean anonimato) { this.anonimato = anonimato; }

    public LocalDateTime getFechaValoracion() { return fechaValoracion; }
    public void setFechaValoracion(LocalDateTime fechaValoracion) { this.fechaValoracion = fechaValoracion; }

    @Override
    public String toString() {
        return "Valoracion{" +
                "id=" + id +
                ", reserva=" + reserva +
                ", puntuacion=" + puntuacion +
                ", comentario='" + comentario + '\'' +
                ", anonimato=" + anonimato +
                ", fechaValoracion=" + fechaValoracion +
                '}';
    }
}
