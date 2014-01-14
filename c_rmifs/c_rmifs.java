import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;

/**
 * c_rmifs.java
 * Clase que implementa al Cliente.
 * 
 * @author Fernando D'Agostino
 * @author Arturo Voltattorni
 */
public class c_rmifs {

    /* Constante para salir del programa por error */
    private static final int EXIT_FAILURE = -1;

    /* Nombre del cliente autenticado */
    private static String nombreCliente = null;

    /* Clave del cliente autenticado */
    private static String claveCliente = null;

    // Ruta donde va a ejecutarse el servidor
    private static String cwdPath   = "."; 

    // Nombre del archivo de usuarios especificado por el usuario
    private static String archivoUsu = null;

    // Direccion (IP o Host) del servidor especificado por el usuario
    private static String servidor = null;

    // Numero de puerto especificado por el usuario
    private static int puerto = -1;

    // Nombre del archivo de comandos especificado por el usuario
    private static String comandos = null;


    /** 
     * Metodo que verifica los argumentos pasados al programa
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

    /** 
     * Funcion que lee el archivo de usuarios y
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
           System.out.println("- ERROR - Archivo no encontrado.");
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

    /** 
     * Funcion que solicita al usuario los datos para
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
    

    /**
     * Funcion que utiliza el servidor de autenticacion
     * para autenticar a los usuarios provistos
     * @param usuarios Lista con los usuarios a autenticar
     * @param puerto El puerto para conectarse al servidor de autenticacion
     * @return Booleano que indica si se pudo autenticar.
     */
    public static boolean autenticarCliente(ArrayList<Usuario> usuarios, int puerto) {

        //Acceder al servidor remoto de autenticacion
        String nActual = null;
        String cActual = null;
        boolean autenticado = false;
        
        try {
            
            s_rmifs_Interface auth = (s_rmifs_Interface) Naming.lookup("rmi://"+servidor+":"+puerto+"/s_rmifs");            
            
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


    /**
     * Funcion que ejecuta los comandos en el archivo provisto por el
     * usuario
     * @param fs Es el objeto a utilizar para ejecutar los comandos
     * @param filename Es el nombre del archivo donde estan los comandos1
     */
    public static void ejecutarComandosArchivo(s_rmifs_Interface fs, String filename) {

        if (filename != null) {

            File archivo = new File(filename);
            BufferedReader lector = null;

            // Apertura del archivo de comandos
            try {

                lector = new BufferedReader(new FileReader(archivo));
                String linea = null;

                while ((linea = lector.readLine()) != null) {
                    System.out.println("Cliente> "+linea);
                    ejecutarComando(fs, linea);
                }

            //Excepciones
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                System.out.println("- ERROR - Archivo de comandos no encontrado");
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
        }
    }

    /**
     * Funcion que ejecuta los comandos ejecutados por el usuario
     * @param fs Es el objeto a utilizar para ejecutar los comandos
     * @param lineacomando Es el string/comando introducido por el usuario
     */
    public static void ejecutarComando(s_rmifs_Interface fs, String lineacomando) {
        
        String[] ordenes;
        String argumento = "";
        String comando   = "";

        // Obtener el comando y sus argumentos
        ordenes = lineacomando.split(" ");
        comando = ordenes[0];
        if (ordenes.length > 1) {
            argumento = ordenes[1];
        }

        // Lista para verificar que se haya suministrado el archivo
        ArrayList<String> comandosConArgumento = new ArrayList<String>();
        comandosConArgumento.add("sub");
        comandosConArgumento.add("baj");
        comandosConArgumento.add("bor");
        

        // Verificar que se suministro el archivo en caso de ser necesario
        if (comandosConArgumento.contains(comando) && argumento.equals("")) {

            String archivoFaltante = "Error, por favor suministre el " +
                                     "archivo para ejecutar el comando";
            
            System.out.println(archivoFaltante);
            return;
        }

        // Identificar que comando es y que acciones tomar.
        if (comando.equalsIgnoreCase("rls")) {

            rls(nombreCliente, claveCliente, fs);			

		} else if (comando.equalsIgnoreCase("lls")) {
            
            System.out.println(lls(nombreCliente, claveCliente));

		} else if (comando.equalsIgnoreCase("sub")) {

            sub(nombreCliente, claveCliente, argumento, fs);             

		} else if (comando.equalsIgnoreCase("baj")) {
            
            baj(nombreCliente, claveCliente, argumento, fs);

		} else if (comando.equalsIgnoreCase("bor")) {
            
            bor(nombreCliente, claveCliente, argumento, fs);

        } else if (comando.equalsIgnoreCase("info")) {

            System.out.println(info(nombreCliente, claveCliente));

		} else if (comando.equalsIgnoreCase("sal")) {
            
            sal(nombreCliente, claveCliente, fs);

        } else {

            System.out.println("- Error - Comando desconocido.");

        }
    }

    /**
     * Funcion que ejecuta llama a ejecutarComandosArchivo para procesar
     * los comandos provistos por el archivo de comandos, luego, lee
     * comandos de la consola y los pasa a ejecutarComando.
     * @param fs Es el objeto a utilizar para ejecutar los comandos
     * @param comandos Es el nombre del archivo donde estan los comandos
     */
	public static void escucharCliente(s_rmifs_Interface fs, String comandos) {

        // Se ejecutan los comandos del archivo, si es que los hay.
        ejecutarComandosArchivo(fs, comandos);

		// Se procede a escuchar los comandos del usuario
        String comando = "";
        String argumento = "";
        String[] ordenes;

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

            ejecutarComando(fs, comando);
        }
	}

    /**
     * Funcion que lista los archivos locales del cliente
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * Ambos parametros se solicitan por estandarizacion, no son utilizados.
     * @return El string con los archivos locales.
     */
    public static String lls(String nombre, String clave) {

        // Abrir el directorio del cliente
        File cwd = new File(cwdPath);

        // Obtener los archivos
        File[] listaArchivos = cwd.listFiles();
        String archivos = "\n";
        String actual = null;    

        int i;
        for (i=0; i<listaArchivos.length; i++) {
            
            actual = listaArchivos[i].getName();
            archivos = (archivos +i+". Archivo:"+ actual + "\n\n");
        }
        return archivos;
    }


    /**
     * Funcion que lista los archivos en el servidor remoto
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @param fs La interfaz del servidor para acceder al servicio remoto
     */
    public static void rls(String nombre, String clave, s_rmifs_Interface fs) {

        try {
		    System.out.println(fs.rls(nombreCliente, claveCliente));
        } catch (Exception e) {
	        System.out.println(e.getMessage());
            System.out.println(" - ERROR - El Servidor de Archivos ya no esta disponible");
            System.exit(EXIT_FAILURE);
        }
    }



    /**
     * Funcion que sube un archivo al servidor
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @param argumento El nombre del archivo a subir
     * @param fs La interfaz del servidor para acceder al servicio remoto
     */
    public static void sub(String nombre, String clave, String argumento, s_rmifs_Interface fs) {

        try {

            //Comprobar que el archivo existe localmente
            if (comprobarArchivo(argumento)) {                        
                // Ejecutar el comando
                byte[] archivoFormateado = formatearArchivo(argumento);
                if (archivoFormateado != null) {
                    System.out.println(fs.sub(nombreCliente, claveCliente, argumento, archivoFormateado));
                }                        
                
            } else {

                System.out.println("El archivo solicitado no existe");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(" - ERROR - El Servidor de Archivos ya no esta disponible");
            System.exit(EXIT_FAILURE);
        }
    }


    /**
     * Funcion que baja un archivo desde el servidor remoto
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @param argumento Nombre del archivo a bajar
     * @param fs La interfaz del servidor para acceder al servicio remoto
     */
    public static void baj(String nombre, String clave, String argumento, s_rmifs_Interface fs) {

        try {

            //Comprobar que el archivo no existe localmente
            if (!comprobarArchivo(argumento)) {                        
                // Ejecutar el comando
                byte[] archivoFormateado = fs.baj(nombreCliente, claveCliente, argumento);
                if (archivoFormateado != null) {
                    System.out.println(construirArchivo(argumento, archivoFormateado));
                } else {
                    System.out.println("Error al construir el archivo");
                }
                
            } else {

                System.out.println("El archivo solicitado ya existe");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(" - ERROR - El Servidor de Archivos ya no esta disponible");
            System.exit(EXIT_FAILURE);
        }
    }


    /**
     * Funcion que borra un archivo desde el servidor remoto
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @param argumento Nombre del archivo a borrar
     * @param fs La interfaz del servidor para acceder al servicio remoto
     */
    public static void bor(String nombre, String clave, String argumento, s_rmifs_Interface fs) {

        try {
		    System.out.println(fs.bor(nombreCliente, claveCliente, argumento));
	    } catch (Exception e) {
		    System.out.println(e.getMessage());
            System.out.println(" - ERROR - El Servidor de Archivos ya no esta disponible");
            System.exit(EXIT_FAILURE);
	    }

    }


    /**
     * Funcion que termina la ejecucion del cliente
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @param fs La interfaz del servidor para acceder al servicio remoto
     */
    public static void sal(String nombre, String clave, s_rmifs_Interface fs) {

        try {
            fs.sal(nombreCliente, claveCliente);
            System.out.println("- ALERT - Terminando ejecucion.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(" - ERROR - El Servidor de Archivos ya no esta disponible");
            System.exit(EXIT_FAILURE);
        }

    }


    /**
     * Funcion que lista los comandos disponibles al cliente
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * Ambos parametros se solicitan por estandarizacion, no son utilizados.
     * @return El string con los comandos disponibles
     */
    public static String info(String nombre, String clave) {

        String comandos = "";

        //Especificar cada comando.

        String rls = "rls : \n" +
                     "Muestra la lista de archivos que se encuentran en el " +
                     "servidor remoto. \n\n"; 

        String lls = "lls : \n" +
                     "Muestra la lista de archivos que se encuentran en el " +
                     "directorio local donde se ejecuto el cliente. \n\n";

        String sub = "sub : \n" +
                     "Sube un archivo al servidor remoto, el archivo debe " +
                     "existir en el directorio local y no debe existir " +
                     "en el servidor remoto. \n\n";
        
        String baj = "baj : \n" +
                     "Baja un archivo desde el servidor remoto, el archivo " +
                     "no debe existir en el directorio local y debe existir " +
                     "en el servidor remoto. \n\n";

        String bor = "bor : \n" +
                     "Borra un archivo del servidor local, el archivo " +
                     "debe existir en el servidor remoto y pertenecer " +
                     "al cliente. \n\n";

        String info = "info : \n" +
                      "Muestra informacion sobre los comandos disponibles " +
                      "para el cliente. \n\n";
        
        String sal = "sal : \n" +
                     "Termina la ejecucion del cliente. \n\n";


        //Elaborar un string con los comandos.
        comandos = rls + lls + sub + baj + bor + info + sal;

        return comandos;

    }


    /**
     * Funcion que comprueba si el archivo suministrado
     * se encuentra en el directorio actual
     * @param nombreArchivo el nombre del archivo a comprobar
     * @return True si encuentra el archivo
     */
    public static boolean comprobarArchivo(String nombreArchivo) {
        
        // Abrir el directorio del cliente
        File cwd = new File(cwdPath);

        // Obtener los archivos
        File[] listaArchivos = cwd.listFiles();
        String actual = null;    

        int i;
        for (i=0; i<listaArchivos.length; i++) {
            
            actual = listaArchivos[i].getName();
            
            // Si el archivo existe, retornar busqueda exitosa
            if (actual.equals(nombreArchivo)) {
                return true;
            }
            
        }
    
        return false;
    }


    /**
     * Funcion que transforma el archivo suministrado en
     * un arreglo de bytes para transferir al servidor de archivos
     * @param nombreArchivo el nombre del archivo a comprobar
     * @return True si encuentra el archivo
     */
    public static byte[] formatearArchivo(String nombreArchivo) {
        
        try {

            // Crear un arreglo de bytes con el tamaño del archivo
            File archivo = new File(nombreArchivo);
            byte buffer[] = new byte[(int)archivo.length()];
            BufferedInputStream input =
                new BufferedInputStream(new FileInputStream(nombreArchivo));
            input.read(buffer, 0, buffer.length);
            input.close();
            return(buffer);
        } catch (Exception e) {
            String error = "- Error - Problema al transformar el archivo " +
                           "en un arreglo de bytes";
            System.out.print(error);
            return(null);
        }

    }   

    /**
     * Funcion que transforma el arreglo de bytes en un archivo
     * con el nombre suministrado
     * @param buffer El arreglo de bytes del archivo
     * @param nombreArchivo El nombre del archivo a crear
     * @return Mensaje de exito o fracaso.
     */

    public static String construirArchivo(String nombreArchivo, byte[] buffer) {
        
        try {
            FileOutputStream out = new FileOutputStream(nombreArchivo);
            out.write(buffer);
            out.close();
            return "Archivo " + nombreArchivo + " bajado exitosamente.";
        } catch (Exception e) {
            return "- ALERT - Ocurrio un error al construir el archivo.";
        }        

    } 


    /**
     * Funcion que ejecuta los comandos en el archivo provisto por el
     * usuario
     * @param puerto Es el puerto por donde contactar al servidor de archivos
     * @param comandos Es el nombre de archivos de comandos provisto por el usuario
     * @param hostname Es el hostname en donde se encuentra el servidor de archivos
     */
	public static void servidorArchivos(int puerto, String comandos, String hostname) {

		try {
            
            s_rmifs_Interface fs = (s_rmifs_Interface) 
				Naming.lookup("rmi://"+hostname+":"+puerto+"/s_rmifs");
        	escucharCliente(fs, comandos);

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

        // Agregamos ciertos valores por defecto a las variables globales
        archivoUsu = null;
        servidor = null;
        puerto = -1;
        comandos = null;

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
            System.out.println("Sintaxis: java c_rmifs [-f usuarios] -p puerto -m servidor [-c comandos]");
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

            System.out.println("- ERROR - No se pudo autenticar: terminando ejecucion");
            System.exit(EXIT_FAILURE);
        }

        //Una vez autenticado, proceder a ejecutar los comandos del archivo

		servidorArchivos(puerto, comandos, servidor);

    }
}
