package com.reservas.model;

/**
 * <h1>Modelo de Propiedad</h1>
 *
 * Representa una unidad de alojamiento disponible en el sistema de reservas.
 * <p>
 * Contiene información relevante como ubicación, precio por noche, capacidad,
 * estado actual y una descripción detallada.
 * </p>
 *
 * <h2>Características principales:</h2>
 * <ul>
 *     <li>Permite identificar cada propiedad mediante un ID único.</li>
 *     <li>Incluye validaciones para asegurar la integridad de los datos ingresados.</li>
 *     <li>Gestiona el estado de disponibilidad de la propiedad.</li>
 * </ul>
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

    /**
     * Constructor completo para inicializar una propiedad con todos sus atributos.
     *
     * @param idPropiedad identificador único de la propiedad.
     * @param nombre nombre comercial o identificativo.
     * @param direccion dirección física de la propiedad.
     * @param ciudad ciudad donde se ubica.
     * @param pais país correspondiente.
     * @param precio_noche tarifa por noche en euros.
     * @param capacidad número máximo de personas que puede alojar.
     * @param descripcion descripción detallada del alojamiento.
     * @param estado_propiedad estado actual (disponible, ocupada, mantenimiento).
     */
    public Propiedad(int idPropiedad, String nombre, String direccion, String ciudad, String pais, float precio_noche, int capacidad,
                     String descripcion, String estado_propiedad) {

        this.idPropiedad = idPropiedad;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
        this.precio_noche = precio_noche;
        this.capacidad = capacidad;
        this.descripcion = descripcion;


    }

    /**
     * Constructor alternativo para registrar una nueva propiedad sin ID asignado.
     *
     * @param nombre nombre comercial o identificativo.
     * @param direccion dirección física de la propiedad.
     * @param ciudad ciudad donde se ubica.
     * @param pais país correspondiente.
     * @param precio_noche tarifa por noche en euros.
     * @param capacidad número máximo de personas que puede alojar.
     * @param descripcion descripción detallada del alojamiento.
     * @param estado_propiedad estado actual (disponible, ocupada, mantenimiento).
     */
    public Propiedad(String nombre, String direccion, String ciudad, String pais, float precio_noche, int capacidad, String descripcion,
                     String estado_propiedad) {

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

    /**
     * Establece el nombre de la propiedad validando su longitud y contenido.
     *
     * @param nombre nombre comercial o identificativo.
     */
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

    /**
     * Establece el precio por noche validando que sea positivo.
     *
     * @param precio_noche tarifa en euros.
     */
    public void setPrecio_noche(float precio_noche) {

        if (precio_noche <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor que 0.");
        }

        this.precio_noche = precio_noche;
    }

    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Establece la capacidad máxima de huéspedes.
     *
     * @param capacidad número de personas.
     */
    public void setCapacidad(int capacidad) {

        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1 persona.");
        }

        this.capacidad = capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la propiedad validando su longitud.
     *
     * @param descripcion texto descriptivo.
     */
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

    /**
     * Establece el estado actual de la propiedad.
     * Valores válidos: disponible, ocupada, mantenimiento.
     *
     * @param estado_propiedad estado textual.
     */
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

