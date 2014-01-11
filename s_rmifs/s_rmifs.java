import java.rmi.Naming;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.io.*;

/*
 * Este es el servidor de archivos. Aqui se realizan
 * las lecturas de archivos y verificaciones pertinentes
 * Asimismo, aqui es donde se inicia el servidor.
 *
 * @author Arturo Voltattorni
 * @author Fernando Dagostino
 */
public class s_rmifs {


    /* DECLARACION DE CONSTANTES */

    // Codigo de salida en caso de fallos
    private static int EXIT_FAILURE = -1;
    
    // Nombre del archivo que contiene archivos y propietarios
    private static String cwfName   = "registroArchivos.txt";

    // Ruta donde va a ejecutarse el servidor
    private static String cwdPath   = ".";

    // Lista de archivos disponibles en el servidor de archivos
    private static ArrayList<Archivo> sFiles = new ArrayList<Archivo>();

    // Lista a utilizar para el Log.
    private static ArrayList<String> log = new ArrayList<String>();

    /* CLASES PARA LOS HILOS */

    public static class s_rmifs_thread extends Thread {

        private s_rmifs_Interface fs;
        private String puertolocal;

        public s_rmifs_thread(s_rmifs_Interface fs, String puertolocal) {
            this.fs = fs;
            this.puertolocal = puertolocal;
        }
       
        @Override
        public void run() {
            new s_rmifs(sFiles, this.puertolocal, this.fs); 
        }        
    }


    /* METODOS PARA EL MAIN DEL SERVIDOR DE ARCHIVOS */


    /* Metodo que verifica los argumentos pasados al programa
     * @param indice Indice del arreglo de argumentos
     * @param longitud Longitud del arreglo de argumentos
     * @param args Arreglo de argumentos
     * @param listaFlags lista con los flags validos
     */
    private static void verificarArg(int indice, int longitud, String[] args, ArrayList<String> listaFlags) {

        if ((indice+1 == longitud) || listaFlags.contains(args[indice+1])) {
            System.out.println("- ERROR - Sintaxis: java s_rmifs -l puertolocal -h host -r puerto");
            System.exit(EXIT_FAILURE);
        }

    }
    

