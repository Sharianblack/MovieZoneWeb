package filter;

import model.Usuario;
import java.io.IOException;

// ¡AQUÍ ESTÁ LA MAGIA! Todo con Jakarta
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// El "/*" significa que este filtro va a vigilar ABSOLUTAMENTE TODAS las URLs del proyecto
@WebFilter("/*")
public class FiltroSeguridad implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Se ejecuta una sola vez al arrancar el servidor
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // 1. Obtener la ruta que el usuario está intentando abrir
        String requestURI = httpRequest.getRequestURI();

        // 2. Definir qué cosas son PÚBLICAS (cualquiera puede verlas sin loguearse)
        boolean esPaginaLogin = requestURI.endsWith("login.jsp") || requestURI.endsWith("registro.jsp");
        boolean esServletUsuario = requestURI.contains("usuario"); // Para el login/registro
        boolean esRecursoEstatico = requestURI.contains("/css/") || requestURI.contains("/img/") || requestURI.contains("/js/");
        boolean esIndex = requestURI.endsWith("index.jsp") || requestURI.endsWith(httpRequest.getContextPath() + "/");

        // Extraer el usuario de la sesión si existe
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogueado") : null;
        boolean estaLogueado = (usuario != null);

        // 3. CONTROL DE ACCESO GENERAL: Si no está logueado y busca algo privado, va para el login
        if (!estaLogueado && !esPaginaLogin && !esServletUsuario && !esRecursoEstatico && !esIndex) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return; // Corta la petición aquí, no lo deja avanzar
        }

        // 4. CONTROL DE ACCESO ADMINISTRADOR: Si intenta entrar a zonas admin sin el rol
        if (requestURI.contains("admin.jsp") || requestURI.contains("panelAdmin")) {
            if (!estaLogueado || !"ADMIN".equals(usuario.getRol())) {
                // Si no es admin, lo rebotamos al inicio
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
                return;
            }
        }

        // Si pasó todas las reglas del guardián, lo dejamos continuar a su destino
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Se ejecuta al apagar el servidor
    }
}