<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.PeliculaGuardada" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>MovieZone - Búsqueda</title>
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
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link active" href="index.jsp">Buscar</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="peliculas?accion=listarFavoritos">Mis Favoritos</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8 text-center">
            <h1 class="mb-4">Encuentra tus películas favoritas</h1>

            <form action="peliculas" method="GET" class="d-flex shadow-sm">
                <input type="hidden" name="accion" value="buscar">

                <input type="text" name="query" class="form-control form-control-lg me-2" placeholder="Ej: Batman, Avengers, Shrek..." required>
                <button type="submit" class="btn btn-primary btn-lg">Buscar</button>
            </form>
        </div>
    </div>

    <div class="row mt-5">
        <%
            // Recuperamos la lista que nos mandó el Controlador desde la API
            List<PeliculaGuardada> peliculas = (List<PeliculaGuardada>) request.getAttribute("listaPeliculas");

            if (peliculas != null && !peliculas.isEmpty()) {
        %>
        <div class="col-12">
            <h3 class="mb-4">Resultados de tu búsqueda:</h3>
        </div>
        <%
            // Empezamos a dibujar una tarjeta por cada película que llegó
            for (PeliculaGuardada peli : peliculas) {
        %>

        <div class="col-md-3 mb-4">
            <div class="card h-100 shadow-sm">
                <img src="<%= peli.getPosterUrl() %>" class="card-img-top" alt="Poster de <%= peli.getTitulo() %>" style="height: 400px; object-fit: cover;">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title"><%= peli.getTitulo() %></h5>
                    <p class="card-text text-muted">ID TMDB: <%= peli.getIdExternoApi() %></p>

                    <form action="peliculas" method="POST" class="mt-auto">
                        <input type="hidden" name="accion" value="guardarLocal">
                        <input type="hidden" name="idApi" value="<%= peli.getIdExternoApi() %>">
                        <input type="hidden" name="titulo" value="<%= peli.getTitulo() %>">
                        <input type="hidden" name="categoria" value="<%= peli.getCategoriaLocal() %>">
                        <input type="hidden" name="posterUrl" value="<%= peli.getPosterUrl() %>">

                        <button type="submit" class="btn btn-outline-success w-100">❤️ Guardar Favorito</button>
                    </form>
                </div>
            </div>
        </div>

        <%
            } // Fin del for
        } else if (peliculas != null) {
        %>
        <div class="col-12 text-center">
            <h4 class="text-danger">No se encontraron películas con ese nombre. 😔</h4>
        </div>
        <% } %>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>