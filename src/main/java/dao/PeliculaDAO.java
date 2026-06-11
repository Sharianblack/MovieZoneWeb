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
    // ========================================================
    // 1. GUARDAR EN PELICULAS_GUARDADAS Y LUEGO EN FAVORITOS
    // ========================================================
    public boolean guardarPeliculaFavorita(PeliculaGuardada peli, int idUsuario) {
        Connection con = null;
        PreparedStatement psPeli = null;
        PreparedStatement psFav = null;
        PreparedStatement psCheck = null;
        ResultSet rs = null;

        try {
            con = dao.ConexionDB.obtenerConexion();
            // Desactivamos auto-commit para manejarlo como una sola transacción segura
            con.setAutoCommit(false);

            // Paso A: Insertar la película si no existe usando una consulta limpia
            String sqlPeli = "INSERT INTO peliculas_guardadas (id_externo_api, titulo, categoria_local, poster_url) " +
                    "VALUES (?, ?, ?, ?) ON CONFLICT (id_externo_api) DO NOTHING";
            psPeli = con.prepareStatement(sqlPeli);
            psPeli.setInt(1, peli.getIdExternoApi());
            psPeli.setString(2, peli.getTitulo());
            psPeli.setString(3, peli.getCategoriaLocal());
            psPeli.setString(4, peli.getPosterUrl());
            psPeli.executeUpdate();

            // Paso B: Conseguir el id_pelicula generado por la base
            String sqlGetId = "SELECT id_pelicula FROM peliculas_guardadas WHERE id_externo_api = ?";
            psCheck = con.prepareStatement(sqlGetId);
            psCheck.setInt(1, peli.getIdExternoApi());
            rs = psCheck.executeQuery();

            int idPeliculaBase = 0;
            if (rs.next()) {
                idPeliculaBase = rs.getInt("id_pelicula");
            }

            // Paso C: Verificar si este usuario ya tiene agregada esta película en favoritos
            String sqlCheckFav = "SELECT 1 FROM favoritos WHERE id_usuario = ? AND id_pelicula = ?";
            try (PreparedStatement psCF = con.prepareStatement(sqlCheckFav)) {
                psCF.setInt(1, idUsuario);
                psCF.setInt(2, idPeliculaBase);
                try (ResultSet rsCF = psCF.executeQuery()) {
                    if (rsCF.next()) {
                        con.rollback(); // Ya existe, cancelamos todo de forma segura
                        return false;
                    }
                }
            }

            // Paso D: Insertar en la tabla intermedia favoritos
            String sqlFav = "INSERT INTO favoritos (id_usuario, id_pelicula) VALUES (?, ?)";
            psFav = con.prepareStatement(sqlFav);
            psFav.setInt(1, idUsuario);
            psFav.setInt(2, idPeliculaBase);

            int filasAfectadas = psFav.executeUpdate();
            con.commit(); // Confirmamos los cambios en PostgreSQL
            return filasAfectadas > 0;

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Cerramos todos los flujos abiertos
            try {
                if (rs != null) rs.close();
                if (psPeli != null) psPeli.close();
                if (psFav != null) psFav.close();
                if (psCheck != null) psCheck.close();
                if (con != null) con.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // ========================================================
    // 2. LISTAR PELÍCULAS FILTRADAS POR EL ID DEL USUARIO (INNER JOIN)
    // ========================================================
    public List<PeliculaGuardada> listarPeliculasPorUsuario(int idUsuario) {
        List<PeliculaGuardada> lista = new java.util.ArrayList<>();
        String sql = "SELECT p.* FROM peliculas_guardadas p " +
                "INNER JOIN favoritos f ON p.id_pelicula = f.id_pelicula " +
                "WHERE f.id_usuario = ?";

        try (Connection con = dao.ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PeliculaGuardada p = new PeliculaGuardada();
                    p.setIdExternoApi(rs.getInt("id_externo_api"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setCategoriaLocal(rs.getString("categoria_local"));
                    p.setPosterUrl(rs.getString("poster_url"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ========================================================
    // 3. ELIMINAR SOLAMENTE EL FAVORITO DE ESE USUARIO
    // ========================================================
    public boolean eliminarFavoritoUsuario(int idExternoApi, int idUsuario) {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_pelicula = " +
                "(SELECT id_pelicula FROM peliculas_guardadas WHERE id_externo_api = ?)";

        try (Connection con = dao.ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idExternoApi);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
