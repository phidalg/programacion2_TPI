package DAO;

import edu.utn.entities.FichaBibliografica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FichaBibliograficaDAO implements GenericDAO<FichaBibliografica> {
    
    @Override
    public void crear(FichaBibliografica fb, Connection conn) throws Exception {
        String sql = "INSERT INTO fichas_bibliograficas(isbn, clasificacion_dewey, estanteria, idioma, id_libro, eliminado) VALUES (?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fb.getIsbn());
            stmt.setString(2, fb.getClasificacionDewey());
            stmt.setString(3, fb.getEstanteria());
            stmt.setString(4, fb.getIdioma());
            // Por ahora id_libro es NULL, se establece cuando se asocia a un Libro
            stmt.setObject(5, null);
            
            //  FIX: Ejecutar el INSERT
            stmt.executeUpdate();
            
            //  FIX: Obtener el ID generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fb.setId(rs.getLong(1));
                }
            }
        }
    }
    
    /**
     * Crear ficha Y asociarla a un libro (para transacciones)
     */
    public void crearConLibro(FichaBibliografica fb, Long idLibro, Connection conn) throws Exception {
        String sql = "INSERT INTO fichas_bibliograficas(isbn, clasificacion_dewey, estanteria, idioma, id_libro, eliminado) VALUES (?, ?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fb.getIsbn());
            stmt.setString(2, fb.getClasificacionDewey());
            stmt.setString(3, fb.getEstanteria());
            stmt.setString(4, fb.getIdioma());
            stmt.setLong(5, idLibro);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fb.setId(rs.getLong(1));
                }
            }
        }
    }
    
    @Override
    public FichaBibliografica getById(long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM fichas_bibliograficas WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearFicha(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<FichaBibliografica> getAll(Connection conn) throws Exception {
        String sql = "SELECT * FROM fichas_bibliograficas WHERE eliminado = false";
        List<FichaBibliografica> fichas = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fichas.add(mapearFicha(rs));
            }
        }
        return fichas;
    }
    
    @Override 
    public void actualizar(FichaBibliografica fb, Connection conn) throws Exception {
        String sql = "UPDATE fichas_bibliograficas SET eliminado = ?, isbn = ?, clasificacion_dewey = ?, estanteria = ?, idioma = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, fb.isEliminado());
            stmt.setString(2, fb.getIsbn());
            stmt.setString(3, fb.getClasificacionDewey());
            stmt.setString(4, fb.getEstanteria());
            stmt.setString(5, fb.getIdioma());
            stmt.setLong(6, fb.getId());
            stmt.executeUpdate();
        }
    }
    
    @Override
    public void eliminar(Long id, Connection conn) throws Exception {
        // 🔧 FIX: Nombre correcto de tabla
        String sql = "UPDATE fichas_bibliograficas SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     *  NUEVO: Buscar ficha por ISBN
     */
    public FichaBibliografica buscarPorIsbn(String isbn, Connection conn) throws Exception {
        String sql = "SELECT * FROM fichas_bibliograficas WHERE isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearFicha(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Mapea un ResultSet a un objeto FichaBibliografica
     */
    public FichaBibliografica mapearFicha(ResultSet rs) throws SQLException {
        FichaBibliografica fb = new FichaBibliografica();
        fb.setId(rs.getLong("id"));
        fb.setEliminado(rs.getBoolean("eliminado"));
        fb.setIsbn(rs.getString("isbn"));
        // 🔧 FIX: Nombre correcto de columna con guión bajo
        fb.setClasificacionDewey(rs.getString("clasificacion_dewey"));
        fb.setEstanteria(rs.getString("estanteria"));
        fb.setIdioma(rs.getString("idioma"));
        return fb;
    }
}