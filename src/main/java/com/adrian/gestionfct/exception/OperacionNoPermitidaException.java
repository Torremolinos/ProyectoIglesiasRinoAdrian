package com.adrian.gestionfct.exception;

/**
 * Excepción lanzada cuando una operación no está permitida debido a 
 * reglas de negocio o restricciones del sistema.
 * 
 * @author Adrián Iglesias Rino
 */
public class OperacionNoPermitidaException extends GestionFCTException {

    private static final long serialVersionUID = 1L;
    
    private final String operacion;
    private final String motivo;

    public OperacionNoPermitidaException(String mensaje) {
        super("GFCT-403", mensaje);
        this.operacion = null;
        this.motivo = mensaje;
    }

    public OperacionNoPermitidaException(String operacion, String motivo) {
        super("GFCT-403", String.format("Operación '%s' no permitida: %s", operacion, motivo));
        this.operacion = operacion;
        this.motivo = motivo;
    }

    public String getOperacion() {
        return operacion;
    }

    public String getMotivo() {
        return motivo;
    }

    /**
     * Crea una excepción para eliminación no permitida por dependencias.
     */
    public static OperacionNoPermitidaException eliminacionConDependencias(String entidad, String dependencia) {
        return new OperacionNoPermitidaException("eliminar " + entidad, 
                "tiene " + dependencia + " asociados. Debe eliminarlos primero.");
    }

    /**
     * Crea una excepción para modificación no permitida.
     */
    public static OperacionNoPermitidaException modificacionNoPermitida(String entidad, String estado) {
        return new OperacionNoPermitidaException("modificar " + entidad, 
                "no se puede modificar en estado " + estado);
    }

    /**
     * Crea una excepción para asignación duplicada.
     */
    public static OperacionNoPermitidaException asignacionDuplicada(String mensaje) {
        return new OperacionNoPermitidaException("asignar FCT", mensaje);
    }
}
