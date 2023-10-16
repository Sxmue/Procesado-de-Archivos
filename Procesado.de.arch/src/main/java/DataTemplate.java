import Exceptions.MarcadorNotMatched;
import Exceptions.MissingData;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;


/**
 * Clase para rellenar templates con datos
 *
 * @author Samuel Leiva Alvarez
 */
public class DataTemplate {

    /*
     * Declaracion del logger que se usara para trazar la aplicacion
     */
    static final Logger LOG = LoggerFactory.getLogger(DataTemplate.class);


    /* Boolean para indicar si es el inicio del programa o no, nos ayudara con el borrado*/
    private static boolean start = false;

    //Puntero para saber la linea que estamos leyendo,usado en caso de error
    private static int linePointer = 0;


    /**
     * Metodo para la ejecución del proceso completo
     *
     * @param data     el nombre o la ruta del archivo csv
     * @param template el nombre o la ruta de la plantilla
     */
    public static void doTemplate(String data, String template) {
        LOG.info("Inicio del metodo doTemplate");
        //Variable en la que se almacena la lectura de datos
        String s;
        //Variable en la que se almacenan el template modificado
        String c;

        //Contador usado para no leer la primera linea del archivo csv
        int contador = 0;

        //Flujo de lectura
        try (BufferedReader br = new BufferedReader(new FileReader(data))) {
            while (((s = br.readLine()) != null)) {

                //Sumamos al puntero de la linea para saber en que linea en caso de error
                linePointer++;

                //Salto de la primera linea con el contador y de las lineas de espacios en blanco
                if (contador > 0 && !s.isBlank()) {

                    //Almacenaje del template modificado
                    c = templateChanger(s, template);

                    //Escritura del nuevo template
                    templateWriter(s, c);

                    LOG.info("Archivo para el usuario " + s.substring(0, 3) + " creado correctamente");

                }
                //suma en el contador
                contador++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Metodo para leer una plantilla
     *
     * @param template recibe el nombre del archivo con el template
     * @return un StringBuilder con la plantilla
     */
    private static StringBuilder readTemplate(String template) {
        LOG.info("Inicio de la lectura del template");

        //Variable en la que almacenamos la lectura
        String s;

        //Variable que se devuelve en el metodo con el concatenado de la lectura
        StringBuilder c = new StringBuilder();

        //Flujo de lectura
        try (BufferedReader br2 = new BufferedReader(new FileReader(template))) {
            while (((s = br2.readLine()) != null)) {

                //Concatenacion de la lectura
                c.append(s);
                c.append("\n");

            }
            LOG.info("Template leido correctamente");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return c;
    }


    /**
     * Metodo para realizar los cambios en el template
     *
     * @param data         String que almacena una linea de datos del archivo csv
     * @param templatePath recibe el nombre o la ruta de la plantilla (usado internamente en el metodo readTemplate)
     * @return String que almacena la plantilla modificada
     */
    private static String templateChanger(String data, String templatePath) {
        LOG.info("Inicio del templateChanger");

        //Creacion del Array de String en el que se tiene la linea de datos csv separada por ","
        String[] fields = dataValidator(data);

        //Traemos la plantilla con el metodo readTemplate
        StringBuilder c = readTemplate(templatePath);

        //Declaracion del String en el que se realizaran las modificaciones
        String s = "";

        //Conversion de la plantilla a String
        s = c.toString();

        //Modificaciones en la plantilla con los campos de fields[]
        s = s.replace("{{NombreResp}}", fields[4])
                .replace("{{NombreEmp}}", fields[1])
                .replace("{{Correo}}", fields[3])
                .replace("{{Localidad}}", fields[2]);
        LOG.info("Conversion realizada");


        //Pasamos a s el markupValidator para neutralizar los marcadores no conocidos
        s = markupValidator(s);

        return s;
    }

    /**
     * Metodo para la validacion de los datos csv
     *
     * @param data String con la linea de datos que hemos leido del csv
     * @return devuelve un Array de String
     */
    private static String[] dataValidator(String data) {
        LOG.info("Inicio de la validacion de los datos");

        //Array de Strings que usamos en la devolucion
        String[] d;

        //lo rellenamos con los datos del csv
        d = data.split(",");

        try {

            //Si hay menos o más de 5 datos
            if (d.length != 5) {

                //Lo declaramos de 5 huecos
                d= new String[5];

                //rellena el array con 5 palabras error
                Arrays.fill(d, "ERROR");

                LOG.error("Hay problemas en los datos del csv");

                //Lanzamos exepcion
                throw new MissingData();
            }
        } catch (MissingData e) {

            //Tratamiento de la excepcion con el mensaje mas la linea en la que hemos dado error
            System.out.println(e.getMessage() + " EN LA LINEA " + linePointer);

        }

        LOG.info("Validacion del csv completada");

        return d;
    }

    /**
     * Metodo para la validacion de los marcadores
     *
     * @param s String que almacena el template con los cambios realizados
     * @return
     */
    private static String markupValidator(String s) {
        LOG.info("Inicio de la validacion de las marcas");

        //Try para la verificacion de marcador extra
        try {

            //Si despues de la conversion se encuentra alguna llave mas
            if (s.contains("{")) {

                LOG.error("HAY MARCADORES DE MAS");

                //Reemplaza el contenido de los marcadores extra por espacios vacios
                s = s.replaceAll("\\{\\{\\w+}}", "_________");

                //Lanzamiento de excepcion
                throw new MarcadorNotMatched();
            }

        } catch (MarcadorNotMatched e) {
            System.out.println(e.getMessage());
        }

        LOG.info("Validacion de los marcadores completada");

        return s;
    }


    /**
     * Metodo para la escritura del template modificado
     *
     * @param dataRow String con la linea de datos original,usada solo para la personalizacion del nombre final del archivo
     * @param defText String con el template ya modificado y listo para escribirse
     */
    private static void templateWriter(String dataRow, String defText) {

        //String que se usara en nombre del archivo
        String filename = "salida/template-" + dataRow.substring(0, 3) + ".txt";

        //Si el texto definitivo contiene error, le añade al archivo final la coletilla -err
        if (defText.contains("ERROR")) filename=filename.replace(".txt","-err.txt");

        //Metodo que se encarga de preparar la carpeta en caso de ser necesario
        folderSetter();

        //Flujo de escritura con el nombre de archivo personalizado
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {

            //Escritura del texto definitivo
            bw.write(String.valueOf(defText));
            LOG.info("Nuevo template escrito correctamente");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo que se encarga de prepara la carpeta en la escritura de archivos
     */
    private static void folderSetter() {
        //Carpeta usada en la escritura de archivos
        File carpet = new File("salida");

        //Si la carpeta no esta creada, se crea
        if (!carpet.exists()) {
            LOG.warn("La carpeta no existia");
            try {

                //Creacion de la carpeta
                FileUtils.forceMkdir(carpet);
                LOG.info("Carpeta creada con exito");


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            //Si esta creada anteriormente
            try {
                //Comprobacion para saber si es el inicio del programa
                if (!start) {

                    //Si el programa no esta empezado
                    //Se borra la carpeta de manera recursiva
                    FileUtils.cleanDirectory(carpet);

                    LOG.info("Carpeta limpiada correctamente");

                    //Cambio en el boolean para que solo borre al inicio de la ejecucion
                    start = Boolean.TRUE;

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Metodo Main para el inicio del programa
     *
     * @param args usados en el main
     */
    public static void main(String[] args) {

        //Llamada stática a la clase DataTemplate para el inicio del programa
        DataTemplate.doTemplate("data.csv.txt", "plantilla.txt");


    }
}


