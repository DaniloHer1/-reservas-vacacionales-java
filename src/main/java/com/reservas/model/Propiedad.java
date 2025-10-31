package com.reservas.model;

/**
 * Clase que representa una propiedad del sistema.
 *
 * @author Diego Regueira
 * @since 31/10/2025
 */
public class Propiedad {

    private int idPropiedad;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String pais;
    private float precio_noche;
    private int capacidad;
    private String descripcion;
    private String estado_propiedad;

    public Propiedad(int idPropiedad, String nombre, String direccion, String ciudad, String pais, float precio_noche, int capacidad, String descripcion, String estado_propiedad) {
        this.idPropiedad = idPropiedad;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
        this.precio_noche = precio_noche;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.estado_propiedad = estado_propiedad;
    }

    public int getIdPropiedad() {
        return idPropiedad;
    }

    public void setIdPropiedad(int idPropiedad) {
        this.idPropiedad = idPropiedad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public float getPrecio_noche() {
        return precio_noche;
    }

    public void setPrecio_noche(float precio_noche) {
        this.precio_noche = precio_noche;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado_propiedad() {
        return estado_propiedad;
    }

    public void setEstado_propiedad(String estado_propiedad) {
        this.estado_propiedad = estado_propiedad;
    }
}
