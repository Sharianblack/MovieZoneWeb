package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Aquí pones los datos de tu pgAdmin.
    // ¡Ojo! Cambia 'tu_contrasena_aqui' por la clave real que le pusiste al usuario.
    private static final String URL = "jdbc:postgresql://localhost:5432/moviezone_db";
    private static final String USUARIO = "moviezone_user";
    private static final String CONTRASENA = "12345";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            // 1. Cargar el driver de PostgreSQL que pusimos en el pom.xml
            Class.forName("org.postgresql.Driver");

            // 2. Intentar establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("¡Conexion a PostgreSQL exitosa, mijo!");

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