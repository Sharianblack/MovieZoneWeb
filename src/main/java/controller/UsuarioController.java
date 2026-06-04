package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UsuarioController", urlPatterns = {"/usuarios"})
public class UsuarioController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("registrar".equals(accion)) {
            String nombreCompleto = request.getParameter("nombre_completo");
            String correo = request.getParameter("correo");
            String contrasena = request.getParameter("contrasena");

            // NOTA: Aquí a futuro llamaríamos a un UsuarioDAO para hacer el INSERT en tu tabla 'usuarios'
            response.getWriter().println("Registrando al usuario: " + nombreCompleto);

        } else if ("login".equals(accion)) {
            String correo = request.getParameter("correo");
            String contrasena = request.getParameter("contrasena");

            // Validar credenciales
            response.getWriter().println("Iniciando sesion para: " + correo);
        } else {
            response.getWriter().println("Accion de usuario no valida.");
        }
    }
}