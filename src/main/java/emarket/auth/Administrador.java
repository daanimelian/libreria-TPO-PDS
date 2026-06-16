package emarket.auth;

public class Administrador extends Usuario {

    private final String legajo;

    public Administrador(String username, String pass) {
        super(username, pass);
        this.legajo = "ADM-" + username.toUpperCase();
    }

    public String getLegajo() { return legajo; }
}
