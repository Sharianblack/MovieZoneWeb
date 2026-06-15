<%@ page import="java.util.List" %>
<%@ page import="model.PeliculaGuardada" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>MovieZone - Mis Favoritos</title>
  <link rel="icon" href="img/logo.png" type="image/png">
  <link rel="stylesheet" href="css/favorito.css?v=3.0">
</head>
<body>

<nav class="navbar">
  <div class="navbar-inner">

    <div class="navbar-header">
      <a class="navbar-brand" href="index.jsp">MovieZone</a>
      <button class="hamburger-btn" onclick="toggleMenu()">☰</button>
    </div>

    <ul class="navbar-links" id="navLinks">
      <li><a href="index.jsp">Buscar</a></li>

      <%
        model.Usuario usuarioActivo = (model.Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioActivo != null) {
      %>
      <% if ("ADMIN".equals(usuarioActivo.getRol())) { %>
      <li><a class="link-admin" href="usuario?accion=panelAdmin">Panel Admin</a></li>
      <% } %>
      <li><span class="user-greeting">Qué más, <%= usuarioActivo.getNombreCompleto() %>!</span></li>
      <li><a class="btn-logout" href="usuario?accion=logout">Cerrar Sesión</a></li>
      <% } else { %>
      <li><a class="btn-login" href="login.jsp">Iniciar Sesión</a></li>
      <% } %>
    </ul>

  </div>
</nav>
<div class="container">

  <div class="favoritos-header">
    <div class="favoritos-header-text">
      <h1>Mis <span>Favoritos</span></h1>
      <p>Las películas que tienes guardadas en tu base de datos</p>
    </div>
    <a href="peliculas?accion=descargarPDF" class="btn-pdf">Descargar Reporte PDF</a>
  </div>

  <%
    List<PeliculaGuardada> favoritos = (List<PeliculaGuardada>) request.getAttribute("listaFavoritos");

    if (favoritos != null && !favoritos.isEmpty()) {
  %>

  <div class="favoritos-grid">
    <%
      for (PeliculaGuardada peli : favoritos) {
    %>
    <div class="card">
      <div class="card-poster">
        <img src="<%= peli.getPosterUrl() %>" alt="Poster de <%= peli.getTitulo() %>">
      </div>
      <div class="card-body">
        <p class="card-title"><%= peli.getTitulo() %></p>
        <p class="card-categoria"><%= peli.getCategoriaLocal() %></p>

        <div class="resena-box">
          <% if (peli.getCalificacionUsuario() > 0) { %>
          <p class="estrellas">
            <% for (int i = 0; i < peli.getCalificacionUsuario(); i++) out.print("⭐"); %>
          </p>
          <p class="comentario-texto">"<%= peli.getComentarioUsuario() %>"</p>
          <% } else { %>
          <button type="button" class="btn-calificar"
                  onclick="abrirModal(<%= peli.getIdExternoApi() %>, '<%= peli.getTitulo().replace("'", "\\'") %>')">
            Calificar
          </button>
          <% } %>
        </div>

        <form action="peliculas" method="POST" style="margin-top: auto;">
          <input type="hidden" name="accion" value="eliminarLocal">
          <input type="hidden" name="idApi" value="<%= peli.getIdExternoApi() %>">
          <button type="submit" class="btn-eliminar"
                  onclick="return confirm('¿Seguro que quieres borrarla?')">
            Eliminar
          </button>
        </form>
      </div>
    </div>
    <%
      }
    %>
  </div>

  <%
  } else {
  %>
  <div class="favoritos-vacio">
    <p>Aún no has guardado ninguna película.</p>
    <a href="index.jsp" class="btn-buscar">Ir a buscar películas</a>
  </div>
  <% } %>

</div>

<!-- MODAL RESEÑA -->
<div id="modalResena" class="modal-overlay">
  <div class="modal-content">

    <span class="close-modal" onclick="cerrarModal()">&times;</span>
    <h2>Calificar Película</h2>
    <p id="modalTitulo"></p>

    <form action="peliculas" method="POST" class="modal-form">
      <input type="hidden" name="accion" value="guardarResena">
      <input type="hidden" name="idApi" id="modalIdApi" value="">

      <label>Calificación (1 a 5)</label>
      <input type="number" name="calificacion" min="1" max="5" value="5" required>

      <label>Tu Opinión</label>
      <textarea name="comentario" rows="3" placeholder="¿Qué te pareció?" required></textarea>

      <button type="submit" class="btn-guardar-resena">Guardar Reseña</button>
    </form>

  </div>
</div>

<script>
  function abrirModal(idApi, titulo) {
    document.getElementById('modalIdApi').value = idApi;
    document.getElementById('modalTitulo').innerText = titulo;
    document.getElementById('modalResena').style.display = 'flex';
  }

  function cerrarModal() {
    document.getElementById('modalResena').style.display = 'none';
  }

  window.onclick = function(event) {
    var modal = document.getElementById('modalResena');
    if (event.target === modal) {
      modal.style.display = 'none';
    }
  }
</script>
<script>
  // Abrir y cerrar menú hamburguesa
  function toggleMenu() {
    var menu = document.getElementById("navLinks");
    menu.classList.toggle("active");
  }
</script>
</body>
</html>