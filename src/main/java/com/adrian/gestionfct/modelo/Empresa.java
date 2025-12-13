package com.adrian.gestionfct.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Empresa - Representa una empresa colaboradora donde se realizan FCT.
 */
@Entity
@Table(name = "empresas")
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String nombre;

	@Column(nullable = false, unique = true, length = 15)
	private String nif;

	@Column(length = 300)
	private String direccion;

	@Column(length = 100)
	private String localidad;

	@Column(name = "codigo_postal", length = 10)
	private String codigoPostal;

	@Column(length = 100)
	private String provincia;

	@Column(length = 20)
	private String telefono;

	@Column(unique = true, length = 100)
	private String email;

	@Column(name = "persona_contacto", length = 200)
	private String personaContacto;

	@Column(length = 500)
	private String sector;

	@Column(nullable = false)
	private Boolean activa = true;

	@Column(length = 1000)
	private String observaciones;

	@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
	private List<TutorEmpresa> tutores = new ArrayList<>();

	@OneToMany(mappedBy = "empresa")
	private List<FCT> fcts = new ArrayList<>();

	public Empresa() {
	}

	public Empresa(String nombre, String nif) {
		this.nombre = nombre;
		this.nif = nif;
	}

	public Empresa(String nombre, String nif, String direccion, String email) {
		this.nombre = nombre;
		this.nif = nif;
		this.direccion = direccion;
		this.email = email;
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

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
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

	public String getPersonaContacto() {
		return personaContacto;
	}

	public void setPersonaContacto(String personaContacto) {
		this.personaContacto = personaContacto;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public Boolean getActiva() {
		return activa;
	}

	public void setActiva(Boolean activa) {
		this.activa = activa;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public List<TutorEmpresa> getTutores() {
		return tutores;
	}

	public void setTutores(List<TutorEmpresa> tutores) {
		this.tutores = tutores;
	}

	public List<FCT> getFcts() {
		return fcts;
	}

	public void setFcts(List<FCT> fcts) {
		this.fcts = fcts;
	}

	public void addTutor(TutorEmpresa tutor) {
		tutores.add(tutor);
		tutor.setEmpresa(this);
	}

	public String getDireccionCompleta() {
		StringBuilder sb = new StringBuilder();
		if (direccion != null)
			sb.append(direccion);
		if (codigoPostal != null)
			sb.append(", ").append(codigoPostal);
		if (localidad != null)
			sb.append(" ").append(localidad);
		if (provincia != null)
			sb.append(" (").append(provincia).append(")");
		return sb.toString();
	}

	@Override
	public String toString() {
		return nombre + " (" + nif + ")";
	}
}
