package edu.utn.entities;

public class Libro {
    
    private Long id;
    private boolean eliminado;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioEdicion;
    private FichaBibliografica fichaBibliografica;

    public Libro(String titulo, String autor, String editorial, Integer anioEdicion, FichaBibliografica ficha) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioEdicion = anioEdicion;
        this.fichaBibliografica = ficha;
    }

    public Libro(Long id, boolean eliminado, String titulo, String autor, String editorial, Integer anioEdicion, FichaBibliografica ficha) {
        this.id = id;
        this.eliminado = eliminado;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioEdicion = anioEdicion;
        this.fichaBibliografica = ficha;
    }

    public Libro() {
    }

    public Long getId() {
        return id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public Integer getAnioEdicion() {
        return anioEdicion;
    }

    public FichaBibliografica getFichaBibliografica() {
        return fichaBibliografica;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public void setAnioEdicion(Integer anioEdicion) {
        this.anioEdicion = anioEdicion;
    }

    public void setFichaBibliografica(FichaBibliografica fichaBibliografica) {
        this.fichaBibliografica = fichaBibliografica;
    }
    
}