package Exceptions;

import java.io.Serial;

/**
 * Clase que representa una excepcion personalizada en los datos CSV
 */
public class MissingData extends RuntimeException {

    /**
     * Variable de clase que almacena el serial de la excepción
     */
    @Serial
    private static final long serialVersionUID= 6688681582733838920L;

    /**
     * Constructor con el mensaje de la excepcion correspondiente
     */
    public MissingData() {
        super("PROBLEMA EN LOS DATOS CSV");
    }


}
