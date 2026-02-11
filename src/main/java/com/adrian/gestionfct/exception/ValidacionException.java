package com.adrian.gestionfct.exception;

/**
 * Excepción lanzada cuando hay errores de validación en los datos de entrada.
 * 
 * @author Adrián Iglesias Rino
 */
public class ValidacionException extends GestionFCTException {

    private static final long serialVersionUID = 1L;
    
    private final String campo;

    public ValidacionException(String mensaje) {
        super("GFCT-400", mensaje);
        this.campo = null;
    }

    public ValidacionException(String campo, String mensaje) {
        super("GFCT-400", String.format("Error en campo '%s': %s", campo, mensaje));
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }

    /**
     * Crea una excepción de validación para campo obligatorio.
     */
    public static ValidacionException campoObligatorio(String campo) {
        return new ValidacionException(campo, "es obligatorio");
    }

    /**
     * Crea una excepción de validación para formato inválido.
     */
    public static ValidacionException formatoInvalido(String campo, String formatoEsperado) {
        return new ValidacionException(campo, "formato inválido. Se esperaba: " + formatoEsperado);
    }

    /**
     * Crea una excepción de validación para valor fuera de rango.
     */
    public static ValidacionException fueraDeRango(String campo, Object min, Object max) {
        return new ValidacionException(campo, String.format("debe estar entre %s y %s", min, max));
    }
}
