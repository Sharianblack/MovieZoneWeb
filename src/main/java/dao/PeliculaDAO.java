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
    // ========================================================
    // LISTAR PELÍCULAS CON SUS RESEÑAS (LEFT JOIN)
    // ========================================================
    public List<PeliculaGuardada> listarPeliculasPorUsuario(int idUsuario) {
        List<PeliculaGuardada> lista = new java.util.ArrayList<>();
        // Traemos la peli y, si existe, la reseña que este usuario específico le puso
        String sql = "SELECT p.*, r.calificacion, r.comentario FROM peliculas_guardadas p " +
                "INNER JOIN favoritos f ON p.id_pelicula = f.id_pelicula " +
                "LEFT JOIN resenas r ON p.id_pelicula = r.id_pelicula AND r.id_usuario = ? " +
                "WHERE f.id_usuario = ?";

        try (Connection con = dao.ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PeliculaGuardada p = new PeliculaGuardada();
                    p.setIdPelicula(rs.getInt("id_pelicula")); // Asegúrate de tener este setter para el id interno
                    p.setIdExternoApi(rs.getInt("id_externo_api"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setCategoriaLocal(rs.getString("categoria_local"));
                    p.setPosterUrl(rs.getString("poster_url"));

                    // Llenamos los campos de la reseña (si no tiene, llegarán como 0 y null)
                    p.setCalificacionUsuario(rs.getInt("calificacion"));
                    p.setComentarioUsuario(rs.getString("comentario"));

                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ========================================================
    // GUARDAR O ACTUALIZAR UNA RESEÑA
    // ========================================================
    public boolean guardarResena(int idUsuario, int idExternoApi, int calificacion, String comentario) {
        // Primero buscamos el id_pelicula interno usando el id de la API
        String sqlGetId = "SELECT id_pelicula FROM peliculas_guardadas WHERE id_externo_api = ?";
        String sqlInsert = "INSERT INTO resenas (id_usuario, id_pelicula, calificacion, comentario) VALUES (?, ?, ?, ?)";

        try (Connection con = dao.ConexionDB.obtenerConexion();
             PreparedStatement psId = con.prepareStatement(sqlGetId)) {

            psId.setInt(1, idExternoApi);
            try (ResultSet rs = psId.executeQuery()) {
                if (rs.next()) {
                    int idPeliculaBase = rs.getInt("id_pelicula");

                    // Insertamos la reseña limpia
                    try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                        psInsert.setInt(1, idUsuario);
                        psInsert.setInt(2, idPeliculaBase);
                        psInsert.setInt(3, calificacion);
                        psInsert.setString(4, comentario);

                        return psInsert.executeUpdate() > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
