package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Estudiante - Alumno que realiza la Formación en Centro de Trabajo.
 */
@Entity
@Table(name = "estudiantes")
public class Estudiante {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String apellidos;

	@Column(length = 15)
	private String dni;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(length = 20)
	private String telefono;

	@Column(length = 100)
	private String email;

	@Column(length = 300)
	private String direccion;

	@Column(length = 100)
	private String ciclo;

	@Column(length = 50)
	private String grupo;

	@Column(name = "curso_actual")
	private Integer cursoActual;

	@Column(nullable = false)
	private Boolean activo = true;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", unique = true)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profesor_tutor_id")
	private Usuario profesorTutor;

	@OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
	private List<FCT> fcts = new ArrayList<>();

	public Estudiante() {
	}

	public Estudiante(String nombre, String apellidos, String dni) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
	}

	public Estudiante(String nombre, String apellidos, String dni, String ciclo, String grupo) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.ciclo = ciclo;
		this.grupo = grupo;
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

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiclo() {
		return ciclo;
	}

	public void setCiclo(String ciclo) {
		this.ciclo = ciclo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Integer getCursoActual() {
		return cursoActual;
	}

	public void setCursoActual(Integer cursoActual) {
		this.cursoActual = cursoActual;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getProfesorTutor() {
		return profesorTutor;
	}

	public void setProfesorTutor(Usuario profesorTutor) {
		this.profesorTutor = profesorTutor;
	}

	public List<FCT> getFcts() {
		return fcts;
	}

	public void setFcts(List<FCT> fcts) {
		this.fcts = fcts;
	}

	public String getNombreCompleto() {
		return nombre + " " + apellidos;
	}

	public FCT getFctActiva() {
		return fcts.stream().filter(fct -> fct.getEstado() == EstadoFCT.ACTIVA).findFirst().orElse(null);
	}

	public boolean tieneFctEnPeriodo(Periodo periodo) {
		return fcts.stream()
				.anyMatch(fct -> fct.getPeriodo().equals(periodo) && fct.getEstado() != EstadoFCT.CANCELADA);
	}

	@Override
	public String toString() {
		return getNombreCompleto() + " (" + grupo + ")";
	}
}
