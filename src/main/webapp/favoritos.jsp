<%--
  Created by IntelliJ IDEA.
  User: Sharianblack
  Date: 3/6/2026
  Time: 20:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.PeliculaGuardada" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>MovieZone - Mis Favoritos</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand" href="index.jsp">🎬 MovieZone</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-toggle="target">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto align-items-center">
        <li class="nav-item">
          <a class="nav-link" href="index.jsp">Buscar</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="peliculas?accion=listarFavoritos">Mis Favoritos</a>
        </li>
        <%
          // Leemos si hay un usuario logueado en la memoria
          model.Usuario usuarioActivo = (model.Usuario) session.getAttribute("usuarioLogueado");
          if (usuarioActivo != null) {
        %>
        <li class="nav-item ms-3">
          <span class="text-light me-3">¡Qué más, <%= usuarioActivo.getNombreCompleto() %>! 👋</span>
        </li>
        <li class="nav-item">
          <a class="btn btn-outline-danger btn-sm" href="usuario?accion=logout">Cerrar Sesión</a>
        </li>
        <% } else { %>
        <li class="nav-item ms-3">
          <a class="btn btn-outline-light btn-sm" href="login.jsp">Iniciar Sesión</a>
        </li>
        <% } %>
      </ul>
    </div>
  </div>
</nav>

<div class="container mt-5">
  <div class="row">
    <div class="col-12 text-center mb-4">
      <h1>Mis Películas Guardadas ⭐</h1>
      <p class="text-muted">Las películas que tienes en tu base de datos PostgreSQL</p>
    </div>
  </div>

  <div class="row mt-4">
    <%
      // Recuperamos la lista de la base de datos
      List<PeliculaGuardada> favoritos = (List<PeliculaGuardada>) request.getAttribute("listaFavoritos");

      if (favoritos != null && !favoritos.isEmpty()) {
        for (PeliculaGuardada peli : favoritos) {
    %>

    <div class="col-md-3 mb-4">
      <div class="card h-100 shadow-sm border-success">
        <img src="<%= peli.getPosterUrl() %>" class="card-img-top" alt="Poster" style="height: 400px; object-fit: cover;">
        <div class="card-body d-flex flex-column">
          <h5 class="card-title text-success"><%= peli.getTitulo() %></h5>
          <p class="card-text text-muted"><strong>Categoría:</strong> <%= peli.getCategoriaLocal() %></p>
          <p class="card-text text-muted" style="font-size: 0.85em;">ID TMDB: <%= peli.getIdExternoApi() %></p>

          <div class="mt-auto">
            <form action="peliculas" method="POST" class="mt-2">
              <input type="hidden" name="accion" value="eliminarLocal">
              <input type="hidden" name="idApi" value="<%= peli.getIdExternoApi() %>">
              <button type="submit" class="btn btn-outline-danger w-100" onclick="return confirm('¿Seguro que quieres borrarla, mijo?');">
                🗑️ Eliminar
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <%
      } // Fin del for
    } else {
    %>
    <div class="col-12 text-center mt-5">
      <h4 class="text-secondary">Aún no has guardado ninguna película. 🎬</h4>
      <a href="index.jsp" class="btn btn-primary mt-3">Ir a buscar películas</a>
    </div>
    <% } %>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>