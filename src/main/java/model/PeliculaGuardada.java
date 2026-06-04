package model;

public class PeliculaGuardada {
    private int idPelicula;
    private int idExternoApi;
    private String titulo;
    private String categoriaLocal;

    public PeliculaGuardada() {
    }

    public PeliculaGuardada(int idPelicula, int idExternoApi, String titulo, String categoriaLocal) {
        this.idPelicula = idPelicula;
        this.idExternoApi = idExternoApi;
        this.titulo = titulo;
        this.categoriaLocal = categoriaLocal;
    }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public int getIdExternoApi() { return idExternoApi; }
    public void setIdExternoApi(int idExternoApi) { this.idExternoApi = idExternoApi; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoriaLocal() { return categoriaLocal; }
    public void setCategoriaLocal(String categoriaLocal) { this.categoriaLocal = categoriaLocal; }
}