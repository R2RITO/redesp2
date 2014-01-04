import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.io.*;

public class s_rmifs {


    // Declaracion de constantes
    private static int EXIT_FAILURE = -1;
    private static String cwfName   = "registroArchivos.txt";
    private static String cwdPath   = ".";
    private static ArrayList<Archivo> sFiles = new ArrayList<Archivo>();


    // Clases para la creacion de hilos

    public static class s_rmifs_listen extends Thread {



        public s_rmifs_listen() {

        }       

        @Override
        public void run() {

            while (true) {
                System.out.println("SOY UNA DIVINA");
            }        
        }        
    }


    // Metodos para el Main del servidor de archivos
    
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
            // usuarios no lo modifiquen sin querer.
            if (j == sFiles.size()) {
                sFiles.add(new Archivo(fileList[i].getName(), "SYSTEM"));
            }

        }

    }


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
                System.out.println(" - ERROR - Falta el archivo "+actual+" en el directorio");
                System.exit(EXIT_FAILURE);
            }

        }
    }

    public static void main(String args[]) {

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

        for (int i=0; i<sFiles.size(); i++) {

            System.out.println(sFiles.get(i).toString());

        }

        System.out.println(sFiles.toString());

        s_rmifs_listen cosa = new s_rmifs_listen();

        //Correr el servidor
        new s_rmifs(sFiles, 21000);


    }

}
