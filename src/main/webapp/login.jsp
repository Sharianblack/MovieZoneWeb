<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - MovieZone</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>

<div class="login-wrapper">
    <div class="login-card">

        <a class="login-brand" href="index.jsp">MovieZone</a>
        <p class="login-subtitle">Iniciar Sesión</p>

        <%
            String msjError = (String) request.getAttribute("mensajeError");
            if (msjError != null) {
        %>
        <div class="alert alert-danger"><%= msjError %></div>
        <% } %>

        <%
            String msjExito = (String) request.getAttribute("mensajeExito");
            if (msjExito != null) {
        %>
        <div class="alert alert-success"><%= msjExito %></div>
        <% } %>

        <form action="usuario" method="POST">
            <input type="hidden" name="accion" value="login">

            <div class="form-group">
                <input type="email" name="correo" placeholder="Correo electrónico" required>
            </div>
            <div class="form-group">
                <input type="password" name="password" placeholder="Contraseña" required>
            </div>

            <button type="submit" class="btn-submit">Entrar</button>
        </form>

        <div class="login-footer">
            <a href="registro.jsp">¿No tienes cuenta? Regístrate aquí</a>
        </div>

    </div>
</div>

</body>
</html>