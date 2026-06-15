
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login - MovieZone</title>
    <link rel="icon" href="img/logo.png" type="image/png">
    <link rel="stylesheet" href="css/login.css?v=3.0">
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
            <div class="form-group password-group">
            <input type="password" id="passwordField" name="password" placeholder="Contraseña" required>
            <button type="button" class="btn-toggle-password" onclick="togglePassword()" title="Mostrar/Ocultar contraseña">
                <svg id="eyeIcon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                </svg>
            </button>
        </div>

            <button type="submit" class="btn-submit">Entrar</button>
        </form>

        <div class="login-footer">
            <a href="registro.jsp">¿No tienes cuenta? Regístrate aquí</a>
        </div>

    </div>
</div>
<script>
    function togglePassword() {
        const passwordField = document.getElementById("passwordField");
        const eyeIcon = document.getElementById("eyeIcon");

        if (passwordField.type === "password") {
            // Cambiamos el input a texto para que se vea la clave
            passwordField.type = "text";
            // Cambiamos el dibujo al SVG del "ojo tachado"
            eyeIcon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line>';
        } else {
            // Lo regresamos a modo contraseña oculta
            passwordField.type = "password";
            // Regresamos al dibujo del ojo normal
            eyeIcon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle>';
        }
    }
</script>
</body>
</html>