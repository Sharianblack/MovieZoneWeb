package controller;

import dao.PeliculaDAO;
import model.PeliculaGuardada;
import service.MovieApiService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List; // Importante agregar este import

@WebServlet(name = "PeliculaController", urlPatterns = {"/peliculas"})
public class PeliculaController extends HttpServlet {

    private PeliculaDAO peliculaDAO;

    @Override
    public void init() throws ServletException {
        // Inicializamos el DAO una sola vez cuando arranca el Servlet
        peliculaDAO = new PeliculaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("buscar".equals(accion)) {

            String query = request.getParameter("query");

            // Buscar películas en TMDB
            MovieApiService apiService = new MovieApiService();
            List<PeliculaGuardada> peliculas = apiService.buscarPeliculas(query);

            // Enviar resultados al index.jsp
            request.setAttribute("listaPeliculas", peliculas);
            request.getRequestDispatcher("index.jsp")
                    .forward(request, response);

        } else if ("listarFavoritos".equals(accion)) {

            // Consultar películas guardadas en PostgreSQL
            List<PeliculaGuardada> misFavoritos =
                    peliculaDAO.listarPeliculasGuardadas();

            // Enviar la lista al JSP
            request.setAttribute("listaFavoritos", misFavoritos);

            // Mostrar favoritos.jsp
            request.getRequestDispatcher("favoritos.jsp")
                    .forward(request, response);

        } else {

            response.getWriter().println(
                    "Bienvenido. Usa el formulario del index.jsp para buscar."
            );
        }

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("guardarLocal".equals(accion)) {
            try {
                int idApi = Integer.parseInt(request.getParameter("idApi"));
                String titulo = request.getParameter("titulo");
                String categoria = request.getParameter("categoria");
                String posterUrl = request.getParameter("posterUrl");

                PeliculaGuardada nuevaPeli = new PeliculaGuardada();
                nuevaPeli.setIdExternoApi(idApi);
                nuevaPeli.setTitulo(titulo);
                nuevaPeli.setCategoriaLocal(categoria);
                nuevaPeli.setPosterUrl(posterUrl);

                boolean exito = peliculaDAO.guardarPeliculaLocal(nuevaPeli);

                // EN LUGAR DE IMPRIMIR TEXTO FEO, MANDAMOS UN MENSAJE A LA VISTA
                if (exito) {
                    request.setAttribute("mensajeExito", "¡Película '" + titulo + "' añadida a favoritos! 🍿");
                } else {
                    request.setAttribute("mensajeError", "La película '" + titulo + "' ya está en tu lista. ⚠️");
                }

                // Redirigimos de vuelta a la página principal sin perder el estilo
                request.getRequestDispatcher("index.jsp").forward(request, response);

            } catch (NumberFormatException e) {
                request.setAttribute("mensajeError", "Error procesando el ID de la película.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }

        } else if ("eliminarLocal".equals(accion)) {
            // Lógica para el nuevo botón de eliminar
            try {
                int idApi = Integer.parseInt(request.getParameter("idApi"));
                peliculaDAO.eliminarPeliculaLocal(idApi);

                // Refrescamos la lista de favoritos de una
                response.sendRedirect("peliculas?accion=listarFavoritos");
            } catch (Exception e) {
                response.sendRedirect("peliculas?accion=listarFavoritos");
            }
        }
    }
}