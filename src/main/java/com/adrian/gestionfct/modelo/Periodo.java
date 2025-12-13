package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Periodo - Define los rangos temporales de las prácticas FCT.
 */
@Entity
@Table(name = "periodos")
public class Periodo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(nullable = false)
	private Integer curso;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoPeriodo tipo;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fecha_fin", nullable = false)
	private LocalDate fechaFin;

	@Column(name = "horas_totales")
	private Integer horasTotales;

	// ============== RELACIONES ==============
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curso_academico_id", nullable = false)
	private CursoAcademico cursoAcademico;

	@OneToMany(mappedBy = "periodo")
	private List<FCT> fcts = new ArrayList<>();

	// ============== CONSTRUCTORES ==============
	public Periodo() {
	}

	public Periodo(String nombre, Integer curso, TipoPeriodo tipo, LocalDate fechaInicio, LocalDate fechaFin) {
		this.nombre = nombre;
		this.curso = curso;
		this.tipo = tipo;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	// ============== GETTERS Y SETTERS ==============
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

	public Integer getCurso() {
		return curso;
	}

	public void setCurso(Integer curso) {
		this.curso = curso;
	}

	public TipoPeriodo getTipo() {
		return tipo;
	}

	public void setTipo(TipoPeriodo tipo) {
		this.tipo = tipo;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Integer getHorasTotales() {
		return horasTotales;
	}

	public void setHorasTotales(Integer horasTotales) {
		this.horasTotales = horasTotales;
	}

	public CursoAcademico getCursoAcademico() {
		return cursoAcademico;
	}

	public void setCursoAcademico(CursoAcademico cursoAcademico) {
		this.cursoAcademico = cursoAcademico;
	}

	public List<FCT> getFcts() {
		return fcts;
	}

	public void setFcts(List<FCT> fcts) {
		this.fcts = fcts;
	}

	// ============== MÉTODOS ÚTILES ==============
	public String getDescripcionCompleta() {
		return curso + "º " + tipo.name().toLowerCase() + " (" + fechaInicio + " - " + fechaFin + ")";
	}

	@Override
	public String toString() {
		return nombre + " - " + cursoAcademico.getNombre();
	}
}
