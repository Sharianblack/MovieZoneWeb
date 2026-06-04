<%--
  Created by IntelliJ IDEA.
  User: Sharianblack
  Date: 3/6/2026
  Time: 21:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Login - MovieZone</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center" style="height: 100vh;">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center p-4">
                    <h3 class="mb-4">🎬 Iniciar Sesión</h3>

                    <% String msjError = (String) request.getAttribute("mensajeError");
                        if(msjError != null) { %>
                    <div class="alert alert-danger"><%= msjError %></div>
                    <% } %>
                    <% String msjExito = (String) request.getAttribute("mensajeExito");
                        if(msjExito != null) { %>
                    <div class="alert alert-success"><%= msjExito %></div>
                    <% } %>

                    <form action="usuario" method="POST">
                        <input type="hidden" name="accion" value="login">
                        <div class="mb-3">
                            <input type="email" name="correo" class="form-control" placeholder="Correo electrónico" required>
                        </div>
                        <div class="mb-3">
                            <input type="password" name="password" class="form-control" placeholder="Contraseña" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Entrar</button>
                    </form>
                    <div class="mt-3">
                        <a href="registro.jsp" class="text-decoration-none">¿No tienes cuenta? Regístrate aquí</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>