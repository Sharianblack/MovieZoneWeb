package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Tus credenciales locales de siempre (para cuando corres en tu compu)
    private static final String URL_LOCAL = "jdbc:postgresql://localhost:5432/moviezone_db";
    private static final String USUARIO_LOCAL = "moviezone_user";
    private static final String CONTRASENA_LOCAL = "12345";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            // 1. Cargar el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");

            // 2. ¡LA MAGIA PARA LA NUBE! Buscamos si la plataforma (Railway/Render) nos dio una URL
            String dbUrlNube = System.getenv("DATABASE_URL");

            if (dbUrlNube != null && !dbUrlNube.isEmpty()) {
                // Si la variable existe, significa que el proyecto ya está en internet
                conexion = DriverManager.getConnection(dbUrlNube);
                System.out.println("¡Conectado a la base de datos en la NUBE, coronamos!");
            } else {
                // Si no existe, asume que estás en tu laptop y usa el pgAdmin local
                conexion = DriverManager.getConnection(URL_LOCAL, USUARIO_LOCAL, CONTRASENA_LOCAL);
                System.out.println("¡Conexion a PostgreSQL LOCAL exitosa, mijo!");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de PostgreSQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error de SQL al intentar conectar.");
            e.printStackTrace();
        }
        return conexion;
    }
}