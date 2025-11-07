/**
* Clase TutorPracticas.java
*
* @author ADRIAN IGLESIAS RIÑO
* @version 1.0
*/

package entidades;

public class TutorPracticas {

	private Long idTutorEmpresa;

	public void setIdTutorEmpresa(Long idTutorEmpresa) {
		this.idTutorEmpresa = idTutorEmpresa;
	}

	public TutorPracticas() {

	}

	public TutorPracticas(Long idTutorEmpresa) {
		super();
		this.idTutorEmpresa = idTutorEmpresa;
	}

	public Long getIdTutorEmpresa() {
		return idTutorEmpresa;
	}

}
