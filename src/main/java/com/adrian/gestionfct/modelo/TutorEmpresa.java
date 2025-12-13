package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad TutorEmpresa - Persona de la empresa que tutoriza al estudiante.
 */
@Entity
@Table(name = "tutores_empresa")
public class TutorEmpresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String apellidos;

	@Column(length = 15)
	private String dni;

	@Column(length = 20)
	private String telefono;

	@Column(length = 100)
	private String email;

	@Column(length = 100)
	private String cargo;

	@Column(nullable = false)
	private Boolean activo = true;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", unique = true)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;

	@OneToMany(mappedBy = "tutorEmpresa")
	private List<FCT> fcts = new ArrayList<>();

	public TutorEmpresa() {
	}

	public TutorEmpresa(String nombre, String apellidos, Empresa empresa) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.empresa = empresa;
	}

	public TutorEmpresa(String nombre, String apellidos, String email, Empresa empresa) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.empresa = empresa;
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

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
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

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
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

	public boolean tieneFctsActivas() {
		return fcts.stream().anyMatch(fct -> fct.getEstado() == EstadoFCT.ACTIVA);
	}

	@Override
	public String toString() {
		return getNombreCompleto() + " - " + empresa.getNombre();
	}
}
