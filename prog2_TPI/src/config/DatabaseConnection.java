package config;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        Properties propers = new Properties();

        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config/db.properties")) {

            if (input == null) {
                throw new FileNotFoundException("No se encontró el archivo db.properties en el classpath.");
            }

            propers.load(input);

        } catch (FileNotFoundException e) {
            throw new SQLException("Error: archivo de configuración no encontrado. " +
                                   "Verificá que 'db.properties' esté en la carpeta resources.", e);

        } catch (IOException e) {
            throw new SQLException("Error al leer el archivo db.properties. " +
                                   "Posiblemente el archivo esté dañado o tenga formato incorrecto.", e);
        }

        String url = propers.getProperty("db.url");
        String user = propers.getProperty("db.user");
        String password = propers.getProperty("db.password");
        String driver = propers.getProperty("db.driver");


        if (url == null || user == null || driver == null) {
            throw new SQLException("Configuración incompleta: faltan propiedades obligatorias (url, user o driver).");
        }

        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC: " + driver + 
                                   ". Verificá que el conector esté agregado al proyecto.", e);

        } catch (SQLException e) {
            throw new SQLException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }
}

    
    
  

