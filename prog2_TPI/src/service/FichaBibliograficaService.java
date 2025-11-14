package service;

import config.DatabaseConnection;
import DAO.FichaBibliograficaDAO;
import edu.utn.entities.FichaBibliografica;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FichaBibliograficaService implements GenericService<FichaBibliografica> {
    
    private FichaBibliograficaDAO fichaDAO;
    
    public FichaBibliograficaService() {
        this.fichaDAO = new FichaBibliograficaDAO();
    }
    
    @Override
    public void insertar(FichaBibliografica ficha) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // Validación
            if (ficha.getIsbn() == null || ficha.getIsbn().trim().isEmpty()) {
                throw new IllegalArgumentException("El ISBN es obligatorio");
            }
            
            // Verificar duplicado
            FichaBibliografica existente = fichaDAO.buscarPorIsbn(ficha.getIsbn(), conn);
            if (existente != null && !existente.isEliminado()) {
                throw new IllegalArgumentException("Ya existe una ficha con ISBN: " + ficha.getIsbn());
            }
            
            fichaDAO.crear(ficha, conn);
        } catch (Exception e) {
            throw new SQLException("Error al insertar ficha: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public void actualizar(FichaBibliografica ficha) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            fichaDAO.actualizar(ficha, conn);
        } catch (Exception e) {
            throw new SQLException("Error al actualizar ficha: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public void eliminar(long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            fichaDAO.eliminar(id, conn);
        } catch (Exception e) {
            throw new SQLException("Error al eliminar ficha: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public FichaBibliografica getById(long id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return fichaDAO.getById(id, conn);
        } catch (Exception e) {
            throw new SQLException("Error al buscar ficha: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    @Override
    public List<FichaBibliografica> getAll() throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return fichaDAO.getAll(conn);
        } catch (Exception e) {
            throw new SQLException("Error al listar fichas: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    public FichaBibliografica buscarPorIsbn(String isbn) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return fichaDAO.buscarPorIsbn(isbn, conn);
        } catch (Exception e) {
            throw new SQLException("Error al buscar por ISBN: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.close();
        }
    }
}