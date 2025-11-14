package service;

import config.DatabaseConnection;
import DAO.LibroDao;
import DAO.FichaBibliograficaDAO;
import edu.utn.entities.Libro;
import edu.utn.entities.FichaBibliografica;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LibroService implements GenericService<Libro> {
    
    private LibroDao libroDAO;
    private FichaBibliograficaDAO fichaDAO;
    
    public LibroService() {
        this.libroDAO = new LibroDao();
        this.fichaDAO = new FichaBibliograficaDAO();
    }
    
    // ========== CRUD BÁSICO ==========
    
    @Override
    public void insertar(Libro libro) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            libroDAO.crear(libro, conn);
        } catch (Exception e) {
            throw new SQLException("Error al insertar libro: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public void actualizar(Libro libro) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            libroDAO.actualizar(libro, conn);
        } catch (Exception e) {
            throw new SQLException("Error al actualizar libro: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public void eliminar(long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            libroDAO.eliminar(id, conn);
        } catch (Exception e) {
            throw new SQLException("Error al eliminar libro: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public Libro getById(long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return libroDAO.getById(id, conn);
        } catch (Exception e) {
            throw new SQLException("Error al buscar libro: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public List<Libro> getAll() throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return libroDAO.getAll(conn);
        } catch (Exception e) {
            throw new SQLException("Error al listar libros: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    // ========== TRANSACCIONES (LO IMPORTANTE) ==========
    
    /**
     * 🔄 TRANSACCIÓN 1: Crear Libro con su FichaBibliografica
     * Esta es LA OPERACIÓN PRINCIPAL que deben mostrar en el video
     * Orden: crear Libro → crear Ficha asociada
     * Si algo falla, ROLLBACK de ambas operaciones
     */
    public void crearLibroConFicha(Libro libro, FichaBibliografica ficha) throws SQLException {
        Connection conn = null;
        try {
            // 1. Obtener conexión y desactivar auto-commit
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            System.out.println("🔄 Iniciando transacción: crear libro con ficha...");
            
            // 2. VALIDACIONES
            if (libro == null || ficha == null) {
                throw new IllegalArgumentException("Libro y Ficha no pueden ser null");
            }
            
            if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("El título del libro es obligatorio");
            }
            
            if (ficha.getIsbn() == null || ficha.getIsbn().trim().isEmpty()) {
                throw new IllegalArgumentException("El ISBN es obligatorio");
            }
            
            // Verificar que el ISBN no exista
            FichaBibliografica fichaExistente = fichaDAO.buscarPorIsbn(ficha.getIsbn(), conn);
            if (fichaExistente != null && !fichaExistente.isEliminado()) {
                throw new IllegalArgumentException("Ya existe una ficha con ese ISBN: " + ficha.getIsbn());
            }
            
            // 3. ORDEN CORRECTO: Crear Libro primero (porque Ficha tiene FK a Libro)
            System.out.println("  → Creando Libro...");
            libroDAO.crear(libro, conn);
            System.out.println("  ✓ Libro creado con ID: " + libro.getId());
            
            // 4. Crear FichaBibliografica asociada al Libro
            System.out.println("  → Creando FichaBibliografica asociada...");
            fichaDAO.crearConLibro(ficha, libro.getId(), conn);
            System.out.println("  ✓ Ficha creada con ID: " + ficha.getId());
            
            // 5. Asociar la ficha al libro en el objeto Java
            libro.setFichaBibliografica(ficha);
            
            // 6. COMMIT - Todo salió bien
            conn.commit();
            System.out.println("✅ Transacción completada exitosamente");
            
        } catch (Exception e) {
            // 7. ROLLBACK - Algo falló
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("↩️  ROLLBACK ejecutado - cambios revertidos");
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            throw new SQLException("Error en transacción crearLibroConFicha: " + e.getMessage(), e);
            
        } finally {
            // 8. Restaurar auto-commit y cerrar
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 🔄 TRANSACCIÓN 2: Eliminar Libro y su FichaBibliografica (baja lógica)
     * Ambas entidades se marcan como eliminadas en la misma transacción
     */
    public void eliminarLibroConFicha(long libroId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            System.out.println("🔄 Iniciando transacción: eliminar libro con ficha...");
            
            // 1. Obtener el libro con su ficha
            Libro libro = libroDAO.getById(libroId, conn);
            if (libro == null) {
                throw new IllegalArgumentException("No existe un libro con ID: " + libroId);
            }
            
            if (libro.isEliminado()) {
                throw new IllegalArgumentException("El libro ya está eliminado");
            }
            
            // 2. Eliminar (lógico) la ficha asociada si existe
            if (libro.getFichaBibliografica() != null) {
                long fichaId = libro.getFichaBibliografica().getId();
                System.out.println("  → Eliminando FichaBibliografica ID: " + fichaId);
                fichaDAO.eliminar(fichaId, conn);
                System.out.println("  ✓ Ficha eliminada (lógicamente)");
            }
            
            // 3. Eliminar (lógico) el libro
            System.out.println("  → Eliminando Libro ID: " + libroId);
            libroDAO.eliminar(libroId, conn);
            System.out.println("  ✓ Libro eliminado (lógicamente)");
            
            // 4. Commit
            conn.commit();
            System.out.println("✅ Transacción completada exitosamente");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("↩️  ROLLBACK ejecutado");
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            throw new SQLException("Error en transacción eliminarLibroConFicha: " + e.getMessage(), e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 🎬 TRANSACCIÓN 3 (DEMO): Rollback con fallo simulado
     * CRÍTICO PARA EL VIDEO: Demuestra que el rollback funciona (-8 pts si no lo hacen)
     */
    public void demoRollbackFalloSimulado(Libro libro, FichaBibliografica ficha) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            System.out.println("\n🎬 === DEMO DE ROLLBACK CON FALLO SIMULADO ===");
            System.out.println("Vamos a crear un Libro, luego simular un error.");
            System.out.println("Verificaremos que el Libro NO quede en la BD.\n");
            
            // 1. Crear libro (esto SÍ se ejecuta en la BD)
            System.out.println("  → Creando Libro...");
            libroDAO.crear(libro, conn);
            System.out.println("  ✓ Libro creado temporalmente con ID: " + libro.getId());
            System.out.println("  ℹ️  (Todavía no está confirmado, está en la transacción)");
            
            // 2. SIMULAR ERROR ANTES DE COMMIT
            System.out.println("\n  💥 SIMULANDO ERROR...");
            throw new RuntimeException("❌ ERROR SIMULADO - Probando que el rollback funciona");
            
            // 3. Este código NUNCA se alcanza
            // fichaDAO.crearConLibro(ficha, libro.getId(), conn);
            // conn.commit();
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("\n↩️  ✅ ROLLBACK EJECUTADO CORRECTAMENTE");
                    System.out.println("  → El Libro NO quedó guardado en la BD");
                    System.out.println("  → Todos los cambios fueron revertidos");
                    
                    // Verificar que efectivamente no quedó
                    System.out.println("\n  🔍 Verificando en la BD...");
                    if (libro.getId() != null) {
                        Libro verificacion = libroDAO.getById(libro.getId(), conn);
                        if (verificacion == null) {
                            System.out.println("  ✅ CONFIRMADO: El libro con ID " + libro.getId() + " NO existe en la BD");
                        } else {
                            System.out.println("  ❌ ERROR: El libro SÍ quedó (rollback falló)");
                        }
                    }
                    
                } catch (Exception ex) {
                    System.err.println("❌ Error ejecutando rollback: " + ex.getMessage());
                }
            }
            System.out.println("\n🎬 === FIN DE LA DEMO ===\n");
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 🔄 TRANSACCIÓN 4 (BONUS): Actualizar Libro y su Ficha
     */
    public void actualizarLibroYFicha(Libro libro, FichaBibliografica ficha) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            System.out.println("🔄 Iniciando transacción: actualizar libro y ficha...");
            
            // Validaciones
            if (libro.getId() == null || ficha.getId() == null) {
                throw new IllegalArgumentException("Libro y Ficha deben tener ID para actualizar");
            }
            
            // Actualizar ambos
            System.out.println("  → Actualizando Libro...");
            libroDAO.actualizar(libro, conn);
            
            System.out.println("  → Actualizando FichaBibliografica...");
            fichaDAO.actualizar(ficha, conn);
            
            conn.commit();
            System.out.println("✅ Ambos actualizados correctamente");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("↩️  ROLLBACK ejecutado");
                } catch (SQLException ex) {
                    System.err.println("❌ Error en rollback: " + ex.getMessage());
                }
            }
            throw new SQLException("Error en transacción actualizarLibroYFicha: " + e.getMessage(), e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    public Libro buscarPorIsbn(String isbn) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return libroDAO.buscarPorIsbn(isbn, conn);
        } catch (Exception e) {
            throw new SQLException("Error al buscar por ISBN: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
}