    /*
     * Constructor para el servidor de archivos
     * @param sFiles Es la lista de archivos disponibles en el servidor.
     * En este punto, esta lista ya se encuentra verificada y contiene
     * un snapshot del ultimo estado del servidor de archivos.
     * @param puertoEspecifico Es el puerto por donde se asociara
     * al objeto remoto.
     */
    public s_rmifs(ArrayList<Archivo> sFiles, String puerto, s_rmifs_Interface fs) {

        try {
            int puertoEspecifico = Integer.parseInt(puerto);
            LocateRegistry.createRegistry(puertoEspecifico);
            String hostName = InetAddress.getLocalHost().getHostName();
            Naming.rebind("rmi://"+hostName+":"+puerto+"/s_rmifs", fs);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }


    /*
     * Procedimiento que lee el archivo de propietarios de archivos
     * y los coloca en la lista de archivos global 'sFiles'.
     * Al terminar la ejecucion, sFiles debera contener una lista
     * de todos los archivos de los usuarios, cada archivo con
     * su respectivo dueno.
     * @param file Es el archivo del cual se va a leer.
     */
    public static void leerArchivo(File file) {

        try {
            Scanner scanner = new Scanner(file);
            String line;
            String[] result;

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                result = line.split(":");
                sFiles.add(new Archivo(result[0], result[1]));
    
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    /*
     * Procedimiento que agrega los demas archivos que se encuentran
     * en el directorio en donde se ejecuta el servidor, pero que no
     * son propiedad de usuario alguno. Es decir, son archivos que
     * le pertenecen al sistema y que no pueden ser modificados o
     * eliminados.
     * Al terminar su ejecucion, la lista sFiles debera ser la misma
     * lista anterior mas los archivos que corresponden al sistema
     * que se encuentren en la carpeta donde se ejecuto el servidor.
     * @param cwd Es el directorio en donde se ejecuto el servidor
     */
    public static void agregarArchivoSistema(File cwd) {

        File[] fileList = cwd.listFiles();
        String actual;
        for (int i=0; i<fileList.length; i++) {

            actual = fileList[i].getName();
            int j=0;

            // Buscamos si el archivo fue agregado anteriormente
            for (j=0; j<sFiles.size(); j++) {

                if (sFiles.get(j).equalsFilename(actual)) {
                     break;
                }

            }

            // Si el archivo no estaba. Lo agregamos a la lista
            // con el sistema como su dueno. Esto para que otro
            // usuarios no lo modifiquen o lo borren.
            if (j == sFiles.size()) {
                sFiles.add(new Archivo(fileList[i].getName(), "SYSTEM"));
            }

        }

    }


    /*
     * Procedimiento que verifica que todos los archivos que se
     * encuentran en la lista realmente estan en el directorio
     * en donde se ejecuto el servidor. Es decir, verifica que
     * el contenido del archivo de texto se corresponda con los
     * archivos en el directorio de ejecucion.
     * @param cwd Es el archivo que corresponde al directorio de ejecucion
     */
    public static void verificarArchivosListados(File cwd) {

        File[] fileList = cwd.listFiles();
        String actual;
        for (int i=0; i<sFiles.size(); i++) {

            actual = sFiles.get(i).getFilename();
            int j;
            for (j=0; j<fileList.length; j++) {
                if (fileList[j].getName().equals(actual)) {
                    break;
                }
            }
            if (j == fileList.length) {
                System.out.println(" - ERROR - Falta el archivo "+
                                   actual+" en el directorio");
                System.exit(EXIT_FAILURE);
            }
        }
    }

    /* Metodo para escribir la lista de archivos y sus duenos al
     * archivo de registro
     */

    public static void escribirArchivoRegistro() {
        
        try {
              
            File file = new File(cwfName);
 
            file.createNewFile();
 
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            int i;
            String actual;

            for (i=0; i<sFiles.size(); i++) {
                if (!sFiles.get(i).getDueno().equals("SYSTEM")) {
                    actual = sFiles.get(i).toFileLine();
                    bw.write(actual);
                    bw.newLine();
                }
            }

            bw.close();
 
 
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de registro");
            e.printStackTrace();
        }
    }

    /* PROGRAMA PRINCIPAL */

    public static void main(String args[]) {

        System.out.println(" - Iniciando Servidor de Archivos - ");

        //Definicion de constantes y listas
        String puertolocal = null;
        String puerto      = null;
        String host       = null;
        ArrayList<String> listaFlags = new ArrayList<String>();
        listaFlags.add("-l");
        listaFlags.add("-h");
        listaFlags.add("-r");

        // Verificacion de argumentos

        int i;
        for (i=0; i<args.length; i++) {

            if (args[i].equals("-l")) {
                verificarArg(i,args.length,args,listaFlags);
                puertolocal = args[i+1];

            } else if (args[i].equals("-h")) {
                verificarArg(i,args.length,args,listaFlags);
                host = args[i+1];
            } else if (args[i].equals("-r")) {
                verificarArg(i,args.length,args,listaFlags);
                puerto = args[i+1];
            }

        }


        if ((puerto == null) || (host == null) || (puertolocal == null)) {
            System.out.println("- ERROR - Problema de sintaxis al ejecutar.");
            System.out.println("Sintaxis: java s_rmifs -l puertolocal -h host -p puerto");
            System.exit(EXIT_FAILURE);
        }

        File cwf = new File(cwfName);
        
        // Si el archivo de registro ya existe, debemos hacer verificaciones
        // para comprobar que el archivo se corresponda al contenido del
        // directorio.
        if (cwf.exists()) {
                
            // Recorremos el archivo de permisos y agregamos
            // cada archivo con su dueno en una lista
            leerArchivo(cwf);

            // Procedemos a revisar si los archivos en la lista
            // definida previamente se encuentran realmente en
            // el directorio actual
            File cwd = new File(cwdPath);
            verificarArchivosListados(cwd);
            agregarArchivoSistema(cwd);


            //cwd.close();

        // Si el archivo de registro no existe, se crea uno nuevo, se ignora
        // el contenido del directorio actual.
        } else {

            try {

                cwf.createNewFile();            
                File cwd = new File(cwdPath);
                agregarArchivoSistema(cwd);

            } catch (Exception IO) {
                System.out.println(" - ERROR - Al crear el archivo de registro");
                System.exit(EXIT_FAILURE);            
            }
        }

        s_rmifs_Interface fileserver = null;

        try {
            fileserver = new s_rmifs_Implementation(sFiles, log, host, puerto);
            s_rmifs_thread servidor = new s_rmifs_thread(fileserver, puertolocal);
            // Se ejecuta entonces el servidor:
            servidor.start();

        } catch (Exception e) {
            System.out.println("- ERROR - Problema al iniciar el servidor: "+e.getMessage());
            System.exit(EXIT_FAILURE);
        }

        boolean servidorActivo = true;

        System.out.println(" - Servidor de Archivos: Listo. - ");


        // Se procede a escuchar los comandos del usuario
        String comando = "";

        BufferedReader stdin = 
            new BufferedReader(new InputStreamReader(System.in));

        // Mientras el servidor este activo    
        while (servidorActivo) {

            // Se lee un comando de la entrada estandar
            try {
                System.out.print("FileServer> ");
                comando = stdin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Y se identifica que comando es y que acciones tomar.
            if (comando.equalsIgnoreCase("log")) {
                
                try {
                    System.out.println(fileserver.imprimirLog());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if (comando.equalsIgnoreCase("sal")) {

                servidorActivo = false;

                // Guardar los cambios a los archivos en el registro
                escribirArchivoRegistro();                
                System.exit(1); // ESTO HAY Q CAMBIARLO

            } else {

                System.out.println("- Error - Comando desconocido.");

            }
        }
    }
}
