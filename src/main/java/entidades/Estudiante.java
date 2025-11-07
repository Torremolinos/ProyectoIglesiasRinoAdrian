/**
* Clase Alumno.java
*
* @author ADRIAN IGLESIAS RIÑO
* @version 1.0
*/

package entidades;

import java.util.Date;

public class Estudiante {

	private Long idEstudiante;
	private Date fechaInicioFormacion;
	private Date fechaFinFormacion;
	private Date añoAcademico;
	/* Boolean titula? */

	public Estudiante() {
		super();
	}

	public Estudiante(Long idEstudiante, Date fechaInicioFormacion,
			Date fechaFinFormacion, Date añoAcademico) {
		super();
		this.idEstudiante = idEstudiante;
		this.fechaInicioFormacion = fechaInicioFormacion;
		this.fechaFinFormacion = fechaFinFormacion;
		this.añoAcademico = añoAcademico;
	}

	public Long getIdEstudiante() {
		return idEstudiante;
	}

	public void setIdEstudiante(Long idEstudiante) {
		this.idEstudiante = idEstudiante;
	}

	public Date getFechaInicioFormacion() {
		return fechaInicioFormacion;
	}

	public void setFechaInicioFormacion(Date fechaInicioFormacion) {
		this.fechaInicioFormacion = fechaInicioFormacion;
	}

	public Date getFechaFinFormacion() {
		return fechaFinFormacion;
	}

	public void setFechaFinFormacion(Date fechaFinFormacion) {
		this.fechaFinFormacion = fechaFinFormacion;
	}

	public Date getAñoAcademico() {
		return añoAcademico;
	}

	public void setAñoAcademico(Date añoAcademico) {
		this.añoAcademico = añoAcademico;
	}

}
