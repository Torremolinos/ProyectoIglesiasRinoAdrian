package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Documento - Representa un archivo subido al sistema.
 */
@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(name = "nombre_almacenado", nullable = false, length = 255)
    private String nombreAlmacenado;

    @Column(nullable = false, length = 500)
    private String ruta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipo;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "tamano")
    private Long tamano;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    // ============== RELACIONES ==============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fct_id", nullable = false)
    private FCT fct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    public Documento() {
        this.fechaSubida = LocalDateTime.now();
    }

    public Documento(String nombre, String ruta, TipoDocumento tipo, FCT fct, Usuario autor) {
        this();
        this.nombre = nombre;
        this.nombreAlmacenado = generarNombreAlmacenado(nombre);
        this.ruta = ruta;
        this.tipo = tipo;
        this.fct = fct;
        this.autor = autor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreAlmacenado() {
        return nombreAlmacenado;
    }

    public void setNombreAlmacenado(String nombreAlmacenado) {
        this.nombreAlmacenado = nombreAlmacenado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumento tipo) {
        this.tipo = tipo;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getTamano() {
        return tamano;
    }

    public void setTamano(Long tamano) {
        this.tamano = tamano;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public FCT getFct() {
        return fct;
    }

    public void setFct(FCT fct) {
        this.fct = fct;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    private String generarNombreAlmacenado(String nombreOriginal) {
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) {
            extension = nombreOriginal.substring(i);
        }
        return System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
    }

    public String getTamanoFormateado() {
        if (tamano == null)
            return "0 B";
        if (tamano < 1024)
            return tamano + " B";
        if (tamano < 1024 * 1024)
            return String.format("%.1f KB", tamano / 1024.0);
        return String.format("%.1f MB", tamano / (1024.0 * 1024));
    }

    public String getExtension() {
        int i = nombre.lastIndexOf('.');
        return i > 0 ? nombre.substring(i + 1).toUpperCase() : "";
    }

    @Override
    public String toString() {
        return "Documento{" + "nombre='" + nombre + '\'' + ", tipo=" + tipo + ", fechaSubida=" + fechaSubida + '}';
    }
}
