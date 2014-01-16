import java.util.*;

/**
 * 
 * a_rmifs_Implementation.java
 * Implementacion de la interfaz a_rmifs_Interface
 * 
 * @author Fernando D'Agostino
 * @author Arturo Voltattorni
 */
public class a_rmifs_Implementation
    extends 
      java.rmi.server.UnicastRemoteObject
    implements a_rmifs_Interface {

    public static final long serialVersionUID = 1L;
    private ArrayList<Usuario> usuarios;

    /**
     * Constructor del servidor de autenticacion
     * @param usuarios Lista de usuarios validos
     */
    public a_rmifs_Implementation(ArrayList<Usuario> usuarios) throws java.rmi.RemoteException {
        super();
	    this.usuarios = usuarios;
    }

    /**
     * Metodo para autenticar un usuario
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @return El resultado de la autenticacion, true si fue exitosa
     * @throws RemoteException al no encontrar el objeto remoto
     */
    public Boolean autenticar(String nombre, String clave) throws java.rmi.RemoteException {

        Usuario usr = new Usuario(nombre,clave);
        return this.usuarios.contains(usr);
    }
}
