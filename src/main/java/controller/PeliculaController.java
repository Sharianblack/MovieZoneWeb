package controller;

import dao.PeliculaDAO;
import model.PeliculaGuardada;
import model.Usuario; // Importamos el modelo Usuario
import service.MovieApiService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importamos HttpSession
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PeliculaController", urlPatterns = {"/peliculas"})
public class PeliculaController extends HttpServlet {

    private PeliculaDAO peliculaDAO;

    @Override
    public void init() throws ServletException {
        peliculaDAO = new PeliculaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("buscar".equals(accion)) {
            String query = request.getParameter("query");
            service.MovieApiService apiService = new service.MovieApiService();
            List<model.PeliculaGuardada> peliculas = apiService.buscarPeliculas(query);

            request.setAttribute("listaPeliculas", peliculas);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } else if ("listarFavoritos".equals(accion)) {
            jakarta.servlet.http.HttpSession sesion = request.getSession();
            model.Usuario usuarioActivo = (model.Usuario) sesion.getAttribute("usuarioLogueado");

            if (usuarioActivo != null) {
                List<model.PeliculaGuardada> misFavoritos = peliculaDAO.listarPeliculasPorUsuario(usuarioActivo.getIdUsuario());
                request.setAttribute("listaFavoritos", misFavoritos);
                request.getRequestDispatcher("favoritos.jsp").forward(request, response);
            } else {
                response.sendRedirect("login.jsp");
            }

        }
        // =========================================================
        // NUEVO: GENERADOR DE REPORTE PDF (El Rompe-Notas)
        // =========================================================
        else if ("descargarPDF".equals(accion)) {
            jakarta.servlet.http.HttpSession sesion = request.getSession();
            model.Usuario usuarioActivo = (model.Usuario) sesion.getAttribute("usuarioLogueado");

            if (usuarioActivo != null) {
                // Traemos la lista de películas de la base de datos
                List<model.PeliculaGuardada> misFavoritos = peliculaDAO.listarPeliculasPorUsuario(usuarioActivo.getIdUsuario());

                // Le decimos al navegador que le vamos a mandar un archivo PDF descargable
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=Mis_Peliculas_MovieZone.pdf");

                try {
                    // Creamos el documento en blanco usando iText
                    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                    com.itextpdf.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());
                    document.open();

                    // Título del PDF
                    com.itextpdf.text.Font fontTitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD);
                    com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph("🎬 Reporte de Favoritos - MovieZone", fontTitulo);
                    titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(titulo);
                    document.add(new com.itextpdf.text.Paragraph("\n")); // Espacio

                    // Datos del Usuario
                    document.add(new com.itextpdf.text.Paragraph("Usuario: " + usuarioActivo.getNombreCompleto()));
                    document.add(new com.itextpdf.text.Paragraph("Correo: " + usuarioActivo.getCorreo()));
                    document.add(new com.itextpdf.text.Paragraph("Total de películas guardadas: " + misFavoritos.size()));
                    document.add(new com.itextpdf.text.Paragraph("\n")); // Espacio

                    // Creamos la tabla con 3 columnas
                    com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(3);
                    table.setWidthPercentage(100); // Que ocupe todo el ancho

                    // Encabezados de la tabla
                    table.addCell("ID TMDB");
                    table.addCell("Título de la Película");
                    table.addCell("Categoría Local");

                    // Llenamos la tabla con el bucle for
                    for (model.PeliculaGuardada p : misFavoritos) {
                        table.addCell(String.valueOf(p.getIdExternoApi()));
                        table.addCell(p.getTitulo());
                        table.addCell(p.getCategoriaLocal() != null ? p.getCategoriaLocal() : "Sin categoría");
                    }

                    // Metemos la tabla al documento y cerramos
                    document.add(table);
                    document.close();

                } catch (Exception e) {
                    System.out.println("Error al generar el PDF: " + e.getMessage());
                }
            } else {
                response.sendRedirect("login.jsp");
            }
        } else {
            response.getWriter().println("Bienvenido. Usa el formulario del index.jsp para buscar.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();
        Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuarioLogueado");

        // Si no hay sesión iniciada, patada de vuelta al login
        if (usuarioActivo == null) {
            response.sendRedirect("login.jsp");
            return;
        }

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

                // CAMBIO AQUÍ: Mandamos la película Y el id del usuario activo para crear el Favorito
                boolean exito = peliculaDAO.guardarPeliculaFavorita(nuevaPeli, usuarioActivo.getIdUsuario());

                if (exito) {
                    request.setAttribute("mensajeExito", "¡Película '" + titulo + "' añadida a favoritos! 🍿");
                } else {
                    request.setAttribute("mensajeError", "La película '" + titulo + "' ya está en tu lista. ⚠️");
                }

                request.getRequestDispatcher("index.jsp").forward(request, response);

            } catch (NumberFormatException e) {
                request.setAttribute("mensajeError", "Error procesando el ID de la película.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }

        } else if ("eliminarLocal".equals(accion)) {
            try {
                int idApi = Integer.parseInt(request.getParameter("idApi"));

                // CAMBIO AQUÍ: Eliminamos el favorito vinculando película y usuario
                peliculaDAO.eliminarFavoritoUsuario(idApi, usuarioActivo.getIdUsuario());

                response.sendRedirect("peliculas?accion=listarFavoritos");
            } catch (Exception e) {
                response.sendRedirect("peliculas?accion=listarFavoritos");
            }
        }
    }
}