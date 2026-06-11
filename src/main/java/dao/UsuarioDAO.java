package dao;

import model.Usuario;
import org.mindrot.jbcrypt.BCrypt; // Importamos la nueva librería
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // ==========================================
    // REGISTRAR USUARIO CON CONTRASEÑA ENCRIPTADA
    // ==========================================
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_completo, correo, contrasena) VALUES (?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombreCompleto());
            ps.setString(2, usuario.getCorreo());

            // ¡AQUÍ ESTÁ LA MAGIA DEL HASHEO!
            String claveHasheada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
            ps.setString(3, claveHasheada);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar usuario.");
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // LOGIN COMPARANDO EL HASH
    // ==========================================
    // ==========================================
    // LOGIN COMPARANDO EL HASH
    // ==========================================
    public Usuario validarLogin(String correo, String passwordIngresado) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        Usuario usuario = null;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashEnBaseDatos = rs.getString("contrasena");

                    if (BCrypt.checkpw(passwordIngresado, hashEnBaseDatos)) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setNombreCompleto(rs.getString("nombre_completo"));
                        usuario.setCorreo(rs.getString("correo"));

                        // NUEVO: Leemos el rol desde la base de datos
                        usuario.setRol(rs.getString("rol"));

                        usuario.setPassword("");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar login.");
            e.printStackTrace();
        }
        return usuario;
    }

    // ==========================================
    // LISTAR USUARIOS PARA EL PANEL DE ADMIN
    // ==========================================
    public java.util.List<Usuario> listarUsuariosParaAdmin() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        // Hacemos un LEFT JOIN para contar cuántas películas tiene cada uno en 'favoritos'
        String sql = "SELECT u.id_usuario, u.nombre_completo, u.correo, u.rol, COUNT(f.id_favorito) AS total_peliculas " +
                "FROM usuarios u " +
                "LEFT JOIN favoritos f ON u.id_usuario = f.id_usuario " +
                "GROUP BY u.id_usuario " +
                "ORDER BY u.id_usuario ASC";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(rs.getString("rol"));
                u.setTotalPeliculas(rs.getInt("total_peliculas")); // El conteo mágico
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios.");
            e.printStackTrace();
        }
        return lista;
    }

    // ==========================================
    // ELIMINAR USUARIO DESDE EL PANEL
    // ==========================================
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario.");
            e.printStackTrace();
            return false;
        }
    }
}