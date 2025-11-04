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

    public Propiedad(String nombre, String direccion, String ciudad, String pais, float precio_noche, int capacidad, String descripcion, String estado_propiedad) {
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
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la propiedad no puede estar vacío.");
        }
        if (nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre de la propiedad no puede superar los 100 caracteres.");
        }
        this.nombre = nombre.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        this.direccion = direccion.trim();
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía.");
        }
        this.ciudad = ciudad.trim();
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        if (pais == null || pais.trim().isEmpty()) {
            throw new IllegalArgumentException("El país no puede estar vacío.");
        }
        if (pais.length() > 50) {
            throw new IllegalArgumentException("El nombre del país no puede superar los 50 caracteres.");
        }
        this.pais = pais.trim();
    }

    public float getPrecio_noche() {
        return precio_noche;
    }

    public void setPrecio_noche(float precio_noche) {
        if (precio_noche <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor que 0.");
        }
        this.precio_noche = precio_noche;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1 persona.");
        }
        this.capacidad = capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        if (descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede superar los 500 caracteres.");
        }
        this.descripcion = descripcion.trim();
    }

    public String getEstado_propiedad() {
        return estado_propiedad;
    }

    public void setEstado_propiedad(String estado_propiedad) {
        if (estado_propiedad == null || estado_propiedad.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de la propiedad no puede estar vacío.");
        }
        if (!estado_propiedad.equalsIgnoreCase("disponible")
                && !estado_propiedad.equalsIgnoreCase("ocupada")
                && !estado_propiedad.equalsIgnoreCase("mantenimiento")) {
            throw new IllegalArgumentException("El estado de la propiedad debe ser: disponible, ocupada o mantenimiento.");
        }
        this.estado_propiedad = estado_propiedad.toLowerCase();
    }
}

