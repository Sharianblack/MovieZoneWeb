package dao;

import model.PeliculaGuardada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAO {

    /*
     * =====================================================
     * GUARDAR PELÍCULA EN LA TABLA peliculas_guardadas
     * =====================================================
     */
    public boolean guardarPeliculaLocal(PeliculaGuardada pelicula) {

        String sql = "INSERT INTO peliculas_guardadas "
                + "(id_externo_api, titulo, categoria_local, poster_url) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // ID que viene desde TMDB
            ps.setInt(1, pelicula.getIdExternoApi());

            // Título de la película
            ps.setString(2, pelicula.getTitulo());

            // Categoría local definida por el sistema
            ps.setString(3, pelicula.getCategoriaLocal());

            // URL del póster de la película
            ps.setString(4, pelicula.getPosterUrl());

            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al guardar la película.");
            e.printStackTrace();
            return false;
        }
    }
    /*
     * =====================================================
     * ELIMINAR PELÍCULA DE FAVORITOS
     * =====================================================
     */
    public boolean eliminarPeliculaLocal(int idApi) {
        String sql = "DELETE FROM peliculas_guardadas WHERE id_externo_api = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idApi);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar la película.");
            e.printStackTrace();
            return false;
        }
    }

    /*
     * =====================================================
     * LISTAR TODAS LAS PELÍCULAS GUARDADAS
     * =====================================================
     */
    public List<PeliculaGuardada> listarPeliculasGuardadas() {

        List<PeliculaGuardada> lista = new ArrayList<>();

        String sql = "SELECT * FROM peliculas_guardadas";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                PeliculaGuardada peli = new PeliculaGuardada();

                // ID interno de PostgreSQL
                peli.setIdPelicula(
                        rs.getInt("id_pelicula")
                );

                // ID original de TMDB
                peli.setIdExternoApi(
                        rs.getInt("id_externo_api")
                );

                // Título
                peli.setTitulo(
                        rs.getString("titulo")
                );

                // Categoría local
                peli.setCategoriaLocal(
                        rs.getString("categoria_local")
                );

                // Leemos la URL real desde PostgreSQL
                peli.setPosterUrl(
                        rs.getString("poster_url")
                );

                lista.add(peli);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar películas.");
            e.printStackTrace();
        }

        return lista;
    }


}
