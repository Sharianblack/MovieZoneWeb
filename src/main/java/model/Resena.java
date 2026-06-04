package model;
import java.sql.Timestamp;

public class Resena {
    private int idResena;
    private int idUsuario;
    private int idPelicula;
    private int calificacion;
    private String comentario;
    private Timestamp fechaPublicacion;

    public Resena() {
    }

    public Resena(int idResena, int idUsuario, int idPelicula, int calificacion, String comentario, Timestamp fechaPublicacion) {
        this.idResena = idResena;
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getIdResena() { return idResena; }
    public void setIdResena(int idResena) { this.idResena = idResena; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Timestamp getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Timestamp fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}