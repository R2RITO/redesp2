import java.rmi.Naming;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.io.*;

/**
 * a_rmifs.java
 * Clase que implementa al Servidor de Autenticacion.
 * 
 * @author Fernando D'Agostino
 * @author Arturo Voltattorni
 */
public class a_rmifs {

    //Constantes
    private static final int EXIT_FAILURE = -1;

    /**
     * Metodo que crea el objeto a ofrecer remotamente
     * y lo registra.
     * @param usuarios Lista de usuarios validos
     * @param puertoEspecifico el puerto en el que se ofrecera el servicio.
     */
    public a_rmifs(ArrayList<Usuario> usuarios, int puertoEspecifico) {

        try {
            String puerto = Integer.toString(puertoEspecifico);
            LocateRegistry.createRegistry(Integer.parseInt(puerto));            
            a_rmifs_Interface auth = (a_rmifs_Interface) new a_rmifs_Implementation(usuarios);
            String hostName = InetAddress.getLocalHost().getHostName();
            Naming.rebind("rmi://"+hostName+":"+puerto+"/a_rmifs", auth);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }

    }

    /**
     * Metodo que verifica los argumentos pasados al programa
     * @param indice Indice del arreglo de argumentos
     * @param longitud Longitud del arreglo de argumentos
     * @param args Arreglo de argumentos
     * @param listaFlags lista con los flags validos
     */
    private static void verificarArg(int indice, int longitud, String[] args, ArrayList<String> listaFlags) {

        if ((indice+1 == longitud) || listaFlags.contains(args[indice+1])) {
            System.out.println("- ERROR - Sintaxis: java a_rmifs -f usuarios -p puerto");
            System.exit(EXIT_FAILURE);
        }

    }


    public static void main(String args[]) {


        //Definicion de constantes y listas

        int puerto = -1;
        ArrayList<String> listaFlags = new ArrayList<String>();
        listaFlags.add("-p");
        listaFlags.add("-f");
        String archivo = null;

        System.out.println(" - Iniciando el Servidor de Autenticacion - ");

        // Verificacion de argumentos

        int i;
        for (i=0; i<args.length; i++) {

            if (args[i].equals("-p")) {
                verificarArg(i,args.length,args,listaFlags);
                puerto = Integer.parseInt(args[i+1]); 
                
            } else if (args[i].equals("-f")) {
                verificarArg(i,args.length,args,listaFlags);
                archivo = args[i+1];
            }

        }


        if ((puerto == -1) || (archivo == null)) {
            System.out.println("Error, especifique el puerto y el archivo");
            System.out.println("Sintaxis: java a_rmifs -f usuarios -p puerto");
            System.exit(EXIT_FAILURE);
        }


        //Apertura del archivo de usuarios y lectura

        File archivoUsuarios = new File(archivo);
        BufferedReader lector = null;

        ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

        try {

            lector = new BufferedReader(new FileReader(archivoUsuarios));
            String linea = null;
            String tokens[];
            Usuario usr;

            while ((linea = lector.readLine()) != null) {
                
                tokens = linea.split(":");
                usr = new Usuario(tokens[0],tokens[1]);
                usuarios.add(usr);

            }
                
        } catch (FileNotFoundException e) {
             System.out.println("- ERROR - Archivo no encontrado");
        } catch (IOException e) {
             e.printStackTrace();
        } finally {
            try {
                if (lector != null) {
                    lector.close();
                }
            } catch (IOException e) {
                System.out.println("- ERROR - Problema al cerrar descriptor");
            }
        }

        System.out.println(" - Servidor de Autenticacion: Listo - ");

        // Ejecutar el servidor

        new a_rmifs(usuarios,puerto);
    }

}
