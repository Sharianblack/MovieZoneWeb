package model;

public class PeliculaGuardada {
    private int idPelicula;
    private int idExternoApi;
    private String titulo;
    private String categoriaLocal;
    private int calificacionUsuario; // Guardará de 1 a 5 estrellas (0 si no tiene)
    private String comentarioUsuario; // Guardará el texto de la reseña

    public PeliculaGuardada() {
    }

    public PeliculaGuardada(int idPelicula, int idExternoApi, String titulo, String categoriaLocal) {
        this.idPelicula = idPelicula;
        this.idExternoApi = idExternoApi;
        this.titulo = titulo;
        this.categoriaLocal = categoriaLocal;
    }
    private String posterUrl;

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public int getIdExternoApi() { return idExternoApi; }
    public void setIdExternoApi(int idExternoApi) { this.idExternoApi = idExternoApi; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoriaLocal() { return categoriaLocal; }
    public void setCategoriaLocal(String categoriaLocal) { this.categoriaLocal = categoriaLocal; }

    public int getCalificacionUsuario() { return calificacionUsuario; }
    public void setCalificacionUsuario(int calificacionUsuario) { this.calificacionUsuario = calificacionUsuario; }

    public String getComentarioUsuario() { return comentarioUsuario; }
    public void setComentarioUsuario(String comentarioUsuario) { this.comentarioUsuario = comentarioUsuario; }
}