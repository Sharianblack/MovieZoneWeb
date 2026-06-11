<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.PeliculaGuardada" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>MovieZone - Mis Favoritos</title>
  <link rel="stylesheet" href="css/favorito.css">
</head>
<body>

<nav class="navbar">
  <div class="navbar-inner">
    <a class="navbar-brand" href="index.jsp">MovieZone</a>

    <ul class="navbar-links">
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
        <p class="card-id">ID TMDB: <%= peli.getIdExternoApi() %></p>

        <form action="peliculas" method="POST">
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

</body>
</html>