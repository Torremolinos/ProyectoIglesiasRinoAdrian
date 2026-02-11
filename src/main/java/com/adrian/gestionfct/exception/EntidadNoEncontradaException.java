package com.adrian.gestionfct.exception;

/**
 * Excepción lanzada cuando no se encuentra una entidad en la base de datos.
 * 
 * @author Adrián Iglesias Rino
 */
public class EntidadNoEncontradaException extends GestionFCTException {

    private static final long serialVersionUID = 1L;
    
    private final String tipoEntidad;
    private final Object identificador;

    public EntidadNoEncontradaException(String tipoEntidad, Object identificador) {
        super("GFCT-404", String.format("%s con identificador '%s' no encontrado/a", tipoEntidad, identificador));
        this.tipoEntidad = tipoEntidad;
        this.identificador = identificador;
    }

    public EntidadNoEncontradaException(String mensaje) {
        super("GFCT-404", mensaje);
        this.tipoEntidad = "Entidad";
        this.identificador = null;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public Object getIdentificador() {
        return identificador;
    }
}
