<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Registro - MovieZone</title>
  <link rel="icon" href="img/logo.png" type="image/png">
  <link rel="stylesheet" href="css/registro.css">
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

    <form action="usuario" method="POST">
      <input type="hidden" name="accion" value="registrar">

      <div class="form-group">
        <input type="text" name="nombre_completo" placeholder="Nombre completo" required>
      </div>
      <div class="form-group">
        <input type="email" name="correo" placeholder="Correo electrónico" required>
      </div>
      <div class="form-group">
        <input type="password" name="password" placeholder="Contraseña" required>
        <p class="password-hint">Mínimo 8 caracteres, 1 mayúscula y 1 número.</p>
      </div>

      <button type="submit" class="btn-submit">Registrarse</button>
    </form>

    <div class="registro-footer">
      <a href="login.jsp">¿Ya tienes cuenta? Inicia sesión</a>
    </div>

  </div>
</div>

</body>
</html>