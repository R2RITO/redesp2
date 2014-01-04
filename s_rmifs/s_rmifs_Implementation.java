import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.InputStream;


public class s_rmifs_Implementation extends java.rmi.server.UnicastRemoteObject
    implements s_rmifs_Interface {

    public static final long serialVersionUID = 1L; // Que es esto?
    private ArrayList<Archivo> sFiles;

    public s_rmifs_Implementation(ArrayList<Archivo> sFiles) 
    throws java.rmi.RemoteException
    {
        super();
        this.sFiles = sFiles;
    }


    public String rls(String user, String password) throws java.rmi.RemoteException {

        String result = "";
        for (int i=0; i<sFiles.size(); i++) {
            result += i+". "+sFiles.get(i).toString()+"\n\n";
        }
        return result;
    }

    public String sub(String user, String password, String filename, InputStream data) throws java.rmi.RemoteException {

        /*try {
            
            int i;
            char c;

            while((i=data.read())!=-1) {
                // converts integer to character
                c=(char)i;
            
                // prints character
                System.out.print(c);
            }

            /*File newFile = new File(file.getName());
            newFile.createNewFile();
        
            Scanner scanner = new Scanner(file);
            String line;
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                System.out.println("ENTRE EN EL WHILE: "+line);
                writer.println(line);
            }    
        
            writer.close();

        } catch (Exception e) {
            System.out.println("- Error - Problema al subir un archivo");
        }*/
        return "- ALERT - Archivo "+filename+" subido con exito.";
    }

    public String baj(String user, String password, String filename) throws java.rmi.RemoteException {

        return "True";
    }

    public String bor(String user, String password, String filename) throws java.rmi.RemoteException {

        Archivo file = new Archivo(filename, user);

        if (sFiles.contains(file)) {

            sFiles.remove(file);
            File fileToErase = new File(filename);
            fileToErase.delete();
            return "- ALERT - El archivo "+filename+" se ha eliminado con exito.";
        
        } else {
            return "- ALERT - El archivo especificado no existe o no tiene permisos para borrarlo";
        }        
    }

    public void sal(String user, String password) throws java.rmi.RemoteException {

    }
}

