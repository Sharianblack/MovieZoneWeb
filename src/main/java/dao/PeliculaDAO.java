package dao;

import model.PeliculaGuardada;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PeliculaDAO {

    // Método para insertar una película en la base de datos
    public boolean guardarPeliculaLocal(PeliculaGuardada pelicula) {
        // La consulta SQL con signos de interrogación para evitar inyección SQL
        String sql = "INSERT INTO peliculas_guardadas (id_externo_api, titulo, categoria_local) VALUES (?, ?, ?)";

        // El try-with-resources cierra la conexión automáticamente al terminar
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Reemplazamos los '?' con los datos del modelo
            ps.setInt(1, pelicula.getIdExternoApi());
            ps.setString(2, pelicula.getTitulo());
            ps.setString(3, pelicula.getCategoriaLocal());

            // Ejecutamos la actualización
            int filasAfectadas = ps.executeUpdate();

            // Si filasAfectadas es mayor a 0, significa que sí se guardó
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al guardar la pelicula en la base de datos.");
            e.printStackTrace();
            return false;
        }
    }
}