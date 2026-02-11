package com.adrian.gestionfct.exception;

/**
 * Excepción base para todas las excepciones del sistema GestionFCT.
 * Proporciona un código de error y mensaje descriptivo.
 * 
 * @author Adrián Iglesias Rino
 */
public class GestionFCTException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final String codigoError;

    public GestionFCTException(String mensaje) {
        super(mensaje);
        this.codigoError = "GFCT-000";
    }

    public GestionFCTException(String codigoError, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public GestionFCTException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = "GFCT-000";
    }

    public GestionFCTException(String codigoError, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = codigoError;
    }

    public String getCodigoError() {
        return codigoError;
    }

    @Override
    public String toString() {
        return "[" + codigoError + "] " + getMessage();
    }
}
