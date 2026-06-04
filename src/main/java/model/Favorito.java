package model;
import java.sql.Timestamp;

public class Favorito {
    private int idFavorito;
    private int idUsuario;
    private int idPelicula;
    private Timestamp fechaAgregado;

    // Constructor vacío
    public Favorito() {
    }

    // Constructor con todos los parámetros
    public Favorito(int idFavorito, int idUsuario, int idPelicula, Timestamp fechaAgregado) {
        this.idFavorito = idFavorito;
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.fechaAgregado = fechaAgregado;
    }

    // Getters y Setters
    public int getIdFavorito() { return idFavorito; }
    public void setIdFavorito(int idFavorito) { this.idFavorito = idFavorito; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public Timestamp getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(Timestamp fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}