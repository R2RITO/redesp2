package autenticacion;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public class AutenticacionClient {

    public static void main(String[] args) {
        try {

            Autenticacion auth = (Autenticacion) Naming.lookup("rmi://127.0.0.1:21000/ServidorAutenticacion");
            
            System.out.println(auth.autenticar("Samantha","clave1"));

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
