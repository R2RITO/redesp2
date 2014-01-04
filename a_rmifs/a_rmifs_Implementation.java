package autenticacion;

import usuario.*;
import java.util.*;

public class AutenticacionImpl
    extends 
      java.rmi.server.UnicastRemoteObject
    implements Autenticacion {

    public static final long serialVersionUID = 1L;
    private ArrayList<Usuario> usuarios;

    public AutenticacionImpl(ArrayList<Usuario> usuarios) throws java.rmi.RemoteException {
        super();
	    this.usuarios = usuarios;
    }

    public Boolean autenticar(String nombre, String clave) throws java.rmi.RemoteException {

        Usuario usr = new Usuario(nombre,clave);
        return this.usuarios.contains(usr);
    }

}
