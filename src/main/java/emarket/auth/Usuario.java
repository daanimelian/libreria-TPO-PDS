package emarket.auth;

public abstract class Usuario {

    protected String username;
    protected String passwordHash;

    public Usuario(String username, String pass) {
        this.username = username;
        // Hash simulado con hashCode nativo de String
        this.passwordHash = String.valueOf(pass.hashCode());
    }

    public String getUsername() { return username; }

    public boolean validarCredenciales(String pass) {
        return passwordHash.equals(String.valueOf(pass.hashCode()));
    }
}
