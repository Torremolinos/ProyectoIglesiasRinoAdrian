/**
* Clase Documento.java
*
* @author ADRIAN IGLESIAS RIÑO
* @version 1.0
*/


package entidades;

public class Documento {
	private Long id;
	private Boolean convenio;
	
	
	public Documento() {
		super();
	}
	public Documento(Long id, Boolean convenio) {
		super();
		this.id = id;
		this.convenio = convenio;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getConvenio() {
		return convenio;
	}
	public void setConvenio(Boolean convenio) {
		this.convenio = convenio;
	}
	
}
