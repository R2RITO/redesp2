import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

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

    public String sub(String user, String password, String filename, byte[] data) throws java.rmi.RemoteException {

        // Falta verificar el archivo si hay permisos o no

        try {
            FileOutputStream out = new FileOutputStream(filename);        
            out.write(data);
            out.close();
        } catch (Exception e) {
            return "- ALERT - Error al subir el archivo.";
        }

        return "- ALERT - Archivo "+filename+" subido con exito.";
    }

    public byte[] baj(String user, String password, String filename) throws java.rmi.RemoteException {

        int i;

        for (i=0; i<sFiles.size(); i++) {
            if (sFiles.get(i).equalsFilename(filename)) {
                break;
            }
        }

        if (i == sFiles.size()) {
            return (null);
        }

        try {
            File file = new File(filename);
            byte buffer[] = new byte[(int)file.length()];
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(filename));
            input.read(buffer, 0, buffer.length);
            input.close();
            return(buffer);
        } catch (Exception e) {
            System.out.println("- Error - Problema al ejecutar comando baj");
            return(null);
        }
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

