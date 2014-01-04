import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class pruebaCliente {

    public static void main(String[] args) {
        try {

            s_rmifs_Interface fs = (s_rmifs_Interface) Naming.lookup("rmi://127.0.0.1:21000/s_rmifs");
            System.out.println(fs.rls("Fertaku", "1234"));
            //System.out.println(fs.bor("arturin", "1234", "ArchivoDeArturin"));
            try {
                InputStream archivoSubida = new FileInputStream("cosaloca");
                System.out.println("Archivo: ");
                System.out.println(archivoSubida);
                System.out.println(fs.sub("Fertaku", "1234", "cosaloca", archivoSubida));
                System.out.println("EJECUTE EL SUBIR");
            } catch (Exception e) {
                System.out.println("ERROR");
            }
            
        }
        catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        }
        catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
        catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }
    }
}

