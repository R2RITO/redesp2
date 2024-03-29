import java.util.*;
import java.io.*;
import java.rmi.*;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;


/**
 * Aqui se contienen las implementaciones de los metodos
 * especificados en la interfaz s_rmifs_Interface; Estos
 * representan las funciones del servidor de archivo.
 *
 * @author Arturo Voltattorni
 * @author Fernando Dagostino
 */
public class      s_rmifs_Implementation 
       extends    java.rmi.server.UnicastRemoteObject
       implements s_rmifs_Interface                   {


    public static final long serialVersionUID = 1L;

    /*
     * Lista de archivos manejados por el servidor de archivos
     * Con esta lista se realizaran todas las verificaciones
     * dinamicas y se manejara el archivo de registro.
     */
    private ArrayList<Archivo> sFiles;

    /* IP o hostname del servidor de autenticacion */
    private static String servidorAuth = null;

    /* Puerto para acceder al servidor de autenticacion */
    private static String puertoAuth = null;

    /* Lista de usuarios autenticados */
    private static ArrayList<Usuario> usuarios;

    /* Lista que representa al log */
    private static ArrayList<String> log;

    /**
     * Constructor para la clase s_rmifs_Implementation
     * @param sFiles La lista de archivos que posee el servidor de archivos
     */
    public s_rmifs_Implementation(ArrayList<Archivo> sFiles, ArrayList<String> log, String servAuth, String puerto) 
    throws java.rmi.RemoteException
    {
        // Inicializamos el objeto remoto
        super();
        // Asignamos la lista de archivos al objeto
        this.sFiles = sFiles;
        this.log = log;
        this.servidorAuth = servAuth;
        this.puertoAuth = puerto;
        this.usuarios = new ArrayList<Usuario>();
    }


    /**
     * Metodo para verificar si un usuario esta autenticado
     * @param user Es el nombre de usuario
     * @param pass Es el password del usuario
     */
    public boolean verificarAutenticado(String user, String pass) {
        return usuarios.contains(new Usuario(user,pass));
    }

    /**
     * Funcion que actualiza el log.
     * @param user Es el ID del usuario que ejecuto el comando
     * @param msg Contiene la informacion del comando que ejecuto.
     */
    public void actualizarLog(String user, String msg) {

        if (log.size() < 20) {
            log.add(user+": "+msg);
        } else {
            log.remove(0);
            log.add(19, user+": "+msg);
        }
    }

    /**
     * Procedimiento que retorna un String con la lista de logs.
     */
    public String imprimirLog() 
    throws java.rmi.RemoteException {

        if (log.size() == 0) {
             return " - El log esta vacio - ";
        } 
        
        String result = "";

        for (int i=0; i<log.size(); i++) {
             result += log.get(i)+"\n";
        }

        return result;
    }

    /**
     * Funcion que lista todos los archivos contenidos en el servidor
     * de archivos. Es decir, lista los archivos remotos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @return Un string con la lista de archivos remotos
     */
    public synchronized String rls(String user, String password) 
    throws java.rmi.RemoteException {

        // Verificamos que el usuario este autenticado
        if (!verificarAutenticado(user, password)) {
            return "- ERROR - Usted no se encuentra autenticado";
        }
        
        //Actualizamos el log.
        String infoComando = "Ejecuto el comando rls: \n\tListar los archivos " +
                             "en el servidor remoto";
        actualizarLog(user, infoComando);

        String result = "";

        for (int i=0; i<sFiles.size(); i++) {
            result += i+". "+sFiles.get(i).toString()+"\n\n";
        }
        return result;
    }


    /**
     * Funcion que sube un archivo al servidor de archivos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @param filename Es el nombre con el cual se va a guardar el archivo
     * @param data Es el contenido del archivo que se va a subir
     * @return Un string con un mensaje de exito o fracaso.
     */
    public synchronized String sub(String user, 
                                    String password, 
                                    String filename, 
                                    byte[] data) 
    throws java.rmi.RemoteException {

        // Verificamos que el usuario este autenticado
        if (!verificarAutenticado(user, password)) {
            return "- ERROR - Usted no se encuentra autenticado";
        }

        //Actualizamos el log.
        String infoComando = "Ejecuto el comando sub: \n\tSubir "+filename+
                             " al servidor remoto";
        actualizarLog(user, infoComando);

        // Garantizamos que la lista de archivos no sea modificada
        // mientras leemos de ella. Adicionalmente, garantizamos
        // que no se agreguen dos archivos con el mismo nombre a 
        // la lista de archivos.
        for (int i=0; i<sFiles.size(); i++) {
            if (sFiles.get(i).equalsFilename(filename)) {
                return "- ALERT - El archivo especificado ya existe. \n"+
                       "Intente cambiando el nombre del archivo o "+
                       "borrando el que ya existe con el comando bor.";
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(filename);        
            out.write(data);
            out.close();
            // Agregar el archivo con su dueño a la lista.
            sFiles.add(new Archivo(filename, user));
           
        } catch (Exception e) {
            return "- ALERT - Ocurrio un error al subir el archivo.";
        }

        return "- ALERT - Archivo "+filename+" subido con exito.";
    }


    /**
     * Funcion que baja un archivo del servidor de archivos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @param filename Es el nombre del archivo que se desea bajar
     * @return Un arreglo de bytes con el contenido del archivo.
     * Si ocurre un error, retorna null.
     */
    public synchronized byte[] baj(String user, String password, String filename) 
    throws java.rmi.RemoteException {

        // Verificamos que el usuario este autenticado
        if (!verificarAutenticado(user, password)) {
            return null;
        }

        //Actualizamos el log.
        String infoComando = "Ejecuto el comando baj: \n\tDescargar "+filename+
                             " archivo del servidor remoto";
        actualizarLog(user, infoComando);

        int i;

        for (i=0; i<sFiles.size(); i++) {
            if (sFiles.get(i).equalsFilename(filename)) {
                break;
            }
        }

        // Si llegamos al final de la lista, el archivo no existe.
        if (i == sFiles.size()) {
            return (null);
        }

        try {
            File file = new File(filename);
            byte buffer[] = new byte[(int)file.length()];
            BufferedInputStream input = 
                new BufferedInputStream(new FileInputStream(filename));
            input.read(buffer, 0, buffer.length);
            input.close();
            return(buffer);
        } catch (Exception e) {
            System.out.println("- Error - Problema al ejecutar comando baj");
            return(null);
        }
    }


    /**
     * Funcion que borra un archivo del servidor de archivos.
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     * @param filename Es el nombre del archivo que se desea borrar
     * @return Un string con un mensaje de exito o fracaso.
     */
    public synchronized String bor(String user, String password, String filename) 
    throws java.rmi.RemoteException {

        // Verificamos que el usuario se encuentre autenticado
        if (!verificarAutenticado(user, password)) {
            return "- ERROR - Usted no se encuentra autenticado";
        }

        //Actualizamos el log.
        String infoComando = "Ejecuto el comando bor: \n\tBorrar" + filename+
                             " del servidor remoto siempre que sea el dueno";
        actualizarLog(user, infoComando);


        Archivo file = new Archivo(filename, user);

        if (sFiles.contains(file)) {

            sFiles.remove(file);
            File fileToErase = new File(filename);
            fileToErase.delete();

            sFiles.remove(file);
            return "- ALERT - El archivo "+filename+
                   " se ha eliminado con exito.";
        
        } else {
            return "- ALERT - El archivo especificado no existe o no"+
                   " tiene permisos para borrarlo";
        }        
    }

    /**
     * Funcion que avisa que un usuario dejo de usar el servidor
     * @param user Es el ID del usuario
     * @param password Es la clave del usuario user
     */
    public synchronized void sal(String user, String password) 
    throws java.rmi.RemoteException {
        
        //Actualizamos el log.

        String infoComando = "Ejecuto el comando sal: \n\tTerminar la " +
                             "ejecucion del cliente";
        actualizarLog(user, infoComando);

        // Si el usuario estaba autenticado, lo quitamos de la lista.
        if (!verificarAutenticado(user, password)) {
            usuarios.remove(new Usuario(user,password));
        }

    }

    /**
     * Metodo para autenticar un usuario
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @return El resultado de la autenticacion, true si fue exitosa.
     */
    public Boolean autenticar(String nombre, String clave)
    throws java.rmi.RemoteException {
        
        try {
            
            a_rmifs_Interface auth = (a_rmifs_Interface) Naming.lookup("rmi://"+servidorAuth+":"+puertoAuth+"/a_rmifs");            
            // Si la autenticacion fue exitosa, agregamos el usuario a la lista de autenticados.
            if (auth.autenticar(nombre,clave)) {
                usuarios.add(new Usuario(nombre,clave));
                return true;
            }
            return false;
        
        // Manejo de excepciones.
        } catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        
        } catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }

        return false;
    }
}

