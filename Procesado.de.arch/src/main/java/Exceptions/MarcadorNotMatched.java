package Exceptions;

import java.io.Serial;

/**
 * Clase que representa una Excepcion Personalizada
 */
public class MarcadorNotMatched extends RuntimeException{

        /**
         * Variable de clase que almacena el serial de la excepción
         */
        @Serial
        private static final long serialVersionUID= 7533319097160881522L;

    /**
         * Constructor con el mensaje de la excepcion correspondiente
         */
    public MarcadorNotMatched() {
        super("Marcador no identificado");
    }
}

