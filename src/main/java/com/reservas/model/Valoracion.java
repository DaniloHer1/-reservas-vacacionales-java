package com.reservas.model;

import java.time.LocalDateTime;

/**
 * @author
 * Sofia Abid
 */
public class Valoracion {

    // Variables de la clase Valoación
    private int id;
    private int reseva;
    private int puntuacion;
    private String comentario;
    private boolean anonimato;
    private LocalDateTime fechaValoracion;

    // Constructor vacío de Valoraciones
    public Valoracion() {}

    // Constructor para añadir valoración con todos los parámetros
    public Valoracion(int id, int reseva, int puntuacion, String comentario, boolean anonimato, LocalDateTime fechaValoracion) {
        this.id = id;
        this.reseva = reseva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
        this.fechaValoracion = fechaValoracion;
    }

    // Constructor para las listas
    public Valoracion(int reseva, int puntuacion, String comentario, boolean anonimato) {
        this.reseva = reseva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
    }

    // Constructor para modificar valoración
    public Valoracion(int id, int reseva, int puntuacion, String comentario, boolean anonimato) {
        this.id = id;
        this.reseva = reseva;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.anonimato = anonimato;
    }

    // Getters y setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getReseva() {return reseva;}
    public void setReseva(int reseva) {this.reseva = reseva;}

    public int getPuntuacion() {return puntuacion;}
    public void setPuntuacion(int puntuacion) {this.puntuacion = puntuacion;}

    public String getComentario() {return comentario;}
    public void setComentario(String comentario) {this.comentario = comentario;}

    public boolean isAnonimato() {return anonimato;}
    public void setAnonimato(boolean anonimato) {this.anonimato = anonimato;}

    public LocalDateTime getFechaValoracion() {return fechaValoracion;}
    public void setFechaValoracion(LocalDateTime fechaValoracion) {this.fechaValoracion = fechaValoracion;}

}
