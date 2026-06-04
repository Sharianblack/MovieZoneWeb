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
        if ("registrar".equals(accion)) {
            String nombreCompleto = request.getParameter("nombre_completo");
            String correo = request.getParameter("correo");
            String password = request.getParameter("password");

            // NUEVO: Validación de seguridad de la contraseña en el servidor
            // Verifica que tenga al menos 8 caracteres, una mayúscula, una minúscula y un número
            if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
                request.setAttribute("mensajeError", "La contraseña es muy débil, mijo. Sigue las instrucciones.");
                request.getRequestDispatcher("registro.jsp").forward(request, response);
                return; // Cortamos la ejecución aquí para que no guarde nada
            }

            // Si pasa la prueba de seguridad, armamos el usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setCorreo(correo);
            nuevoUsuario.setPassword(password);

            boolean exito = usuarioDAO.registrarUsuario(nuevoUsuario);

            if (exito) {
                service.CorreoService.enviarCorreoBienvenida(nuevoUsuario.getCorreo(), nuevoUsuario.getNombreCompleto());
                request.setAttribute("mensajeExito", "Registro exitoso. ¡Revisa tu correo y luego inicia sesión!");
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

            // Validamos con tu DAO que ya tiene BCrypt
            Usuario usuario = usuarioDAO.validarLogin(correo, password);

            if (usuario != null) {
                // ¡SESIÓN EXITOSA!
                jakarta.servlet.http.HttpSession sesion = request.getSession();
                sesion.setAttribute("usuarioLogueado", usuario);

                // MAGIA AQUÍ: Esto es lo que te manda de regreso a la página principal
                response.sendRedirect("index.jsp");
            } else {
                // LOGIN FALLIDO
                request.setAttribute("mensajeError", "Correo o contraseña incorrectos. Pilas ahí.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
    }

    // ==========================================
    // 3. ZONA DE LOGOUT (Peticiones GET)
    // ==========================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("logout".equals(accion)) {
            // Agarramos la sesión actual y la matamos
            jakarta.servlet.http.HttpSession sesion = request.getSession();
            sesion.invalidate();

            // Lo mandamos de regreso al login
            response.sendRedirect("login.jsp");
        }
    }
}