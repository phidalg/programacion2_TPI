
package DAO;

import java.util.List;
import java.sql.Connection;


public interface GenericDAO<T> {
    void crear (T entidad, Connection DatabaseConnection) throws Exception;
    T getById (long id, Connection DatabaseConnection) throws Exception;
    List<T> getAll(Connection DatabaseConnection) throws Exception;
    void actualizar(T entidad, Connection DatabaseConnection) throws Exception;
    void eliminar (Long id, Connection DatabaseConnection) throws Exception;
}
