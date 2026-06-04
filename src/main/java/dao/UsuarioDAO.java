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
    public Usuario validarLogin(String correo, String passwordIngresado) {
        // OJO: Solo buscamos por correo, ya no por contraseña
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        Usuario usuario = null;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Sacamos el garabato guardado en la base de datos
                    String hashEnBaseDatos = rs.getString("contrasena");

                    // BCrypt compara la clave que puso en el formulario con el hash de la base
                    if (BCrypt.checkpw(passwordIngresado, hashEnBaseDatos)) {

                        // Si coinciden, armamos el objeto usuario para que inicie sesión
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setNombreCompleto(rs.getString("nombre_completo"));
                        usuario.setCorreo(rs.getString("correo"));

                        // Opcional: no es necesario guardar el hash en la sesión por seguridad,
                        // pero lo dejamos vacío para que no viaje en memoria.
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
}