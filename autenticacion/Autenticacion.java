public interface Autenticacion 
          extends java.rmi.Remote {
    public Boolean autenticar(String nombre, String clave)
        throws java.rmi.RemoteException;

}

