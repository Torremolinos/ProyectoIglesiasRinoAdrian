/**
* Clase Ftc.java
*
* @author ADRIAN IGLESIAS RIÑO
* @version 1.0
*/


package entidades;

public class Ftc {

	private Long id;
	private Estado estado;
	private String observaciones;
	
	
	
	public Ftc() {
		super();
	}
	public Ftc(Long id, Estado estado, String observaciones) {
		super();
		this.id = id;
		this.estado = estado;
		this.observaciones = observaciones;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	
}
