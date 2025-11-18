# 🏡 Sistema de Gestión de Reservas 🏡

Sistema completo de gestión de reservas hoteleras desarrollado en Java con interfaz gráfica JavaFX y base de datos PostgreSQL en Supabase.

## 📋 Descripción

Aplicación de escritorio que permite gestionar de forma integral reservas, propiedades, clientes y pagos para un negocio de alojamiento. El sistema cuenta con una arquitectura MVC bien definida y una interfaz gráfica intuitiva.

## 🎸 Apartado Para Daniel Espinosa
- **Link de GOOGLE DRIVE**: https://drive.google.com/drive/folders/1V_ev8sfWjTb9AjUAd_GR49BOD7cBRqyA?usp=sharing
- **Link de JIRA**: https://reservas-vacacionales-java.atlassian.net/jira/software/projects/DAM2526/boards/1

## ✨ Características principales

- **Gestión de Clientes**: Registro, actualización y eliminación de clientes
- **Gestión de Propiedades**: Administración completa de propiedades disponibles para reserva
- **Sistema de Reservas**: Control de reservas con fechas, número de personas y estados
- **Gestión de Pagos**: Registro de transacciones con múltiples métodos de pago
- **Historial de Pagos**: Registro automático mediante procedimientos almacenados
- **Interfaz Gráfica**: Diseño intuitivo desarrollado con JavaFX y FXML
- **Conexión a Base de Datos**: Integración con PostgreSQL mediante Supabase

## 🛠️ Tecnologías utilizadas

- **Java 17+**
- **JavaFX** - Interfaz gráfica
- **PostgreSQL** - Base de datos
- **Supabase** - Hosting de base de datos
- **dotenv-java** - Gestión de variables de entorno
- **JDBC** - Conectividad con base de datos
- **Maven** - Gestión de dependencias

## 📦 Estructura del proyecto

```
src/main/java/com/reservas/
├── Main.java                     # Punto de entrada de la aplicación
├── config/
│   └── DataBaseConnection.java   # Configuración Singleton de conexión BD
├── model/
│   ├── Cliente.java              # Modelo de cliente
│   ├── Propiedad.java            # Modelo de propiedad
│   ├── Reserva.java              # Modelo de reserva
│   ├── Pago.java                 # Modelo de pagos
│   └── Valoracion.java           # Modelo de valoración
├── dao/
│   ├── ClienteDAO.java           # Operaciones CRUD de clientes
│   ├── PropiedadDAO.java         # Operaciones CRUD de propiedades
│   ├── ReservaDAO.java           # Operaciones CRUD de reservas
│   ├── PagoDAO.java              # Operaciones CRUD de pagos
│   └── ValoracionDAO.java        # Operaciones CRUD de valoraciones
└── controller/
    └── [Controladores FXML]
```

## 🚀 Instalación

### Prerrequisitos

- Java JDK 17 o superior
- Maven
- Cuenta en Supabase (o servidor PostgreSQL)

### Pasos de instalación

1. **Clonar el repositorio**
```bash
git clone <url-del-repositorio>
cd gestion-reservas
```

2. **Configurar variables de entorno**

Crear un archivo `.env` en la raíz del proyecto:
```env
SUPABASE_DB_URL=jdbc:postgresql://<host>:<puerto>/<nombre_bd>
SUPABASE_DB_USER=<usuario>
SUPABASE_DB_PASSWORD=<contraseña>
```

3. **Instalar dependencias**
```bash
mvn clean install
```

4. **Ejecutar la aplicación**
```bash
mvn javafx:run
```

## 🗄️ Configuración de la Base de Datos

### Tablas principales

- **clientes**: Información de clientes (nombre, email, teléfono, país)
- **propiedades**: Propiedades disponibles (nombre, dirección, precio/noche, capacidad)
- **reservas**: Reservas realizadas (cliente, propiedad, fechas, estado, precio)
- **pagos**: Transacciones de pago (reserva, monto, método, estado)
- **valoraciones**: Valoraciones registradas (reserva, puntuación, comentario, fecha, anonimato)
- **historico_pagos**: Registro automático de cambios en pagos


## 📖 Uso

1. **Iniciar la aplicación**: Al ejecutar, se mostrará la ventana principal con el título "Gestión de Reservas"

2. **Gestionar Clientes**: Agregar, modificar o eliminar información de clientes

3. **Administrar Propiedades**: Registrar nuevas propiedades con toda su información (ubicación, capacidad, precio)

4. **Crear Reservas**: Asociar clientes con propiedades y establecer fechas de reserva

5. **Procesar Pagos**: Registrar pagos con diferentes métodos (tarjeta, efectivo, transferencia, PayPal, Stripe)

## 👥 Autores
- **[Daniel Hernando](https://github.com/DaniloHer1)** - DAO de Pagos, aplicación principal y conexión BD
- **[Jaime Pérez](https://github.com/xaimeprb)** - DAO de Clientes, configuración y conexión con BD
- **[Pablo Armas](https://github.com/pabloar55)** - DAO de Reservas, configuración BD
- **[Diego Regueira](https://github.com/reguue2)** - DAO de Propiedades, desarrollo de la BD
- **[Sofía Abid](https://github.com/sofiacfgsdam)** - DAO de Valoraciones, diseño de aplicación

## 📅 Versión

**Versión actual**: 1.0  
**Fecha de inicio**: 27 Octubre 2025  
**Última actualización**: 17 Noviembre 2025


----------

**Nota**: Asegúrate de configurar correctamente las variables de entorno en el archivo `.env` antes de ejecutar la aplicación.
