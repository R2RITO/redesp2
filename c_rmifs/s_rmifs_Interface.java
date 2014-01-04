import java.io.File;
import java.io.InputStream;

public interface s_rmifs_Interface extends java.rmi.Remote {

    public String rls(String user, String password) throws java.rmi.RemoteException;

    public String sub(String user, String password, String filename, InputStream data) throws java.rmi.RemoteException;

    public String baj(String user, String password, String filename) throws java.rmi.RemoteException;

    public String bor(String user, String password, String filename) throws java.rmi.RemoteException;

    public void sal(String user, String password) throws java.rmi.RemoteException;

}
