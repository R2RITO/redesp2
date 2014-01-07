import java.rmi.Naming;
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

    /* CLASES PARA LOS HILOS */

    public static class s_rmifs_thread extends Thread {

        public s_rmifs_thread() {
        }
       
        @Override
        public void run() {
            new s_rmifs(sFiles, 21000);     
        }        
    }


    /* METODOS PARA EL MAIN DEL SERVIDOR DE ARCHIVOS */
    

    /*
     * Constructor para el servidor de archivos
     * @param sFiles Es la lista de archivos disponibles en el servidor.
     * En este punto, esta lista ya se encuentra verificada y contiene
     * un snapshot del ultimo estado del servidor de archivos.
     * @param puertoEspecifico Es el puerto por donde se asociara
     * al objeto remoto.
     */
    public s_rmifs(ArrayList<Archivo> sFiles, int puertoEspecifico) {

        try {
            String puerto = Integer.toString(puertoEspecifico);
            LocateRegistry.createRegistry(21000);
            s_rmifs_Interface fileserver = new s_rmifs_Implementation(sFiles);
            Naming.rebind("rmi://127.0.0.1:"+puerto+"/s_rmifs", fileserver);
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

    /* PROGRAMA PRINCIPAL */

    public static void main(String args[]) {

        System.out.println(" - Iniciando Servidor de Archivos - ");

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

        s_rmifs_thread servidor = new s_rmifs_thread();

        // Se ejecuta entonces el servidor:
        servidor.start();

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

                System.out.println("Falta Implementar Esta PArte");

            } else if (comando.equalsIgnoreCase("sal")) {

                servidorActivo = false;
                System.exit(1); // ESTO HAY Q CAMBIARLO

            } else {

                System.out.println("- Error - Comando desconocido.");

            }
        }
    }
}
