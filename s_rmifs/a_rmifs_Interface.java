public interface a_rmifs_Interface extends java.rmi.Remote {

    /**
     * Metodo para autenticar un usuario
     * @param nombre El nombre del usuario
     * @param clave La clave del usuario
     * @return El resultado de la autenticacion, true si fue exitosa.
     * @throws RemoteException Cuando el objeto remoto no esta disponible
     */

    public Boolean autenticar(String nombre, String clave)
        throws java.rmi.RemoteException;

}

