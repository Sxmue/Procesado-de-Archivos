

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

/**
 * Clase para customizar el color de la salida en consola
 * 
 * @author samu_
 *
 */
public class ColorCustom extends ForegroundCompositeConverterBase<ILoggingEvent> {



	/**
	 * Metodo para asginar colores personalizados
	 * 
	 * @param event
	 */
	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {

		// Objeto para cambiar el nivel en la traza
		Level level = event.getLevel();

		// Switch para la asignacion de colores
		switch (level.toInt()) {
		case Level.ERROR_INT:
			return ANSIConstants.BOLD + ANSIConstants.RED_FG; // usa rojo para los errores
		case Level.WARN_INT:
			return ANSIConstants.YELLOW_FG;// usa amarillo para los warning
		case Level.INFO_INT:
			return ANSIConstants.GREEN_FG; // usa verde para los info
		default:
			return ANSIConstants.DEFAULT_FG; // color por defecto al resto
		}
	}

}
