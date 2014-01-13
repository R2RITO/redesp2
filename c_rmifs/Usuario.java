/**
 * Clase para crear usuarios que tengan
 * un nombre y una clave.
 *
 * @author Arturo Voltattorni
 * @author Fernando Dagostino
 */


public class Usuario {

    private String usuario;
    private String clave;

    /** Constructor de la clase
     * @param usr Nombre del usuario
     * @param clave Clave del usuario
     */

    public Usuario(String usr, String clave) {

        this.usuario = usr;
        this.clave = clave;

    }

    /** Metodo que sirve para comparar dos usuarios
     * @param o El objeto a comparar
     * @return Resultado de la comparacion, true si son iguales.
     */

    public boolean equals(Object o) {

        Usuario usr = (Usuario) o;

        String usuarioExterno = usr.getUsuario();
        String claveExterna = usr.getClave();

        return this.usuario.equals(usuarioExterno) 
               && this.clave.equals(claveExterna);

    }

    /** Metodo para obtener el campo "usuario"
     * @return El nombre de usuario
     */

    public String getUsuario() {
        return this.usuario;

    }

    /** Metodo para obtener el campo "clave"
     * @return La clave de usuario
     */

    public String getClave() {
        return this.clave;
    }


}
