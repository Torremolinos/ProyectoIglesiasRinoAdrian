/**
* Clase Persona.java
*
* @author ADRIAN IGLESIAS RIÑO
* @version 1.0
*/

package entidades;

public class Usuario {

	protected Long id;
	protected String email;
	protected String nombre;
	Credenciales credenciales;

	public Usuario() {
		super();
	}

	public Usuario(Long id, String email, String nombre,
			Credenciales credenciales) {
		super();
		this.id = id;
		this.email = email;
		this.nombre = nombre;
		this.credenciales = credenciales;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Credenciales getCredenciales() {
		return credenciales;
	}

	public void setCredenciales(Credenciales credenciales) {
		this.credenciales = credenciales;
	}

}
