public class Usuario {

    private String nomeUsuario;
    private String nomeCompleto;
    private String email;
    private String senha;

    public Usuario(String nomeUsuario, String nomeCompleto, String email, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}