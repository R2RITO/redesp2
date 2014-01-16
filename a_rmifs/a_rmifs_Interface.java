/**
 * 
 * a_rmifs_Interface.java
 * Clase interfaz para los metodos remotos a ser usados por el cliente.
 * 
 * @author Fernando Dagostino
 * @author Arturo Voltattorni
 */
public interface a_rmifs_Interface extends java.rmi.Remote {

    /**
     * Metodo para autenticar un usuario
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @return El resultado de la autenticacion, true si fue exitosa
     * @throws RemoteException al no encontrar el objeto remoto
     */
    public Boolean autenticar(String nombre, String clave)
        throws java.rmi.RemoteException;

}

