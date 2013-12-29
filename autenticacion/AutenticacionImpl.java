package Autenticacion;

import Usuarios.*;

public class CalculatorImpl
    extends 
      java.rmi.server.UnicastRemoteObject
    implements Autenticacion {

    public static final long serialVersionUID = 1L;
    private ArrayList<Usuarios> usuarios;

    public AutenticacionImpl(ArrayList<Usuarios> usuarios)
        throws java.rmi.RemoteException {
	this.usuarios = usuarios;
        super();
    }

    public Boolean autenticar(String nombre, String clave) 
        throws java.rmi.RemoteException {

        Usuario usr = new Usuario(nombre,clave);
        return this.usuarios.contains(usr);
    }

}
