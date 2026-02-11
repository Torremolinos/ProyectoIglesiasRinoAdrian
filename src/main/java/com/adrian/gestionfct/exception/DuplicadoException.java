package com.adrian.gestionfct.exception;

/**
 * Excepción lanzada cuando se intenta crear o modificar una entidad 
 * con datos que ya existen (violación de unicidad).
 * 
 * @author Adrián Iglesias Rino
 */
public class DuplicadoException extends GestionFCTException {

    private static final long serialVersionUID = 1L;
    
    private final String campo;
    private final Object valor;

    public DuplicadoException(String campo, Object valor) {
        super("GFCT-409", String.format("Ya existe un registro con %s = '%s'", campo, valor));
        this.campo = campo;
        this.valor = valor;
    }

    public DuplicadoException(String mensaje) {
        super("GFCT-409", mensaje);
        this.campo = null;
        this.valor = null;
    }

    public String getCampo() {
        return campo;
    }

    public Object getValor() {
        return valor;
    }

    /**
     * Crea una excepción para NIF duplicado.
     */
    public static DuplicadoException nifDuplicado(String nif) {
        return new DuplicadoException("NIF", nif);
    }

    /**
     * Crea una excepción para email duplicado.
     */
    public static DuplicadoException emailDuplicado(String email) {
        return new DuplicadoException("email", email);
    }

    /**
     * Crea una excepción para DNI duplicado.
     */
    public static DuplicadoException dniDuplicado(String dni) {
        return new DuplicadoException("DNI", dni);
    }
}
