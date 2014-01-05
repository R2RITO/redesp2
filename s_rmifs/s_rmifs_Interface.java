import java.io.File;
import java.io.InputStream;


/*
 * La interfaz s_rmifs_Interface contiene las firmas de las
 * funciones que seran implementadas para el servidor de
 * archivos s_rmifs.
 *
 * @author Arturo Voltattorni
 * @author Fernando Dagostino
 */
public interface s_rmifs_Interface extends java.rmi.Remote {


    /*
     * Funcion que lista todos los archivos contenidos en el servidor
     * de archivos. Es decir, lista los archivos remotos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @return Un string con la lista de archivos remotos
     */
    public String rls(String user, String password) throws java.rmi.RemoteException;


    /*
     * Funcion que sube un archivo al servidor de archivos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @param filename Es el nombre con el cual se va a guardar el archivo
     * @param data Es el contenido del archivo que se va a subir
     * @return Un string con un mensaje de exito o fracaso.
     */
    public String sub(String user, String password, String filename, byte[] data) throws java.rmi.RemoteException;


    /*
     * Funcion que baja un archivo del servidor de archivos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @param filename Es el nombre del archivo que se desea bajar
     * @return Un arreglo de bytes con el contenido del archivo.
     * Si ocurre un error, retorna null.
     */
    public byte[] baj(String user, String password, String filename) throws java.rmi.RemoteException;


    /*
     * Funcion que borra un archivo del servidor de archivos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @param filename Es el nombre del archivo que se desea borrar
     * @return Un string con un mensaje de exito o fracaso.
     */
    public String bor(String user, String password, String filename) throws java.rmi.RemoteException;


    /*
     * Funcion que avisa que un usuario dejo de usar el servidor
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     */
    public void sal(String user, String password) throws java.rmi.RemoteException;

}
