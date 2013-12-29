public class CalculatorImpl
    extends 
      java.rmi.server.UnicastRemoteObject
    implements Autenticacion {

    public static final long serialVersionUID = 1L;
    private ArrayList<String> usuarios;

    public AutenticacionImpl(ArrayList usuarios)
        throws java.rmi.RemoteException {
	this.usuarios = usuarios;
        super();
    }

    public Boolean autenticar(String nombre, String clave) 
        throws java.rmi.RemoteException {
        return false;
    }

}
