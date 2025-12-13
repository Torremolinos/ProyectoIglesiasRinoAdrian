package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad CursoAcademico - Define el año académico (ej: 2024-2025).
 */
@Entity
@Table(name = "cursos_academicos")
public class CursoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 9)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = false;

    @OneToMany(mappedBy = "cursoAcademico", cascade = CascadeType.ALL)
    private List<Periodo> periodos = new ArrayList<>();

    @OneToMany(mappedBy = "cursoAcademico")
    private List<FCT> fcts = new ArrayList<>();

    public CursoAcademico() {
    }

    public CursoAcademico(String nombre) {
        this.nombre = nombre;
    }

    public CursoAcademico(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
    }

    public List<FCT> getFcts() {
        return fcts;
    }

    public void setFcts(List<FCT> fcts) {
        this.fcts = fcts;
    }

    public void addPeriodo(Periodo periodo) {
        periodos.add(periodo);
        periodo.setCursoAcademico(this);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
