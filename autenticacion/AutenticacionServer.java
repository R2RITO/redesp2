package autenticacion;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import usuario.*;
import java.util.*;
import java.io.*;

public class AutenticacionServer {

    //Constantes
    private static final int EXIT_FAILURE = -1;


    public AutenticacionServer(ArrayList<Usuario> usuarios, int puertoEspecifico) {

        try {
            String puerto = Integer.toString(puertoEspecifico);
            LocateRegistry.createRegistry(21000);            
            Autenticacion auth = new AutenticacionImpl(usuarios);
            Naming.rebind("rmi://127.0.0.1:"+puerto+"/ServidorAutenticacion", auth);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }

    }

    //Metodo "static" porque no requiere de un objeto para utilizarlo
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
        String archivo = "0";

        //Verificacion de argumentos

        if (args.length < 4) {
            System.out.println("Error, especifique el puerto y el archivo");
            System.exit(EXIT_FAILURE);
        }

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
             //e.printStackTrace();
             System.out.println("Error, archivo no encontrado");
        } catch (IOException e) {
             e.printStackTrace();
        } finally {
            try {
                if (lector != null) {
                    lector.close();
                }
            } catch (IOException e) {
            }
        }


        //Correr el servidor
        new AutenticacionServer(usuarios,puerto);
    }

}
