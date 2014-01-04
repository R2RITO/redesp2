import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;

public class c_rmifs {

    public static final int EXIT_FAILURE = -1;
    public static String nombreCliente = null;
    public static String claveCliente = null;

    //Metodo "static" porque no requiere de un objeto para utilizarlo
    private static void verificarArg(int indice, int longitud, String[] args, ArrayList<String> listaFlags) {

        if ((indice+1 == longitud) || listaFlags.contains(args[indice+1])) {
            System.out.println("- ERROR - Sintaxis: java c_rmifs [-f usuarios] -p puerto -m servidor [-c comandos]");
            System.exit(EXIT_FAILURE);
        }

    }

    public static void main(String[] args) {

        //Verificacion de argumentos

        String archivoUsu = null;
        String servidor = null;
        int puerto = -1;
        String comandos = null;

        ArrayList<String> listaFlags = new ArrayList<String>();
        listaFlags.add("-p");
        listaFlags.add("-f");
        listaFlags.add("-c");
        listaFlags.add("-m");

        int i;
        for (i=0; i<args.length; i++) {

            if (args[i].equals("-p")) {
                verificarArg(i,args.length,args,listaFlags);
                puerto = Integer.parseInt(args[i+1]);                 
            } else if (args[i].equals("-f")) {
                verificarArg(i,args.length,args,listaFlags);
                archivoUsu = args[i+1];
            } else if (args[i].equals("-c")) {
                verificarArg(i,args.length,args,listaFlags);
                comandos = args[i+1];
            } else if (args[i].equals("-m")) {
                verificarArg(i,args.length,args,listaFlags);
                servidor = args[i+1];
            }

        }

        //Verificar si la invocacion se hizo con los parametros necesarios

        if ((servidor == null) || (puerto == -1)) {
            System.out.println("Error, especifique el puerto y el servidor");
            System.exit(EXIT_FAILURE);
        }

        //Autenticar al cliente

        //Si fue provisto un archivo de usuarios, se utiliza dicho archivo
        //para autenticar, si no, se solicitan los datos por pantalla


        ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

        if (archivoUsu != null) {

            File archivoUsuarios = new File(archivoUsu);
            BufferedReader lector = null;

            //Apertura del archivo de usuarios

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
            
            //Excepciones
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

        // Solicitar los datos por pantalla
        } else {
            String nombreActual = null;
            String claveActual = null;

            try {
		        BufferedReader lectorEntrada = 
                      new BufferedReader(new InputStreamReader(System.in));
 
                System.out.println("Inserte el nombre de usuario");
		        nombreActual = lectorEntrada.readLine();
            
                System.out.println("Inserte la clave");
                claveActual = lectorEntrada.readLine();
               
                Usuario uActual = new Usuario(nombreActual,claveActual);
                usuarios.add(uActual);   
 
	        } catch (IOException io) {
                System.out.println("Error en la lectura de datos de usuario");
		        io.printStackTrace();
	        }


        }
        
        
        // Una vez tomados los datos, autenticar al usuario 

        boolean autenticado = false;

        //Acceder al servidor remoto de autenticacion
        String nActual = null;
        String cActual = null;
        
        try {
            
            a_rmifs_Interface auth = (a_rmifs_Interface) Naming.lookup("rmi://127.0.0.1:"+puerto+"/ServidorAutenticacion");            
            
            for (Usuario usuActual : usuarios) {

                nActual = usuActual.getUsuario();
                cActual = usuActual.getClave();
                if (auth.autenticar(nActual,cActual)) {
                    autenticado = true;
                    break;
                }

            }
        
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

               
        //Si el usuario no fue autenticado terminar la ejecucion del programa

        if (!autenticado) {

            System.out.println("No se pudo autenticar, terminando ejecucion");
            System.exit(EXIT_FAILURE);

        } else {
            
            nombreCliente = nActual;
            claveCliente = cActual;
        }

        System.out.println("ME AUTENTIQUE WOOO");
        //Una vez autenticado, proceder a ejecutar los comandos del archivo

    }
}
