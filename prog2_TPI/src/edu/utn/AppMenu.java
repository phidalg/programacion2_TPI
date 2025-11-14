package edu.utn;

import edu.utn.entities.Libro;
import edu.utn.entities.FichaBibliografica;
import service.LibroService;
import service.FichaBibliograficaService;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class AppMenu {
    
    private Scanner scanner;
    private LibroService libroService;
    private FichaBibliograficaService fichaService;
    
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        this.libroService = new LibroService();
        this.fichaService = new FichaBibliograficaService();
    }
    
    public void mostrarMenuPrincipal() {
        int opcion;
        do {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("        📚 SISTEMA DE GESTIÓN DE BIBLIOTECA 📚");
            System.out.println("=".repeat(60));
            System.out.println("1.  Gestionar Libros");
            System.out.println("2.  Gestionar Fichas Bibliográficas");
            System.out.println("3.  🔄 TRANSACCIONES (Operaciones Compuestas)");
            System.out.println("4.  🎬 DEMO: Rollback con Fallo Simulado");
            System.out.println("0.  Salir");
            System.out.println("=".repeat(60));
            System.out.print("Seleccione una opción: ");
            
            opcion = leerEnteroSeguro();
            
            switch (opcion) {
                case 1:
                    menuLibros();
                    break;
                case 2:
                    menuFichas();
                    break;
                case 3:
                    menuTransacciones();
                    break;
                case 4:
                    demoRollback();
                    break;
                case 0:
                    System.out.println("\n👋 ¡Hasta luego!");
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        } while (opcion != 0);
    }
    
    // ========== MENÚ LIBROS ==========
    
    private void menuLibros() {
        int opcion;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("           📖 GESTIÓN DE LIBROS 📖");
            System.out.println("=".repeat(50));
            System.out.println("1. Crear Libro (solo libro, sin ficha)");
            System.out.println("2. Listar todos los Libros");
            System.out.println("3. Buscar Libro por ID");
            System.out.println("4. Buscar Libro por ISBN");
            System.out.println("5. Actualizar Libro");
            System.out.println("6. Eliminar Libro (baja lógica)");
            System.out.println("0. Volver");
            System.out.println("=".repeat(50));
            System.out.print("Opción: ");
            
            opcion = leerEnteroSeguro();
            
            switch (opcion) {
                case 1:
                    crearLibroSimple();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    buscarLibroPorId();
                    break;
                case 4:
                    buscarLibroPorIsbn();
                    break;
                case 5:
                    actualizarLibro();
                    break;
                case 6:
                    eliminarLibro();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        } while (opcion != 0);
    }
    
    private void crearLibroSimple() {
        try {
            System.out.println("\n--- Crear Libro Simple ---");
            
            scanner.nextLine(); // Limpiar buffer
            
            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim().toUpperCase();
            
            if (titulo.isEmpty()) {
                System.out.println("❌ El título no puede estar vacío");
                return;
            }
            
            System.out.print("Autor: ");
            String autor = scanner.nextLine().trim().toUpperCase();
            
            if (autor.isEmpty()) {
                System.out.println("❌ El autor no puede estar vacío");
                return;
            }
            
            System.out.print("Editorial: ");
            String editorial = scanner.nextLine().trim();
            
            System.out.print("Año de Edición: ");
            int anioEdicion = leerEnteroSeguro();
            
            if (anioEdicion < 1000 || anioEdicion > 2100) {
                System.out.println("❌ Año inválido");
                return;
            }
            
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setEditorial(editorial);
            libro.setAnioEdicion(anioEdicion);
            libro.setEliminado(false);
            
            libroService.insertar(libro);
            System.out.println("✅ Libro creado exitosamente con ID: " + libro.getId());
            
        } catch (SQLException e) {
            System.err.println("❌ Error al crear libro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void listarLibros() {
        try {
            System.out.println("\n" + "=".repeat(100));
            System.out.println("                              LISTA DE LIBROS");
            System.out.println("=".repeat(100));
            
            List<Libro> libros = libroService.getAll();
            
            if (libros.isEmpty()) {
                System.out.println("📭 No hay libros registrados");
            } else {
                System.out.printf("%-5s %-30s %-25s %-20s %-10s%n", 
                    "ID", "TÍTULO", "AUTOR", "EDITORIAL", "AÑO");
                System.out.println("-".repeat(100));
                
                for (Libro libro : libros) {
                    System.out.printf("%-5d %-30s %-25s %-20s %-10d%n",
                        libro.getId(),
                        truncar(libro.getTitulo(), 30),
                        truncar(libro.getAutor(), 25),
                        truncar(libro.getEditorial(), 20),
                        libro.getAnioEdicion());
                        
                    if (libro.getFichaBibliografica() != null) {
                        System.out.println("      └─ 📋 Ficha: ISBN " + 
                            libro.getFichaBibliografica().getIsbn());
                    }
                }
            }
            System.out.println("=".repeat(100));
            System.out.println("Total: " + libros.size() + " libro(s)");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al listar libros: " + e.getMessage());
        }
    }
    
    private void buscarLibroPorId() {
        try {
            System.out.print("\nIngrese ID del libro: ");
            long id = leerLongSeguro();
            
            Libro libro = libroService.getById(id);
            
            if (libro == null) {
                System.out.println("❌ No se encontró un libro con ID: " + id);
            } else {
                mostrarDetalleLibro(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void buscarLibroPorIsbn() {
        try {
            scanner.nextLine(); // Limpiar buffer
            System.out.print("\nIngrese ISBN: ");
            String isbn = scanner.nextLine().trim().toUpperCase();
            
            if (isbn.isEmpty()) {
                System.out.println("❌ Debe ingresar un ISBN");
                return;
            }
            
            Libro libro = libroService.buscarPorIsbn(isbn);
            
            if (libro == null) {
                System.out.println("❌ No se encontró un libro con ISBN: " + isbn);
            } else {
                mostrarDetalleLibro(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void actualizarLibro() {
        try {
            System.out.print("\nIngrese ID del libro a actualizar: ");
            long id = leerLongSeguro();
            
            Libro libro = libroService.getById(id);
            
            if (libro == null) {
                System.out.println("❌ No se encontró un libro con ID: " + id);
                return;
            }
            
            System.out.println("\n--- Datos actuales ---");
            mostrarDetalleLibro(libro);
            
            scanner.nextLine(); // Limpiar buffer
            
            System.out.print("\nNuevo título (Enter para mantener): ");
            String titulo = scanner.nextLine().trim();
            if (!titulo.isEmpty()) {
                libro.setTitulo(titulo.toUpperCase());
            }
            
            System.out.print("Nuevo autor (Enter para mantener): ");
            String autor = scanner.nextLine().trim();
            if (!autor.isEmpty()) {
                libro.setAutor(autor.toUpperCase());
            }
            
            System.out.print("Nueva editorial (Enter para mantener): ");
            String editorial = scanner.nextLine().trim();
            if (!editorial.isEmpty()) {
                libro.setEditorial(editorial);
            }
            
            System.out.print("Nuevo año (0 para mantener): ");
            int anio = leerEnteroSeguro();
            if (anio > 0) {
                if (anio < 1000 || anio > 2100) {
                    System.out.println("❌ Año inválido, se mantiene el anterior");
                } else {
                    libro.setAnioEdicion(anio);
                }
            }
            
            libroService.actualizar(libro);
            System.out.println("✅ Libro actualizado exitosamente");
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void eliminarLibro() {
        try {
            System.out.print("\nIngrese ID del libro a eliminar: ");
            long id = leerLongSeguro();
            
            Libro libro = libroService.getById(id);
            
            if (libro == null) {
                System.out.println("❌ No se encontró un libro con ID: " + id);
                return;
            }
            
            mostrarDetalleLibro(libro);
            
            System.out.print("\n¿Confirma eliminar este libro? (S/N): ");
            scanner.nextLine(); // Limpiar buffer
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                libroService.eliminar(id);
                System.out.println("✅ Libro eliminado (baja lógica)");
            } else {
                System.out.println("❌ Operación cancelada");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    // ========== MENÚ FICHAS ==========
    
    private void menuFichas() {
        int opcion;
        do {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("      📋 GESTIÓN DE FICHAS BIBLIOGRÁFICAS 📋");
            System.out.println("=".repeat(50));
            System.out.println("1. Crear Ficha");
            System.out.println("2. Listar todas las Fichas");
            System.out.println("3. Buscar Ficha por ID");
            System.out.println("4. Buscar Ficha por ISBN");
            System.out.println("5. Actualizar Ficha");
            System.out.println("6. Eliminar Ficha");
            System.out.println("0. Volver");
            System.out.println("=".repeat(50));
            System.out.print("Opción: ");
            
            opcion = leerEnteroSeguro();
            
            switch (opcion) {
                case 1:
                    crearFicha();
                    break;
                case 2:
                    listarFichas();
                    break;
                case 3:
                    buscarFichaPorId();
                    break;
                case 4:
                    buscarFichaPorIsbn();
                    break;
                case 5:
                    actualizarFicha();
                    break;
                case 6:
                    eliminarFicha();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        } while (opcion != 0);
    }
    
    private void crearFicha() {
        try {
            System.out.println("\n--- Crear Ficha Bibliográfica ---");
            System.out.println("⚠️  Nota: Esta ficha NO estará asociada a ningún libro.");
            System.out.println("    Use la opción 'TRANSACCIONES' para crear libro+ficha juntos.");
            
            scanner.nextLine(); // Limpiar buffer
            
            System.out.print("\nISBN: ");
            String isbn = scanner.nextLine().trim().toUpperCase();
            
            if (isbn.isEmpty()) {
                System.out.println("❌ El ISBN no puede estar vacío");
                return;
            }
            
            System.out.print("Clasificación Dewey: ");
            String clasificacion = scanner.nextLine().trim();
            
            System.out.print("Estantería: ");
            String estanteria = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("Idioma: ");
            String idioma = scanner.nextLine().trim();
            
            FichaBibliografica ficha = new FichaBibliografica();
            ficha.setIsbn(isbn);
            ficha.setClasificacionDewey(clasificacion);
            ficha.setEstanteria(estanteria);
            ficha.setIdioma(idioma);
            ficha.setEliminado(false);
            
            fichaService.insertar(ficha);
            System.out.println("✅ Ficha bibliográfica creada exitosamente con ID: " + ficha.getId());
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void listarFichas() {
        try {
            System.out.println("\n" + "=".repeat(90));
            System.out.println("                    FICHAS BIBLIOGRÁFICAS");
            System.out.println("=".repeat(90));
            
            List<FichaBibliografica> fichas = fichaService.getAll();
            
            if (fichas.isEmpty()) {
                System.out.println("📭 No hay fichas registradas");
            } else {
                System.out.printf("%-5s %-20s %-20s %-15s %-20s%n",
                    "ID", "ISBN", "CLASIFICACIÓN", "ESTANTERÍA", "IDIOMA");
                System.out.println("-".repeat(90));
                
                for (FichaBibliografica ficha : fichas) {
                    System.out.printf("%-5d %-20s %-20s %-15s %-20s%n",
                        ficha.getId(),
                        ficha.getIsbn(),
                        truncar(ficha.getClasificacionDewey(), 20),
                        ficha.getEstanteria(),
                        ficha.getIdioma());
                }
            }
            System.out.println("=".repeat(90));
            System.out.println("Total: " + fichas.size() + " ficha(s)");
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void buscarFichaPorId() {
        try {
            System.out.print("\nIngrese ID de la ficha: ");
            long id = leerLongSeguro();
            
            FichaBibliografica ficha = fichaService.getById(id);
            
            if (ficha == null) {
                System.out.println("❌ No se encontró una ficha con ID: " + id);
            } else {
                mostrarDetalleFicha(ficha);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void buscarFichaPorIsbn() {
        try {
            scanner.nextLine();
            System.out.print("\nIngrese ISBN: ");
            String isbn = scanner.nextLine().trim().toUpperCase();
            
            if (isbn.isEmpty()) {
                System.out.println("❌ Debe ingresar un ISBN");
                return;
            }
            
            FichaBibliografica ficha = fichaService.buscarPorIsbn(isbn);
            
            if (ficha == null) {
                System.out.println("❌ No se encontró una ficha con ISBN: " + isbn);
            } else {
                mostrarDetalleFicha(ficha);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void actualizarFicha() {
        try {
            System.out.print("\nIngrese ID de la ficha a actualizar: ");
            long id = leerLongSeguro();
            
            FichaBibliografica ficha = fichaService.getById(id);
            
            if (ficha == null) {
                System.out.println("❌ No se encontró una ficha con ID: " + id);
                return;
            }
            
            System.out.println("\n--- Datos actuales ---");
            mostrarDetalleFicha(ficha);
            
            scanner.nextLine();
            
            System.out.print("\nNueva clasificación Dewey (Enter para mantener): ");
            String clasificacion = scanner.nextLine().trim();
            if (!clasificacion.isEmpty()) {
                ficha.setClasificacionDewey(clasificacion);
            }
            
            System.out.print("Nueva estantería (Enter para mantener): ");
            String estanteria = scanner.nextLine().trim();
            if (!estanteria.isEmpty()) {
                ficha.setEstanteria(estanteria.toUpperCase());
            }
            
            System.out.print("Nuevo idioma (Enter para mantener): ");
            String idioma = scanner.nextLine().trim();
            if (!idioma.isEmpty()) {
                ficha.setIdioma(idioma);
            }
            
            fichaService.actualizar(ficha);
            System.out.println("✅ Ficha actualizada exitosamente");
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void eliminarFicha() {
        try {
            System.out.print("\nIngrese ID de la ficha a eliminar: ");
            long id = leerLongSeguro();
            
            FichaBibliografica ficha = fichaService.getById(id);
            
            if (ficha == null) {
                System.out.println("❌ No se encontró una ficha con ID: " + id);
                return;
            }
            
            mostrarDetalleFicha(ficha);
            
            System.out.print("\n¿Confirma eliminar esta ficha? (S/N): ");
            scanner.nextLine();
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                fichaService.eliminar(id);
                System.out.println("✅ Ficha eliminada (baja lógica)");
            } else {
                System.out.println("❌ Operación cancelada");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    // ========== MENÚ TRANSACCIONES ==========
    
    private void menuTransacciones() {
        int opcion;
        do {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("        🔄 TRANSACCIONES (Operaciones Compuestas) 🔄");
            System.out.println("=".repeat(60));
            System.out.println("Estas operaciones modifican LIBRO y FICHA en una sola");
            System.out.println("transacción atómica con commit/rollback automático.");
            System.out.println("=".repeat(60));
            System.out.println("1. Crear Libro CON su Ficha Bibliográfica (Transacción)");
            System.out.println("2. Eliminar Libro CON su Ficha (Transacción)");
            System.out.println("3. Actualizar Libro Y su Ficha (Transacción)");
            System.out.println("0. Volver");
            System.out.println("=".repeat(60));
            System.out.print("Opción: ");
            
            opcion = leerEnteroSeguro();
            
            switch (opcion) {
                case 1:
                    crearLibroConFichaTransaccional();
                    break;
                case 2:
                    eliminarLibroConFichaTransaccional();
                    break;
                case 3:
                    actualizarLibroYFichaTransaccional();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }
        } while (opcion != 0);
    }
    
    private void crearLibroConFichaTransaccional() {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("   🔄 TRANSACCIÓN: Crear Libro + Ficha Bibliográfica");
            System.out.println("=".repeat(60));
            System.out.println("Esta operación creará AMBOS en una transacción atómica.");
            System.out.println("Si algo falla, NINGUNO se creará (rollback automático).\n");
            
            scanner.nextLine();
            
            // Datos del libro
            System.out.println("--- DATOS DEL LIBRO ---");
            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim().toUpperCase();
            
            if (titulo.isEmpty()) {
                System.out.println("❌ El título no puede estar vacío");
                return;
            }
            
            System.out.print("Autor: ");
            String autor = scanner.nextLine().trim().toUpperCase();
            
            if (autor.isEmpty()) {
                System.out.println("❌ El autor no puede estar vacío");
                return;
            }
            
            System.out.print("Editorial: ");
            String editorial = scanner.nextLine().trim();
            
            System.out.print("Año de Edición: ");
            int anioEdicion = leerEnteroSeguro();
            
            if (anioEdicion < 1000 || anioEdicion > 2100) {
                System.out.println("❌ Año inválido");
                return;
            }
            
            scanner.nextLine(); // Limpiar
            
            // Datos de la ficha
            System.out.println("\n--- DATOS DE LA FICHA BIBLIOGRÁFICA ---");
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim().toUpperCase();
            
            if (isbn.isEmpty()) {
                System.out.println("❌ El ISBN no puede estar vacío");
                return;
            }
            
            System.out.print("Clasificación Dewey: ");
            String clasificacion = scanner.nextLine().trim();
            
            System.out.print("Estantería: ");
            String estanteria = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("Idioma: ");
            String idioma = scanner.nextLine().trim();
            
            // Crear objetos
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setEditorial(editorial);
            libro.setAnioEdicion(anioEdicion);
            libro.setEliminado(false);
            
            FichaBibliografica ficha = new FichaBibliografica();
            ficha.setIsbn(isbn);
            ficha.setClasificacionDewey(clasificacion);
            ficha.setEstanteria(estanteria);
            ficha.setIdioma(idioma);
            ficha.setEliminado(false);
            
            // Ejecutar transacción
            System.out.println("\n" + "-".repeat(60));
            libroService.crearLibroConFicha(libro, ficha);
            System.out.println("-".repeat(60));
            System.out.println("\n✅ TRANSACCIÓN EXITOSA: Libro y Ficha creados correctamente");
            System.out.println("   Libro ID: " + libro.getId());
            System.out.println("   Ficha ID: " + ficha.getId());
            
        } catch (SQLException e) {
            System.err.println("\n❌ TRANSACCIÓN FALLIDA: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ Error: " + e.getMessage());
        }
    }
    
    private void eliminarLibroConFichaTransaccional() {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("     🔄 TRANSACCIÓN: Eliminar Libro + Ficha");
            System.out.println("=".repeat(60));
            System.out.println("Esta operación eliminará AMBOS (baja lógica) en una");
            System.out.println("transacción atómica. Si falla, ninguno se elimina.\n");
            
            System.out.print("Ingrese ID del libro: ");
            long id = leerLongSeguro();
            
            Libro libro = libroService.getById(id);
            
            if (libro == null) {
                System.out.println("❌ No se encontró un libro con ID: " + id);
                return;
            }
            
            mostrarDetalleLibro(libro);
            
            if (libro.getFichaBibliografica() == null) {
                System.out.println("\n⚠️  Este libro NO tiene ficha asociada.");
                System.out.println("    ¿Desea eliminarlo de todas formas? (S/N): ");
            } else {
                System.out.print("\n¿Confirma eliminar el libro Y su ficha? (S/N): ");
            }
            
            scanner.nextLine();
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                System.out.println("\n" + "-".repeat(60));
                libroService.eliminarLibroConFicha(id);
                System.out.println("-".repeat(60));
                System.out.println("\n✅ TRANSACCIÓN EXITOSA: Eliminación completada");
            } else {
                System.out.println("❌ Operación cancelada");
            }
            
        } catch (SQLException e) {
            System.err.println("\n❌ TRANSACCIÓN FALLIDA: " + e.getMessage());
        }
    }
    
    private void actualizarLibroYFichaTransaccional() {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("   🔄 TRANSACCIÓN: Actualizar Libro Y Ficha");
            System.out.println("=".repeat(60));
            System.out.println("Esta operación actualizará AMBOS en una transacción");
            System.out.println("atómica. Si falla, ningún cambio se guardará.\n");
            
            System.out.print("Ingrese ID del libro: ");
            long libroId = leerLongSeguro();
            
            Libro libro = libroService.getById(libroId);
            
            if (libro == null) {
                System.out.println("❌ No se encontró un libro con ID: " + libroId);
                return;
            }
            
            if (libro.getFichaBibliografica() == null) {
                System.out.println("❌ Este libro no tiene ficha asociada");
                System.out.println("   Use la opción de actualizar libro individual.");
                return;
            }
            
            FichaBibliografica ficha = libro.getFichaBibliografica();
            
            System.out.println("\n--- Datos actuales ---");
            mostrarDetalleLibro(libro);
            
            scanner.nextLine();
            
            // Actualizar libro
            System.out.println("\n--- ACTUALIZAR LIBRO ---");
            System.out.print("Nuevo título (Enter para mantener): ");
            String titulo = scanner.nextLine().trim();
            if (!titulo.isEmpty()) libro.setTitulo(titulo.toUpperCase());
            
            System.out.print("Nuevo autor (Enter para mantener): ");
            String autor = scanner.nextLine().trim();
            if (!autor.isEmpty()) libro.setAutor(autor.toUpperCase());
            
            System.out.print("Nueva editorial (Enter para mantener): ");
            String editorial = scanner.nextLine().trim();
            if (!editorial.isEmpty()) libro.setEditorial(editorial);
            
            System.out.print("Nuevo año (0 para mantener): ");
            int anio = leerEnteroSeguro();
            if (anio > 0) {
                if (anio < 1000 || anio > 2100) {
                    System.out.println("⚠️  Año inválido, se mantendrá el anterior");
                } else {
                    libro.setAnioEdicion(anio);
                }
            }
            
            scanner.nextLine(); // Limpiar
            
            // Actualizar ficha
            System.out.println("\n--- ACTUALIZAR FICHA ---");
            System.out.print("Nueva clasificación Dewey (Enter para mantener): ");
            String clasificacion = scanner.nextLine().trim();
            if (!clasificacion.isEmpty()) ficha.setClasificacionDewey(clasificacion);
            
            System.out.print("Nueva estantería (Enter para mantener): ");
            String estanteria = scanner.nextLine().trim();
            if (!estanteria.isEmpty()) ficha.setEstanteria(estanteria.toUpperCase());
            
            System.out.print("Nuevo idioma (Enter para mantener): ");
            String idioma = scanner.nextLine().trim();
            if (!idioma.isEmpty()) ficha.setIdioma(idioma);
            
            System.out.println("\n" + "-".repeat(60));
            libroService.actualizarLibroYFicha(libro, ficha);
            System.out.println("-".repeat(60));
            System.out.println("\n✅ TRANSACCIÓN EXITOSA: Ambos actualizados correctamente");
            
        } catch (SQLException e) {
            System.err.println("\n❌ TRANSACCIÓN FALLIDA: " + e.getMessage());
        }
    }
    
    // ========== DEMO ROLLBACK ==========
    
    private void demoRollback() {
        try {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("        🎬 DEMOSTRACIÓN DE ROLLBACK CON FALLO SIMULADO 🎬");
            System.out.println("=".repeat(70));
            System.out.println("Esta demo creará un Libro en la BD, luego simulará un error");
            System.out.println("ANTES del commit. Veremos cómo el ROLLBACK revierte todo.");
            System.out.println("\n⚠️  IMPORTANTE: Esta es una demostración técnica para el video.");
            System.out.println("   El error es INTENCIONAL para probar el rollback.");
            System.out.println("=".repeat(70));
            
            scanner.nextLine();
            
            System.out.print("\nIngrese un ISBN temporal para la demo: ");
            String isbn = scanner.nextLine().trim().toUpperCase();
            
            if (isbn.isEmpty()) {
                isbn = "DEMO-" + System.currentTimeMillis();
                System.out.println("Usando ISBN generado: " + isbn);
            }
            
            // Crear datos de prueba
            Libro libro = new Libro();
            libro.setTitulo("LIBRO DE PRUEBA - DEMO ROLLBACK");
            libro.setAutor("AUTOR DE PRUEBA");
            libro.setEditorial("Editorial Test");
            libro.setAnioEdicion(2024);
            libro.setEliminado(false);
            
            FichaBibliografica ficha = new FichaBibliografica();
            ficha.setIsbn(isbn);
            ficha.setClasificacionDewey("000.000");
            ficha.setEstanteria("DEMO-01");
            ficha.setIdioma("Español");
            ficha.setEliminado(false);
            
            System.out.println("\n📌 Presione Enter para iniciar la demo...");
            scanner.nextLine();
            
            // Ejecutar demo
            libroService.demoRollbackFalloSimulado(libro, ficha);
            
            System.out.println("\n✅ Demo completada exitosamente.");
            System.out.println("   El rollback funcionó correctamente.");
            System.out.println("=".repeat(70));
            
            System.out.print("\n¿Desea ejecutar la demo nuevamente? (S/N): ");
            String repetir = scanner.nextLine().trim().toUpperCase();
            if (repetir.equals("S") || repetir.equals("SI")) {
                demoRollback();
            }
            
        } catch (Exception e) {
            System.err.println("Error en demo: " + e.getMessage());
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private void mostrarDetalleLibro(Libro libro) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📖 DETALLE DEL LIBRO");
        System.out.println("=".repeat(60));
        System.out.println("ID:        " + libro.getId());
        System.out.println("Título:    " + libro.getTitulo());
        System.out.println("Autor:     " + libro.getAutor());
        System.out.println("Editorial: " + libro.getEditorial());
        System.out.println("Año:       " + libro.getAnioEdicion());
        
        if (libro.getFichaBibliografica() != null) {
            FichaBibliografica ficha = libro.getFichaBibliografica();
            System.out.println("\n📋 FICHA BIBLIOGRÁFICA ASOCIADA:");
            System.out.println("   ID:            " + ficha.getId());
            System.out.println("   ISBN:          " + ficha.getIsbn());
            System.out.println("   Clasificación: " + ficha.getClasificacionDewey());
            System.out.println("   Estantería:    " + ficha.getEstanteria());
            System.out.println("   Idioma:        " + ficha.getIdioma());
        } else {
            System.out.println("\n📋 Sin ficha bibliográfica asociada");
        }
        System.out.println("=".repeat(60));
    }
    
    private void mostrarDetalleFicha(FichaBibliografica ficha) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 DETALLE DE LA FICHA BIBLIOGRÁFICA");
        System.out.println("=".repeat(60));
        System.out.println("ID:            " + ficha.getId());
        System.out.println("ISBN:          " + ficha.getIsbn());
        System.out.println("Clasificación: " + ficha.getClasificacionDewey());
        System.out.println("Estantería:    " + ficha.getEstanteria());
        System.out.println("Idioma:        " + ficha.getIdioma());
        System.out.println("=".repeat(60));
    }
    
    private int leerEnteroSeguro() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                return valor;
            } catch (InputMismatchException e) {
                System.out.print("❌ Debe ingresar un número entero. Intente de nuevo: ");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
    
    private long leerLongSeguro() {
        while (true) {
            try {
                long valor = scanner.nextLong();
                if (valor < 0) {
                    System.out.print("❌ El ID debe ser positivo. Intente de nuevo: ");
                    continue;
                }
                return valor;
            } catch (InputMismatchException e) {
                System.out.print("❌ Debe ingresar un número válido. Intente de nuevo: ");
                scanner.nextLine();
            }
        }
    }
    
    private String truncar(String texto, int maxLength) {
        if (texto == null) return "";
        return texto.length() > maxLength ? 
            texto.substring(0, maxLength - 3) + "..." : texto;
    }
}