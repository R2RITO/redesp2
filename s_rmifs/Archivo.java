/**
 * Clase Archivo disenada para empaquetar un archivo
 * especifico con su dueno correspondiente; De gran
 * utilidad para realizar listas de los archivos con
 * sus duenos.
 *
 * @author Arturo Voltattorni
 * @author Fernando Dagostino
 */
public class Archivo {

    private String filename;
    private String dueno;

    /**
     * Constructor de la clase Archivo
     * @param filename Es el nombre del archivo
     * @param dueno Es el dueno del archivo
     */
    public Archivo(String filename, String dueno) {

        this.filename = filename;
        this.dueno    = dueno;
    }

    /**
     * Verifica si un archivo es igual a this
     * @param o Es el objeto con el cual comparar
     * @return True si ambos objetos son iguales en contenido
     */
    public boolean equals(Object o) {

        Archivo file = (Archivo) o;

        String filenameExterno = file.getFilename();
        String duenoExterno    = file.getDueno();

        return this.filename.equals(filenameExterno)
               && this.dueno.equals(duenoExterno);

    }

    /**
     * Verifica si this tiene por nombre el string filename
     * @param filename Es el string con el cual comparar
     * @return True si this tiene como nombre el string filename
     */
    public boolean equalsFilename(String filename) {
        return this.filename.equals(filename);
    }

    /**
     * Retorna el nombre del archivo
     * @return El nombre del archivo
     */
    public String getFilename() {
        return this.filename;

    }

    /**
     * Retorna el dueno del archivo
     * @return El dueno del archivo
     */
    public String getDueno() {
        return this.dueno;
    }

    /**
     * Implementacion de toString para un archivo
     * @return El string que representa a un archivo y su dueno.
     */
    public String toString() {
        return "Archivo: "+this.filename+", Dueno: "+this.dueno;
    }

    /**
     * Metodo para generar un string con el nombre del archivo
     * y el dueño separados por el caracter ":"
     * @return El string con el nombre y el dueño
     */
    public String toFileLine() {
        return this.filename + ":" + this.dueno;
    }

}
