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
    public void crear(FichaBibliografica fb, Connection conn)throws Exception{
        String sql = "INSERT INTO fichas_libliograficas(isbn, clasificacion_dewey, estanteria, idioma, eliminado)VALUES (?, ?, ?, ?, false)";
        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, fb.getIsbn());
            stmt.setString(2, fb.getClasificacionDewey());
            stmt.setString(3, fb.getEstanteria());
            stmt.setString(4, fb.getIdioma());
            
            }
    }
    @Override
    public FichaBibliografica getById(long id, Connection conn) throws Exception{
        String sql = "SELECT * FROM fichas_libliograficas WHERE id = ? AND eliminado = false";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
        stmt.setLong(1,id);
        try (ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
        return mapearLibro(rs);
                }
            }
        }
        return null;
    }
    @Override
    public List<FichaBibliografica> getAll(Connection conn) throws Exception{
        String sql = "SELECT * FROM fichas_libliograficas WHERE eliminado = false";
        List<FichaBibliografica> fichaBibliografica = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                fichaBibliografica.add(mapearLibro(rs));
            }
        }
        return fichaBibliografica;
    }
    @Override 
    public void actualizar (FichaBibliografica fb, Connection conn) throws Exception{
        String sql = "UPDATE fichas_libliograficas SET eliminado = ?, isbn = ?, clasificacion_dewey = ?, estanteria = ?, idioma = ? WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
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
    public void eliminar(Long id, Connection conn) throws Exception{
    String sql ="UPDATE fichas_libliograficas SET eliminado = true WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)){
    stmt.setLong(1, id);
    stmt.executeUpdate();
        }
    }
    
    
    public FichaBibliografica mapearLibro(ResultSet rs) throws SQLException {
    FichaBibliografica fb = new FichaBibliografica();

    fb.setId(rs.getLong("id"));
    fb.setEliminado(rs.getBoolean("eliminado"));
    fb.setIsbn(rs.getString("isbn"));
    fb.setClasificacionDewey(rs.getString("clasificacionDewey"));
    fb.setEstanteria(rs.getString("estanteria"));
    fb.setIdioma(rs.getString("idioma"));

    return fb;
    }
    
}

