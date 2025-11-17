package DAO;

import edu.utn.entities.Libro;
import edu.utn.entities.FichaBibliografica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDao implements GenericDAO<Libro> {

    private FichaBibliograficaDAO fichaDAO = new FichaBibliograficaDAO();

    @Override
    public void crear(Libro libro, Connection conn) throws Exception {
        String sql = "INSERT INTO libros(titulo, autor, editorial, anio_edicion, eliminado) VALUES (?, ?, ?, ?, false)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setInt(4, libro.getAnioEdicion());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getLong(1));
                }
            }
        }
    }
    
    @Override
    public Libro getById(long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM libros WHERE id = ? AND eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = mapearLibro(rs);
                    // Cargar la ficha asociada
                    cargarFichaBibliografica(libro, conn);
                    return libro;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Libro> getAll(Connection conn) throws Exception {
        String sql = "SELECT * FROM libros WHERE eliminado = false";
        List<Libro> libros = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Libro libro = mapearLibro(rs);
                cargarFichaBibliografica(libro, conn);
                libros.add(libro);
            }
        }
        return libros;
    }
    
    @Override 
    public void actualizar(Libro libro, Connection conn) throws Exception {
        String sql = "UPDATE libros SET eliminado = ?, titulo = ?, autor = ?, editorial = ?, anio_edicion = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, libro.isEliminado());
            stmt.setString(2, libro.getTitulo());
            stmt.setString(3, libro.getAutor());
            stmt.setString(4, libro.getEditorial());
            stmt.setInt(5, libro.getAnioEdicion());
            stmt.setLong(6, libro.getId());
            stmt.executeUpdate();
        }
    }
    
    @Override
    public void eliminar(Long id, Connection conn) throws Exception {
        String sql = "UPDATE libros SET eliminado = true WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    

    public Libro buscarPorIsbn(String isbn, Connection conn) throws Exception {
        String sql = "SELECT l.* FROM libros l " +
                     "INNER JOIN fichas_bibliograficas f ON l.id = f.id_libro " +
                     "WHERE f.isbn = ? AND l.eliminado = false AND f.eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = mapearLibro(rs);
                    cargarFichaBibliografica(libro, conn);
                    return libro;
                }
            }
        }
        return null;
    }
    

    private void cargarFichaBibliografica(Libro libro, Connection conn) throws Exception {
        String sql = "SELECT * FROM fichas_bibliograficas WHERE id_libro = ? AND eliminado = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, libro.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FichaBibliografica ficha = fichaDAO.mapearFicha(rs);
                    libro.setFichaBibliografica(ficha);
                }
            }
        }
    }
    
    // Mapea un ResultSet a un objeto Libro

    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getLong("id"));
        libro.setEliminado(rs.getBoolean("eliminado"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnioEdicion(rs.getInt("anio_edicion"));
        return libro;
    }
}
