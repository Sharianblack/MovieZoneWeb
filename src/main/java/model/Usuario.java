package model;

public class Usuario {
    private int idUsuario;
    private String nombreCompleto;
    private String correo;
    private String password;
    private String rol; // <-- NUEVO ATRIBUTO
    private int totalPeliculas; // <-- NUEVO

    public Usuario() {
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public int getTotalPeliculas() { return totalPeliculas; }
    public void setTotalPeliculas(int totalPeliculas) { this.totalPeliculas = totalPeliculas; }

}