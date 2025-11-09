package com.reservas.model;

import java.time.LocalDate;


/**
 * <h1>Modelo de Cliente</h1>
 *
 * Representa a un cliente dentro del sistema de reservas.
 * <p>
 * Cada instancia contiene información personal básica (nombre, apellidos, email, teléfono y país),
 * junto con metadatos de la base de datos como {@code idCliente} y {@code fechaRegistro}.
 * </p>
 *
 * <h2>Características principales:</h2>
 * <ul>
 *     <li>Valida campos sensibles como nombre, apellidos, correo y teléfono.</li>
 *     <li>El ID y la fecha de registro son gestionados automáticamente por la base de datos.</li>
 *     <li>Uso de expresiones regulares para garantizar la integridad de los datos.</li>
 * </ul>
 *
 * @author  Jaime Pérez
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

    /**
     * Constructor principal del modelo {@link Cliente}.
     *
     * @param nombre nombre del cliente.
     * @param apellido apellidos del cliente.
     * @param email correo electrónico (único en la base de datos).
     * @param telefono número de teléfono con prefijo internacional.
     * @param pais país de residencia.
     */
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

    /**
     * Asigna el nombre del cliente validando su formato.
     * <p> Solo se permiten letras (con o sin acentos) y espacios.</p>
     *
     * @param nombre nombre a asignar.
     * @throws IllegalArgumentException si el nombre está vacío o contiene caracteres no válidos.
     */
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

    /**
     * Asigna el apellido del cliente validando su formato.
     *
     * @param apellido apellidos a asignar.
     * @throws IllegalArgumentException si el apellido es nulo, vacío o contiene caracteres inválidos.
     */
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

    /**
     * Asigna el correo electrónico del cliente verificando su formato.
     *
     * @param email dirección de correo electrónico.
     * @throws IllegalArgumentException si el formato del email no es válido.
     */
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

    /**
     * Asigna el número de teléfono del cliente validando su formato internacional (E.164).
     *
     * @param telefono número de teléfono en formato internacional.
     * @throws IllegalArgumentException si el formato del teléfono no es válido.
     */
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
