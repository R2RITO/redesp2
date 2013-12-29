public class Usuario {

    private String usuario;
    private String clave;

    public Usuario(String usr, String clave) {

        this.usuario = usr;
        this.clave = clave;

    }

    public Boolean equals(Usuario usr) {

        String usuarioExterno = usr.getUsuario();
        String claveExterna = usr.getClave();

        return this.usuario.equals(usuarioExterno) 
               && this.clave.equals(claveExterna);

    }

    public String getUsuario() {
        return this.usuario;

    }

    public String getClave() {
        return this.clave;
    }


}
