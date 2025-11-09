package com.reservas.model;

import java.time.LocalDateTime;

/**
 * <h1>Modelo de Valoración</h1>
 *
 * Representa una reseña o calificación realizada por un cliente tras finalizar una reserva.
 * <p>
 * Cada valoración está asociada a una reserva existente e incluye información
 * como la puntuación, el comentario, si es anónima y la fecha en que fue realizada.
 * </p>
 *
 * @author Sofía Abid
 * @since 04/11/2025
 */
public class Valoracion {

    private int id;
    private int reserva;
    private int puntuacion;
    private String comentario;
    private boolean anonimato;
    private LocalDateTime fechaValoracion;

    /**
     * Constructor vacío.
     */
    public Valoracion() {}

    /**
     * Constructor para crear una nueva valoración (sin ID).
     *
     * @param reserva ID de la reserva asociada.
     * @param puntuacion Puntuación entre 1 y 5.
     * @param comentario Texto opcional de la reseña (máximo 500 caracteres).
     * @param anonimato Indica si la reseña será anónima.
     */
    public Valoracion(int reserva, int puntuacion, String comentario, boolean anonimato) {
        this.reserva = reserva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
    }

    /**
     * Constructor para actualizar una valoración existente (incluye ID).
     *
     * @param id Identificador único de la valoración.
     * @param reserva ID de la reserva asociada.
     * @param puntuacion Puntuación entre 1 y 5.
     * @param comentario Texto opcional de la reseña.
     * @param anonimato Indica si la reseña será anónima.
     */
    public Valoracion(int id, int reserva, int puntuacion, String comentario, boolean anonimato) {
        this.id = id;
        this.reserva = reserva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
    }

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
