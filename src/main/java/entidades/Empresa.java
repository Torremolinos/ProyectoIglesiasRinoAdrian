/**
* Clase Empresa.java
*
* @author ADRIAN IGLESIAS RIÑO
* @version 1.0
*/

package entidades;

import java.util.Date;

public class Empresa {

	private Long idEmpresa;
	private Boolean convenio;
	private Date año;

	public Empresa() {
		super();
	}

	public Empresa(Long idEmpresa, Boolean convenio, Date año) {
		super();
		this.idEmpresa = idEmpresa;
		this.convenio = convenio;
		this.año = año;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Boolean getConvenio() {
		return convenio;
	}

	public void setConvenio(Boolean convenio) {
		this.convenio = convenio;
	}

	public Date getAño() {
		return año;
	}

	public void setAño(Date año) {
		this.año = año;
	}

}
