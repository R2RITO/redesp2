import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class Archivo {

    private String filename;
    private String dueno;

    public Archivo(String filename, String dueno) {

        this.filename = filename;
        this.dueno    = dueno;
    }

    public boolean equals(Object o) {

        Archivo file = (Archivo) o;

        String filenameExterno = file.getFilename();
        String duenoExterno    = file.getDueno();

        return this.filename.equals(filenameExterno)
               && this.dueno.equals(duenoExterno);

    }

    public boolean equalsFilename(String filename) {
        return this.filename.equals(filename);
    }

    public String getFilename() {
        return this.filename;

    }

    public String getDueno() {
        return this.dueno;
    }

    public String toString() {
        return "Archivo: "+this.filename+", Dueno: "+this.dueno;
    }

}

