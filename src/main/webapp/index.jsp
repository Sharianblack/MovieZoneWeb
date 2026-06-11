<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.PeliculaGuardada" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>MovieZone - Búsqueda</title>
    <link rel="stylesheet" href="css/index.css">
</head>
<body>

<nav class="navbar">
    <div class="navbar-inner">
        <a class="navbar-brand" href="index.jsp">MovieZone</a>

        <ul class="navbar-links">
            <li><a href="index.jsp">Buscar</a></li>
            <li><a href="peliculas?accion=listarFavoritos">Mis Favoritos</a></li>

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

    <%
        String mensajeExito = (String) request.getAttribute("mensajeExito");
        if (mensajeExito != null) {
    %>
    <div class="alert alert-success">
        <span><strong>¡De lujo!</strong> <%= mensajeExito %></span>
        <button class="alert-close" onclick="this.parentElement.remove()">✕</button>
    </div>
    <% } %>

    <%
        String mensajeError = (String) request.getAttribute("mensajeError");
        if (mensajeError != null) {
    %>
    <div class="alert alert-warning">
        <span><strong>¡Pilas!</strong> <%= mensajeError %></span>
        <button class="alert-close" onclick="this.parentElement.remove()">✕</button>
    </div>
    <% } %>

    <div class="search-section">
        <span class="search-eyebrow">Tu cine personal</span>
        <h1>Encuentra tus <span>películas</span> favoritas</h1>
        <p class="search-tagline">Busca entre miles de títulos y guarda los que más te gusten</p>

        <form action="peliculas" method="GET" class="search-form">
            <input type="hidden" name="accion" value="buscar">
            <input type="text" name="query" placeholder="Ej: Batman, Avengers, Shrek..." required>
            <button type="submit" class="btn-buscar">Buscar</button>
        </form>
    </div>

    <%
        List<PeliculaGuardada> peliculas = (List<PeliculaGuardada>) request.getAttribute("listaPeliculas");

        if (peliculas != null && !peliculas.isEmpty()) {
    %>

    <div class="resultados-header">
        <span class="resultados-titulo">Resultados de tu búsqueda</span>
    </div>

    <div class="peliculas-grid">
        <%
            for (PeliculaGuardada peli : peliculas) {
        %>
        <div class="card">
            <div class="card-poster">
                <img src="<%= peli.getPosterUrl() %>" alt="Poster de <%= peli.getTitulo() %>">
            </div>
            <div class="card-body">
                <p class="card-title"><%= peli.getTitulo() %></p>
                <p class="card-subtitle">ID TMDB: <%= peli.getIdExternoApi() %></p>

                <form action="peliculas" method="POST">
                    <input type="hidden" name="accion" value="guardarLocal">
                    <input type="hidden" name="idApi" value="<%= peli.getIdExternoApi() %>">
                    <input type="hidden" name="titulo" value="<%= peli.getTitulo() %>">
                    <input type="hidden" name="categoria" value="<%= peli.getCategoriaLocal() %>">
                    <input type="hidden" name="posterUrl" value="<%= peli.getPosterUrl() %>">
                    <button type="submit" class="btn-guardar">Guardar Favorito</button>
                </form>
            </div>
        </div>
        <%
            }
        %>
    </div>

    <%
    } else if (peliculas != null) {
    %>
    <div class="sin-resultados">
        <p>No se encontraron películas con ese nombre.</p>
    </div>
    <% } %>

</div>

</body>
</html>