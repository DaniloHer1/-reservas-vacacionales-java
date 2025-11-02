package com.reservas.model;

import java.time.LocalDate;

/**
 * Clase que representa un cliente del sistema.
 * Contiene validaciones para {@link #setNombre(String)}, {@link #setApellido(String)}, {@link #setEmail(String)}, {@link #setTelefono(String)}
 * El id del cliente y la fecha de registro se gestionan automáticamente desde la base de datos.
 *
 * @author Jaime Pérez
 * @since 30/10/2025
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String pais;
    private LocalDate fechaRegistro;

    public Cliente(String nombre, String apellido, String email, String telefono, String pais) {

        setNombre(nombre);
        setApellido(apellido);
        setEmail(email);
        setTelefono(telefono);
        setPais(pais);

    }

    public int getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(int idCliente) {this.idCliente = idCliente;}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {

        String regexNombre = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$";

        if (nombre == null || nombre.isBlank()) {

            throw new IllegalArgumentException("El nombre no puede estar vacío.");

        }

        if (!nombre.matches(regexNombre)) {

            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios");

        }

        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {

        String regexApellido = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$";

        if (apellido == null || apellido.isBlank()) {

            throw new IllegalArgumentException("El apellido no puede estar vacío.");

        }

        if (!apellido.matches(regexApellido)) {

            throw new IllegalArgumentException("El apellido solo puede contener letras y espacios");

        }

        this.apellido = apellido;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        String regexEmail = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (email == null || email.isBlank()) {

            throw new IllegalArgumentException("El email no puede estar vacío");

        }

        if (!email.matches(regexEmail)) {

            throw new IllegalArgumentException("El formato del email no es válido");

        }

        this.email = email;

    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {

        String regexTelefono = "^\\+[1-9]\\d{6,14}$";

        if (!telefono.matches(regexTelefono)) {

            throw new IllegalArgumentException("El formato del teléfono no es válido");

        }

        this.telefono = telefono;

    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {this.fechaRegistro = fechaRegistro;}

    @Override
    public String toString() {

        return "Cliente{" + "idCliente=" + idCliente + ", nombre='" + nombre + '\'' + ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' + ", telefono='" + telefono + '\'' + ", fechaRegistro=" + fechaRegistro +'}';

    }

}
