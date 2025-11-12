package edu.utn.entities;

public class FichaBibliografica {
    
    private Long id;
    private boolean eliminado;
    private String isbn;
    private String clasificacionDewey;
    private String estanteria;
    private String idioma;

    public FichaBibliografica(String isbn, String clasificacionDewey, String estanteria, String idioma) {
        this.isbn = isbn;
        this.clasificacionDewey = clasificacionDewey;
        this.estanteria = estanteria;
        this.idioma = idioma;
    }

    public FichaBibliografica(Long id, boolean eliminado, String isbn, String clasificacionDewey, String estanteria, String idioma) {
        this.id = id;
        this.eliminado = eliminado;
        this.isbn = isbn;
        this.clasificacionDewey = clasificacionDewey;
        this.estanteria = estanteria;
        this.idioma = idioma;
    }

    public FichaBibliografica() {
    }

    public Long getId() {
        return id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getClasificacionDewey() {
        return clasificacionDewey;
    }

    public String getEstanteria() {
        return estanteria;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setClasificacionDewey(String clasificacionDewey) {
        this.clasificacionDewey = clasificacionDewey;
    }

    public void setEstanteria(String estanteria) {
        this.estanteria = estanteria;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
}