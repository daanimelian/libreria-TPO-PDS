package emarket.auth;

/**
 * Clase base abstracta para todos los usuarios del sistema.
 *
 * <p>Encapsula las credenciales comunes (nombre de usuario y contraseña hasheada)
 * y define el contrato de autenticación. Las subclases {@link Cliente} y
 * {@link Administrador} extienden esta clase con atributos y comportamientos propios
 * de cada rol.
 */
public abstract class Usuario {

    protected String username;
    protected String passwordHash;

    /**
     * Construye un usuario hasheando la contraseña provista.
     *
     * @param username nombre de usuario único en el sistema
     * @param pass     contraseña en texto plano; se almacena como hash
     */
    public Usuario(String username, String pass) {
        this.username     = username;
        this.passwordHash = String.valueOf(pass.hashCode());
    }

    /** @return nombre de usuario de esta cuenta */
    public String getUsername() { return username; }

    /**
     * Verifica si la contraseña provista corresponde a la almacenada.
     *
     * @param pass contraseña en texto plano a verificar
     * @return {@code true} si el hash coincide con el almacenado
     */
    public boolean validarCredenciales(String pass) {
        return passwordHash.equals(String.valueOf(pass.hashCode()));
    }

    /** @return hash de la contraseña (para persistencia) */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Restaura el hash de contraseña ya calculado.
     * Uso exclusivo de la capa de persistencia al reconstruir un usuario desde la BD.
     *
     * @param hash hash previamente almacenado
     */
    public void setPasswordHash(String hash) { this.passwordHash = hash; }
}
