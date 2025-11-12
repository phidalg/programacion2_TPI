
package DAO;

import edu.utn.entities.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LibroDao implements GenericDAO<Libro> {

    @Override
    public void crear(Libro libro, Connection conn)throws Exception{
        String sql = "INSERT INTO libros(titulo, autor, editorial, anio_edicion, eliminado)VALUES (?, ?, ?, ?, false)";
        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setInt(4, libro.getAnioEdicion());
            
            }
    }
    
    @Override
    public Libro getById(long id, Connection conn) throws Exception{
    String sql = "SELECT * FROM libros WHERE id = ? AND eliminado = false";
    try(PreparedStatement stmt = conn.prepareStatement(sql)){
        stmt.setLong(1,id);
        try (ResultSet rs = stmt.executeQuery()) {
            if(rs.next())
                return mapearLibro(rs);
            }
        }
    return null;
}
    
    @Override
    public List<Libro> getAll(Connection conn) throws Exception{
        String sql = "SELECT * FROM libros WHERE eliminado = false";
        List<Libro> libros = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                libros.add(mapearLibro(rs));
            }
        }
        return libros;
    }
    
    @Override 
    public void actualizar (Libro libro, Connection conn) throws Exception{
        String sql = "UPDATE libros SET eliminado = ?, titulo = ?, autor = ?, editorial = ?, anioEdicion = ? WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
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
    public void eliminar(Long id, Connection conn) throws Exception{
    String sql ="UPDATE libros SET eliminado = true WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)){
    stmt.setLong(1, id);
    stmt.executeUpdate();
        }
    }
    
    
    public Libro mapearLibro(ResultSet rs) throws SQLException {
    Libro libro = new Libro();

    libro.setId(rs.getLong("id"));
    libro.setEliminado(rs.getBoolean("eliminado"));
    libro.setTitulo(rs.getString("titulo"));
    libro.setAutor(rs.getString("autor"));
    libro.setEditorial(rs.getString("editorial"));
    libro.setAnioEdicion(rs.getInt("anioEdicion"));

    return libro;
    }
    
}


