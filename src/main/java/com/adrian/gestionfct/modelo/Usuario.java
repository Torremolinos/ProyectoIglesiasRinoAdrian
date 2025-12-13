package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Usuario - Gestiona el acceso al sistema.
 * 
 * Cada usuario tiene un rol que determina sus permisos y a qué panel accede.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String apellidos;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(length = 20)
	private String telefono;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Rol rol;

	@Column(nullable = false)
	private Boolean activo = true;

	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	@Column(name = "ultimo_acceso")
	private LocalDateTime ultimoAcceso;

	public Usuario() {
		this.fechaCreacion = LocalDateTime.now();
	}

	public Usuario(String nombre, String apellidos, String email, String password, Rol rol) {
		this();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
		this.rol = rol;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getUltimoAcceso() {
		return ultimoAcceso;
	}

	public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
		this.ultimoAcceso = ultimoAcceso;
	}

	public String getNombreCompleto() {
		return nombre + " " + apellidos;
	}

	public void registrarAcceso() {
		this.ultimoAcceso = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return getNombreCompleto() + " (" + rol + ")";
	}
}