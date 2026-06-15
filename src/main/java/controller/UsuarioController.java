package controller;

import dao.UsuarioDAO;
import model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
// Importamos esto para generar el token loco
import java.util.UUID;

@WebServlet(name = "UsuarioController", urlPatterns = {"/usuario"})
public class UsuarioController extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // ==========================================
        // 1. ZONA DE REGISTRO
        // ==========================================
        // ==========================================
        // 1. ZONA DE REGISTRO
        // ==========================================
        if ("registrar".equals(accion)) {
            String nombreCompleto = request.getParameter("nombre_completo");
            String correo = request.getParameter("correo");
            String password = request.getParameter("password");

            // Validación de seguridad de la contraseña
            if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
                request.setAttribute("mensajeError", "La contraseña es muy débil, mijo. Sigue las instrucciones.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return;
            }

            // ====================================================
            // ¡AQUÍ ENCRIPTAMOS LA CONTRASEÑA ANTES DE GUARDARLA!
            // ====================================================
            String passwordEncriptada = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

            // 1. Generamos el código único e irrepetible para el correo
            String tokenGenerado = UUID.randomUUID().toString();

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setCorreo(correo);
            // Le pasamos la clave encriptada, ya no la normal
            nuevoUsuario.setPassword(passwordEncriptada);

            // 2. Le pasamos el usuario (ya encriptado) y el token al DAO
            boolean exito = usuarioDAO.registrarUsuario(nuevoUsuario, tokenGenerado);

            if (exito) {
                // 3. ¡DISPARAMOS EL CORREO DE VERIFICACIÓN!
                service.CorreoService.enviarCorreoVerificacion(nuevoUsuario.getCorreo(), nuevoUsuario.getNombreCompleto(), tokenGenerado);

                request.setAttribute("mensajeExito", "¡Registro casi listo! Revisa tu correo (y la carpeta Spam) para verificar tu cuenta antes de entrar.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                request.setAttribute("mensajeError", "Error al registrar. Revisa que el correo no esté repetido.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
            }
        }
        // ==========================================
        // 2. ZONA DE LOGIN
        // ==========================================
        else if ("login".equals(accion)) {
            String correo = request.getParameter("correo");
            String password = request.getParameter("password");

            Usuario usuario = usuarioDAO.validarLogin(correo, password);

            if (usuario != null) {
                // ¡SESIÓN EXITOSA!
                jakarta.servlet.http.HttpSession sesion = request.getSession();
                sesion.setAttribute("usuarioLogueado", usuario);
                response.sendRedirect("index.jsp");
            } else {
                // LOGIN FALLIDO (Puede ser por mala clave o porque no ha verificado el correo)
                request.setAttribute("mensajeError", "Datos incorrectos o cuenta no verificada. Pilas ahí.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
        // ==========================================
        // 3. ZONA DE ELIMINAR USUARIO (Panel Admin)
        // ==========================================
        else if ("eliminarUsuario".equals(accion)) {
            int idEliminar = Integer.parseInt(request.getParameter("idUsuario"));
            usuarioDAO.eliminarUsuario(idEliminar);
            response.sendRedirect("usuario?accion=panelAdmin");
        }
    }

    // ==========================================
    // 4. PETICIONES GET (Logout, Admin y Verificación)
    // ==========================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // ------------------------------------------
        // A. CERRAR SESIÓN
        // ------------------------------------------
        if ("logout".equals(accion)) {
            jakarta.servlet.http.HttpSession sesion = request.getSession();
            sesion.invalidate();
            response.sendRedirect("login.jsp");
        }
        // ------------------------------------------
        // B. PANEL DE ADMINISTRADOR
        // ------------------------------------------
        else if ("panelAdmin".equals(accion)) {
            jakarta.servlet.http.HttpSession sesion = request.getSession();
            Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuarioLogueado");

            if (usuarioActivo != null && "ADMIN".equals(usuarioActivo.getRol())) {
                java.util.List<Usuario> listaUsuarios = usuarioDAO.listarUsuariosParaAdmin();
                request.setAttribute("listaUsuarios", listaUsuarios);
                request.getRequestDispatcher("admin.jsp").forward(request, response);
            } else {
                response.sendRedirect("index.jsp");
            }
        }
        // ------------------------------------------
        // C. ¡NUEVO! VERIFICAR CUENTA DESDE EL CORREO
        // ------------------------------------------
        else if ("verificar".equals(accion)) {
            // Atrapamos el token que viene en la URL del correo
            String token = request.getParameter("token");

            // Verificamos en la base de datos
            if (usuarioDAO.verificarCuenta(token)) {
                request.setAttribute("mensajeExito", "¡De lujo! Tu cuenta ha sido verificada. Ya puedes iniciar sesión.");
            } else {
                request.setAttribute("mensajeError", "El enlace es inválido o la cuenta ya fue verificada.");
            }
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}