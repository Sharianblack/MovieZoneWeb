<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel Admin - MovieZone</title>
    <link rel="icon" href="img/logo.png" type="image/png">
    <link rel="stylesheet" href="css/admin.css">
</head>
<body>

<nav class="navbar">
    <div class="navbar-inner">
        <a class="navbar-brand" href="index.jsp">
            MovieZone
            <span class="navbar-badge">Admin</span>
        </a>
        <a href="index.jsp" class="btn-volver">Volver al Buscador</a>
    </div>
</nav>

<div class="container">

    <div class="admin-header">
        <h2>Panel de <span>Control</span></h2>
        <p>Gestión de usuarios registrados en la plataforma</p>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Rol</th>
                <th>Películas</th>
                <th>Acción</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Usuario> usuarios = (List<Usuario>) request.getAttribute("listaUsuarios");
                Usuario adminLogueado = (Usuario) session.getAttribute("usuarioLogueado");

                if (usuarios != null) {
                    for (Usuario u : usuarios) {
            %>
            <tr>
                <td class="td-id">#<%= u.getIdUsuario() %></td>
                <td class="td-nombre"><%= u.getNombreCompleto() %></td>
                <td class="td-correo"><%= u.getCorreo() %></td>
                <td>
                    <span class="badge <%= "ADMIN".equals(u.getRol()) ? "badge-admin" : "badge-user" %>">
                        <%= u.getRol() %>
                    </span>
                </td>
                <td class="td-pelis"><%= u.getTotalPeliculas() %> pelis</td>
                <td>
                    <% if (u.getIdUsuario() != adminLogueado.getIdUsuario()) { %>
                    <form action="usuario" method="POST" style="margin:0;">
                        <input type="hidden" name="accion" value="eliminarUsuario">
                        <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                        <button type="submit" class="btn-eliminar"
                                onclick="return confirm('¿Borrar a este usuario para siempre?')">
                            Eliminar
                        </button>
                    </form>
                    <% } else { %>
                    <button class="btn-tu">Tú</button>
                    <% } %>
                </td>
            </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
    </div>

</div>

</body>
</html>