package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad FCT - Formación en Centro de Trabajo. Es la entidad CENTRAL del
 * sistema.
 */
@Entity
@Table(name = "fcts", uniqueConstraints = @UniqueConstraint(columnNames = { "estudiante_id",
		"periodo_id" }, name = "uk_estudiante_periodo"))
public class FCT {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoFCT estado = EstadoFCT.ACTIVA;

	@Column(name = "fecha_inicio")
	private LocalDate fechaInicio;

	@Column(name = "fecha_fin")
	private LocalDate fechaFin;

	@Column(name = "horas_realizadas")
	private Integer horasRealizadas = 0;

	@Column(name = "horas_totales")
	private Integer horasTotales;

	@Column(length = 1000)
	private String observaciones;

	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	@Column(name = "fecha_modificacion")
	private LocalDateTime fechaModificacion;

	// ============== RELACIONES ==============
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estudiante_id", nullable = false)
	private Estudiante estudiante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tutor_empresa_id", nullable = false)
	private TutorEmpresa tutorEmpresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "periodo_id", nullable = false)
	private Periodo periodo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curso_academico_id", nullable = false)
	private CursoAcademico cursoAcademico;

	@OneToMany(mappedBy = "fct", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Documento> documentos = new ArrayList<>();

	// ============== CONSTRUCTORES ==============
	public FCT() {
		this.fechaCreacion = LocalDateTime.now();
		this.fechaModificacion = LocalDateTime.now();
	}

	public FCT(Estudiante estudiante, Empresa empresa, TutorEmpresa tutorEmpresa, Periodo periodo) {
		this();
		this.estudiante = estudiante;
		this.empresa = empresa;
		this.tutorEmpresa = tutorEmpresa;
		this.periodo = periodo;
		this.cursoAcademico = periodo.getCursoAcademico();
		this.fechaInicio = periodo.getFechaInicio();
		this.fechaFin = periodo.getFechaFin();
		this.horasTotales = periodo.getHorasTotales();
	}

	// ============== GETTERS Y SETTERS ==============
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EstadoFCT getEstado() {
		return estado;
	}

	public void setEstado(EstadoFCT estado) {
		this.estado = estado;
		this.fechaModificacion = LocalDateTime.now();
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

	public Integer getHorasRealizadas() {
		return horasRealizadas;
	}

	public void setHorasRealizadas(Integer horasRealizadas) {
		this.horasRealizadas = horasRealizadas;
	}

	public Integer getHorasTotales() {
		return horasTotales;
	}

	public void setHorasTotales(Integer horasTotales) {
		this.horasTotales = horasTotales;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public Estudiante getEstudiante() {
		return estudiante;
	}

	public void setEstudiante(Estudiante estudiante) {
		this.estudiante = estudiante;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public TutorEmpresa getTutorEmpresa() {
		return tutorEmpresa;
	}

	public void setTutorEmpresa(TutorEmpresa tutorEmpresa) {
		this.tutorEmpresa = tutorEmpresa;
	}

	public Periodo getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
		if (periodo != null) {
			this.cursoAcademico = periodo.getCursoAcademico();
		}
	}

	public CursoAcademico getCursoAcademico() {
		return cursoAcademico;
	}

	public void setCursoAcademico(CursoAcademico cursoAcademico) {
		this.cursoAcademico = cursoAcademico;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	// ============== MÉTODOS ÚTILES ==============
	public void addDocumento(Documento documento) {
		documentos.add(documento);
		documento.setFct(this);
	}

	public void removeDocumento(Documento documento) {
		documentos.remove(documento);
		documento.setFct(null);
	}

	public double getPorcentajeCompletado() {
		if (horasTotales == null || horasTotales == 0)
			return 0;
		return (horasRealizadas * 100.0) / horasTotales;
	}

	public void finalizar() {
		this.estado = EstadoFCT.FINALIZADA;
		this.fechaModificacion = LocalDateTime.now();
	}

	public void cancelar() {
		this.estado = EstadoFCT.CANCELADA;
		this.fechaModificacion = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "FCT{" + "estudiante=" + (estudiante != null ? estudiante.getNombreCompleto() : "N/A") + ", empresa="
				+ (empresa != null ? empresa.getNombre() : "N/A") + ", periodo="
				+ (periodo != null ? periodo.getNombre() : "N/A") + ", estado=" + estado + '}';
	}
}
