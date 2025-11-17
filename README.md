#  Sistema de Gestión de Biblioteca - TFI Programación 2
Trabajo Final Integrador de Programación II - Tecnicatura Universitaria en Programación - Universidad Tecnológica Nacional

## Descripción del Proyecto

Sistema de gestión bibliotecaria que permite administrar libros y sus fichas bibliográficas mediante una arquitectura en capas con manejo de transacciones ACID, desarrollado en Java con conexión a base de datos MySQL.

### Dominio del Proyecto

El sistema gestiona una biblioteca donde:
- Cada Libro contiene información básica (título, autor, editorial, año de edición)
- Cada Ficha Bibliográfica contiene datos catalográficos (ISBN, clasificación Dewey, estantería, idioma)
- Existe una relación 1:1 entre Libro y Ficha Bibliográfica
- Se implementa baja lógica para mantener historial de datos
- Se valida con el código ISBN que por cada edicion de un libro solo hay una ficha bibliográfica
- 
### Software Necesario

| Componente            | Versión Mínima | Versión Recomendada |
|-----------------------|----------------|---------------------|
| **Java JDK**          | 17             | 21                  |
| **MySQL**             | 5.7            | 8.0+                |
| **Apache NetBeans**   | 17             | 21 o superior       |
| **MySQL Connector/J** | 8.0            | 9.5.0               |

### Dependencias

