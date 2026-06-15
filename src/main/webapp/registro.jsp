<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Registro - MovieZone</title>
  <link rel="icon" href="img/logo.png" type="image/png">
  <link rel="stylesheet" href="css/registro.css?v=2.0">
</head>
<body>

<div class="registro-wrapper">
  <div class="registro-card">

    <a class="registro-brand" href="index.jsp">MovieZone</a>
    <p class="registro-subtitle">Crear Cuenta</p>

    <%
      String msjError = (String) request.getAttribute("mensajeError");
      if (msjError != null) {
    %>
    <div class="alert alert-danger"><%= msjError %></div>
    <% } %>

    <form action="usuario" method="POST" onsubmit="return validarPasswords()">
      <input type="hidden" name="accion" value="registrar">

      <div class="form-group">
        <input type="text" name="nombre_completo" placeholder="Nombre completo" required>
      </div>

      <div class="form-group">
        <input type="email" name="correo" placeholder="Correo electrónico" required>
      </div>

      <div class="form-group password-group">
        <input type="password" id="pass1" name="password" placeholder="Contraseña" required>
        <button type="button" class="btn-toggle-password" onclick="togglePassword('pass1', 'eyeIcon1')" title="Mostrar/Ocultar">
          <svg id="eyeIcon1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
        </button>
      </div>
      <p class="password-hint" style="font-size: 0.75rem; color: var(--text-muted); margin-top: -8px; margin-bottom: 12px; text-align: left;">
        Mínimo 8 caracteres, 1 mayúscula y 1 número.
      </p>

      <div class="form-group password-group">
        <input type="password" id="pass2" placeholder="Confirma tu contraseña" required>
        <button type="button" class="btn-toggle-password" onclick="togglePassword('pass2', 'eyeIcon2')" title="Mostrar/Ocultar">
          <svg id="eyeIcon2" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
        </button>
      </div>

      <p id="errorPass" style="color: var(--red); font-size: 0.85rem; margin-top: -5px; margin-bottom: 12px; display: none;">
        ¡Pilas! Las contraseñas no coinciden.
      </p>

      <button type="submit" class="btn-submit">Registrarse</button>
    </form>

    <div class="registro-footer">
      <a href="login.jsp">¿Ya tienes cuenta? Inicia sesión</a>
    </div>

  </div>
</div>

<script>
  // Función para comparar las dos claves
  function validarPasswords() {
    const p1 = document.getElementById("pass1").value;
    const p2 = document.getElementById("pass2").value;
    const mensajeError = document.getElementById("errorPass");

    if (p1 !== p2) {
      mensajeError.style.display = "block"; // Mostramos el texto rojo
      return false; // Bloquea el envío al servidor
    }

    mensajeError.style.display = "none";
    return true; // Pasa limpio al Servlet
  }

  // Función modificada para aceptar qué input y qué icono cambiar
  function togglePassword(inputId, iconId) {
    const passwordField = document.getElementById(inputId);
    const eyeIcon = document.getElementById(iconId);

    if (passwordField.type === "password") {
      passwordField.type = "text";
      eyeIcon.innerHTML = '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line>';
    } else {
      passwordField.type = "password";
      eyeIcon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle>';
    }
  }
</script>

</body>
</html>