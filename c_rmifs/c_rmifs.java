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



    /* Metodo que verifica los argumentos pasados al programa
     * @param indice Indice del arreglo de argumentos
     * @param longitud Longitud del arreglo de argumentos
     * @param args Arreglo de argumentos
     * @param listaFlags lista con los flags validos
     */
    private static void verificarArg(int indice, int longitud, String[] args, ArrayList<String> listaFlags) {

        if ((indice+1 == longitud) || listaFlags.contains(args[indice+1])) {
            System.out.println("- ERROR - Sintaxis: java c_rmifs [-f usuarios] -p puerto -m servidor [-c comandos]");
            System.exit(EXIT_FAILURE);
        }

    }

    /* Funcion que lee el archivo de usuarios y
     * los inserta en una lista
     * @param archivoUsu el nombre del archivo a leer
     * @return retorna la lista con los usuarios del archivo
     */
    public static ArrayList<Usuario> cargarArchivoUsuarios(String archivoUsu) {
         
        
        File archivoUsuarios = new File(archivoUsu);
        BufferedReader lector = null;
        ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

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

        return usuarios;

    }

    /* Funcion que solicita al usuario los datos para
     * autenticarse por pantalla
     * @return La lista con el usuario insertado por pantalla
     */
    public static ArrayList<Usuario> cargarUsuariosPorPantalla() {

        String nombreActual = null;
        String claveActual = null;
        ArrayList<Usuario> usuarios = new ArrayList<Usuario>();


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

        return usuarios;

    }
    

    /* Funcion que utiliza el servidor de autenticacion
    *  para autenticar a los usuarios provistos
    *  @param usuarios Lista con los usuarios a autenticar
    *  @param puerto El puerto para conectarse al servidor de autenticacion
    *  @return Booleano que indica si se pudo autenticar.
    */

    public static boolean autenticarCliente(ArrayList<Usuario> usuarios, int puerto) {

        //Acceder al servidor remoto de autenticacion
        String nActual = null;
        String cActual = null;
        boolean autenticado = false;
        
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

        // Si el usuario fue autenticado, guardar sus datos
        if (autenticado) {
            nombreCliente = nActual;
            claveCliente = cActual;
        }

        return autenticado;

    }

	public static void ejecutarComando(s_rmifs_Interface fs) {

		// Se procede a escuchar los comandos del usuario
        String comando = "";

        BufferedReader stdin =
            new BufferedReader(new InputStreamReader(System.in));
   
        while (true) {

            // Se lee un comando de la entrada estandar
            try {
                System.out.print("Cliente> ");
                comando = stdin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Y se identifica que comando es y que acciones tomar.
            if (comando.equalsIgnoreCase("rls")) {
				try {
					System.out.println(fs.rls(nombreCliente, claveCliente));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			} else if (comando.equalsIgnoreCase("lls")) {

			} else if (comando.equalsIgnoreCase("sub")) {

			} else if (comando.equalsIgnoreCase("baj")) {

			} else if (comando.equalsIgnoreCase("bor")) {

            } else if (comando.equalsIgnoreCase("info")) {

			} else if (comando.equalsIgnoreCase("sal")) {

            } else {

                System.out.println("- Error - Comando desconocido.");

            }
        }


	}

	public static void servidorArchivos(int puerto) {

		try {
            
            s_rmifs_Interface fs = (s_rmifs_Interface) 
				Naming.lookup("rmi://127.0.0.1:"+puerto+"/s_rmifs");
        	ejecutarComando(fs);

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
            usuarios = cargarArchivoUsuarios(archivoUsu);         
        // Solicitar los datos por pantalla
        } else {
            usuarios = cargarUsuariosPorPantalla();         
        }
        
        
        // Una vez tomados los datos, autenticar al usuario 

        boolean autenticado = autenticarCliente(usuarios,puerto);

        //Si el usuario no fue autenticado terminar la ejecucion del programa

        if (!autenticado) {

            System.out.println("No se pudo autenticar, terminando ejecucion");
            System.exit(EXIT_FAILURE);
        }

        //Una vez autenticado, proceder a ejecutar los comandos del archivo

		servidorArchivos(20812);
		


    }
}