- `mysql-connector-j-9.5.0.jar` (Descargar en (https://dev.mysql.com/downloads/connector/j/))

---

## Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/phidalg/programacion2_TPI.git
cd programacion2_TPI
```

### 2. Crear la Base de Datos

### Desde phpMyAdmin (XAMPP)

1. Iniciar **XAMPP** (Apache + MySQL)
2. Abrir `http://localhost/phpmyadmin`
3. Crear base de datos
4. Importar scripts:
   - `sql/BBDD.sql`
   
   - `sql/datos_de_prueba.sql`

### 3. Configurar Credenciales de Base de Datos

Editar el archivo `prog2_TPI/src/config/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/prog2_tpi?useSSL=false&serverTimezone=UTC
db.user=root
db.password=TU_CONTRASEÑA_MYSQL
db.driver=com.mysql.cj.jdbc.Driver
```

**IMPORTANTE:** Reemplazar `TU_CONTRASEÑA_MYSQL` con tu contraseña real de MySQL.

---

## Compilación y Ejecución

### Desde Apache NetBeans

1. Abrir **NetBeans**
2. `File → Open Project` → Seleccionar carpeta `programacion2_TPI`
3. **Agregar MySQL Connector** (si no está):
   - Clic derecho en el proyecto → `Properties`
   - `Libraries → Compile → Add JAR/Folder`
   - Seleccionar `mysql-connector-j-9.5.0.jar`
4. **Compilar:**
   - Clic derecho en el proyecto → `Clean and Build`
5. **Ejecutar:**
   - Clic derecho en `edu.utn.Main.java` → `Run File`

##  Guía de Uso

### Credenciales de Prueba

El sistema viene con **5 libros de prueba** ya cargados:

| ID | Título             | Autor             | ISBN              |
|----|--------------------|-------------------|-------------------|
| 1  | El Aleph           | Jorge Luis Borges | 978-950-04-2749-1 |
| 2  | Rayuela            | Julio Cortázar    | 978-950-07-6424-6 |
| 3  | Martín Fierro      | José Hernández    | 978-950-03-8560-3 |
| 4  | Don Segundo Sombra | Ricardo Güiraldes | 978-950-03-8841-3 |
| 5  | El túnel           | Ernesto Sabato    | 978-987-566-989-6 |

### Flujo de Uso Recomendado

#### 1. Explorar Libros Existentes
```
Menú Principal → 1. Gestionar Libros → 2. Listar todos los Libros
```

Vas a ver los 5 libros con sus fichas bibliográficas asociadas.

#### 2. Buscar por ISBN
```
Menú Principal → 1. Gestionar Libros → 4. Buscar Libro por ISBN
ISBN: 978-950-04-2749-1
```

#### 3. Crear Libro CON Ficha (Transacción)
```
Menú Principal → 3. TRANSACCIONES → 1. Crear Libro con Ficha
```

Ingresa los datos solicitados. Ambos registros se crean de forma atómica.

#### 4. Actualizar Libro y Ficha
```
Menú Principal → 3. TRANSACCIONES → 3. Actualizar Libro y Ficha
```

#### 5. Eliminar Libro con Ficha
```
Menú Principal → 3. TRANSACCIONES → 2. Eliminar Libro con Ficha
```

Ambos registros se marcan como eliminados (baja lógica) en una transacción.

---

## Arquitectura del Sistema

### Estructura de Capas
```
prog2_TPI/
├── src/
│   ├── config/
│   │   ├── DatabaseConnection.java    # Gestión de conexiones
│   │   └── db.properties              # Credenciales BD
│   ├── DAO/
│   │   ├── GenericDAO.java            # CRUD genérico
│   │   ├── LibroDao.java              # DAO específico Libro
│   │   └── FichaBibliograficaDAO.java # DAO específico Ficha
│   ├── service/
│   │   ├── GenericService.java        # Lógica de negocio base
│   │   ├── LibroService.java          # Transacciones ACID
│   │   └── FichaBibliograficaService.java
│   └── edu/utn/
│       ├── entities/
│       │   ├── Libro.java             # Entidad Libro
│       │   └── FichaBibliografica.java # Entidad Ficha
│       ├── Main.java                  # Punto de entrada
│       └── AppMenu.java               # Interfaz de usuario
├── sql/
│   ├── BBDD.sql                       # Estructura de tablas
│   └── datos_de_prueba.sql            # Datos iniciales
└── README.md
```

### Diagrama de Relación
```
┌─────────────┐          1:1           ┌──────────────────────┐
│   Libro     │◄──────────────────────►│ FichaBibliografica   │
├─────────────┤                        ├──────────────────────┤
│ id (PK)     │                        │ id (PK)              │
│ titulo      │                        │ isbn (UNIQUE)        │
│ autor       │                        │ id_libro (FK, UNIQUE)│
│ editorial   │                        │ clasificacion_dewey  │
│ anio_edicion│                        │ estanteria           │
│ eliminado   │                        │ idioma               │
└─────────────┘                        │ eliminado            │
                                       └──────────────────────┘
```

---

##  Transacciones Implementadas

### 1. Crear Libro con Ficha
- **Atomicidad:** Ambos registros se crean o ninguno
- **Uso:** Cuando se ingresa un nuevo libro al catálogo

### 2. Eliminar Libro con Ficha
- **Atomicidad:** Ambos se marcan como eliminados o ninguno
- **Uso:** Baja lógica de libros del sistema

### 3. Actualizar Libro y Ficha
- **Atomicidad:** Ambos se actualizan o ninguno
- **Uso:** Modificación de datos catalogados

### 4. Demo Rollback (Fallo Simulado)
- **Objetivo:** Demostrar que el rollback funciona correctamente
- **Comportamiento:** Crea un libro, simula error, revierte todo

---

##  Video Demostrativo

[Link al video en YouTube/Drive](PEGAR_LINK)

##  Equipo de Desarrollo

**Pedro Hidalgo**

- Diseño y creación de la Base de datos
- Desarrollo de capa de modelo en Java

**Joaquín Escobar**

- Desarrollo de capa de Servicios y Transacciones
- Desarrollo del menú de la app

**Fiorela García**

- Desarrollo de capa de Servicios y Transacciones
- Documentación y UML
- Desarrollo de Pruebas Unitarias

**Martín Gómez**

- Desarrollo de capa DAO

---

##  Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"
**Solución:** Verificar contraseña en `db.properties`

### Error: "No se encontró el driver JDBC"
**Solución:** Agregar `mysql-connector-j.jar` a las librerías del proyecto

### Error: "Table 'prog2_tpi.libros' doesn't exist"
**Solución:** Ejecutar `sql/BBDD.sql` en MySQL

### Error: "Duplicate entry for key 'isbn_UNIQUE'"
**Solución:** Esto es esperado. Demuestra la validación. Usar otro ISBN.
