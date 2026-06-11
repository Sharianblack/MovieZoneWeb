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
            // NUEVO: Atrapamos si eligió "movie" o "tv"
            String tipoBusqueda = request.getParameter("tipoBusqueda");

            // Por si acaso llega nulo, le ponemos película por defecto
            if (tipoBusqueda == null) tipoBusqueda = "movie";

            service.MovieApiService apiService = new service.MovieApiService();
            // Le pasamos el tipo de búsqueda también al método
            List<model.PeliculaGuardada> peliculas = apiService.buscarPeliculas(query, tipoBusqueda);

            request.setAttribute("listaPeliculas", peliculas);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        }else if ("listarFavoritos".equals(accion)) {
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



                    // 1. Título con el color ROJO de tu diseño (RGB: 229, 57, 53)
                    com.itextpdf.text.Font fontTitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 24, com.itextpdf.text.Font.BOLD, new com.itextpdf.text.BaseColor(229, 57, 53));
                    com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph("🎬 Reporte de Favoritos", fontTitulo);
                    titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(titulo);

                    // ==========================================
                    // NUEVO: INSERTAR LOGO REAL EN EL PDF
                    // ==========================================
                    try {
                        // Le pedimos al Tomcat la ruta física exacta de la imagen
                        String rutaLogo = getServletContext().getRealPath("/img/logo2.png");
                        com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(rutaLogo);

                        // Ajustamos el tamaño para que no ocupe toda la hoja (ej: 60x60 píxeles)
                        logo.scaleToFit(60, 60);
                        logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);

                        // Lo agregamos al documento
                        document.add(logo);
                    } catch (Exception e) {
                        System.out.println("Nota: No se pudo cargar el logo en el PDF - " + e.getMessage());
                    }

                    // Subtítulo con el nombre de la app
                    com.itextpdf.text.Font fontSub = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.ITALIC, com.itextpdf.text.BaseColor.DARK_GRAY);
                    com.itextpdf.text.Paragraph subTitulo = new com.itextpdf.text.Paragraph("MovieZone - Tu colección personal\n\n", fontSub);
                    subTitulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(subTitulo);

                    // 2. Datos del Usuario
                    document.add(new com.itextpdf.text.Paragraph("👤 Usuario: " + usuarioActivo.getNombreCompleto()));
                    document.add(new com.itextpdf.text.Paragraph("✉️ Correo: " + usuarioActivo.getCorreo()));
                    document.add(new com.itextpdf.text.Paragraph("📊 Total guardadas: " + misFavoritos.size() + " títulos\n\n"));

                    // 3. Creamos la tabla y sus tamaños
                    com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{3f, 2f, 1.5f, 3.5f});

                    // 4. ESTILO PARA LA CABECERA (Fondo Rojo Oscuro, Letras Blancas)
                    com.itextpdf.text.Font fontCabecera = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
                    com.itextpdf.text.BaseColor colorFondoCabecera = new com.itextpdf.text.BaseColor(183, 28, 28); // Tu var(--red-dark)

                    String[] cabeceras = {"Título", "Categoría", "Calificación", "Tu Opinión"};
                    for (String cabecera : cabeceras) {
                        com.itextpdf.text.pdf.PdfPCell celda = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(cabecera, fontCabecera));
                        celda.setBackgroundColor(colorFondoCabecera);
                        celda.setPadding(8f);
                        celda.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        table.addCell(celda);
                    }

                    // 5. Llenamos los datos
                    com.itextpdf.text.Font fontDatos = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.BLACK);

                    for (model.PeliculaGuardada p : misFavoritos) {
                        table.addCell(new com.itextpdf.text.Phrase(p.getTitulo(), fontDatos));
                        table.addCell(new com.itextpdf.text.Phrase(p.getCategoriaLocal() != null ? p.getCategoriaLocal() : "General", fontDatos));

                        if (p.getCalificacionUsuario() > 0) {
                            // Centramos la calificación
                            com.itextpdf.text.pdf.PdfPCell celdaNota = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(p.getCalificacionUsuario() + " / 5", fontDatos));
                            celdaNota.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                            table.addCell(celdaNota);

                            table.addCell(new com.itextpdf.text.Phrase(p.getComentarioUsuario(), fontDatos));
                        } else {
                            com.itextpdf.text.pdf.PdfPCell celdaNota = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Sin calificar", fontDatos));
                            celdaNota.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                            table.addCell(celdaNota);

                            table.addCell(new com.itextpdf.text.Phrase("-", fontDatos));
                        }
                    }

                    document.add(table);

                    // 6. Footer con la fecha y hora exacta
                    com.itextpdf.text.Font fontFooter = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC, com.itextpdf.text.BaseColor.GRAY);
                    com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph("\n\nReporte generado automáticamente por MovieZone el: " + new java.util.Date().toString(), fontFooter);
                    footer.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                    document.add(footer);

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
        // ==========================================
        // NUEVO: GUARDAR RESEÑA DESDE EL MODAL
        // ==========================================
        else if ("guardarResena".equals(accion)) {
            try {
                int idApi = Integer.parseInt(request.getParameter("idApi"));
                int calificacion = Integer.parseInt(request.getParameter("calificacion"));
                String comentario = request.getParameter("comentario");

                // Mandamos a guardar la reseña en la base de datos
                peliculaDAO.guardarResena(usuarioActivo.getIdUsuario(), idApi, calificacion, comentario);

                // Recargamos la página de favoritos para que aparezcan las estrellitas de una
                response.sendRedirect("peliculas?accion=listarFavoritos");

            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("peliculas?accion=listarFavoritos");
            }
        }
    }
}