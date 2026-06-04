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
                // Atrapamos los datos que nos envía el formulario (o el front-end)
                int idApi = Integer.parseInt(request.getParameter("idApi"));
                String titulo = request.getParameter("titulo");
                String categoria = request.getParameter("categoria");

                // Armamos el objeto modelo
                PeliculaGuardada nuevaPeli = new PeliculaGuardada();
                nuevaPeli.setIdExternoApi(idApi);
                nuevaPeli.setTitulo(titulo);
                nuevaPeli.setCategoriaLocal(categoria);

                // Usamos el DAO para guardarlo en PostgreSQL
                boolean exito = peliculaDAO.guardarPeliculaLocal(nuevaPeli);

                if (exito) {
                    response.getWriter().println("¡Pelicula '" + titulo + "' guardada con exito en la base de datos!");
                } else {
                    response.getWriter().println("Error al guardar la pelicula. Revisa si ya existe.");
                }
            } catch (NumberFormatException e) {
                response.getWriter().println("Error: El ID de la API debe ser un numero.");
            }
        } else {
            response.getWriter().println("Accion no soportada en POST.");
        }
    }
}