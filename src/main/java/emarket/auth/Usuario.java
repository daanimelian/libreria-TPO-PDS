package emarket.auth;

public abstract class Usuario {

    protected String username;
    protected String passwordHash;

    public Usuario(String username, String pass) {
        this.username = username;
        this.passwordHash = String.valueOf(pass.hashCode());
    }

    public String getUsername() { return username; }

    public boolean validarCredenciales(String pass) {
        return passwordHash.equals(String.valueOf(pass.hashCode()));
    }

    public String getPasswordHash() { return passwordHash; }

    // Para uso exclusivo de la capa de persistencia: restaura el hash ya calculado
    public void setPasswordHash(String hash) { this.passwordHash = hash; }
}
