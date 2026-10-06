import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    public String cadastrarUsuario(Usuario usuario) {

        String verificarSql = "SELECT * FROM usuario WHERE email = ? OR nome_usuario = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement verificar = conexao.prepareStatement(verificarSql)) {

            verificar.setString(1, usuario.getEmail());
            verificar.setString(2, usuario.getNomeUsuario());

            ResultSet resultado = verificar.executeQuery();

            if (resultado.next()) {

                if (resultado.getString("email").equals(usuario.getEmail())) {
                    return "Este email já está cadastrado.";
                }

                if (resultado.getString("nome_usuario").equals(usuario.getNomeUsuario())) {
                    return "Este nome de usuário já está cadastrado.";
                }
            }

        } catch (SQLException e) {
            return "Erro ao verificar usuário: " + e.getMessage();
        }

        String sql = """
                INSERT INTO usuario (nome_usuario, nome_completo, email, senha)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, usuario.getNomeUsuario());
            comando.setString(2, usuario.getNomeCompleto());
            comando.setString(3, usuario.getEmail());
            comando.setString(4, usuario.getSenha());

            comando.executeUpdate();

            return "Usuário cadastrado com sucesso!";

        } catch (SQLException e) {
            return "Erro ao cadastrar usuário: " + e.getMessage();
        }
    }

    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT nome_usuario, nome_completo, email, senha FROM usuario";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                Usuario usuario = new Usuario(
                    resultado.getString("nome_usuario"),
                    resultado.getString("nome_completo"),
                    resultado.getString("email"),
                    resultado.getString("senha")
                );

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }

        return usuarios;
    }

    public String fazerLogin(String usuarioOuEmail, String senha) {

        String sql = """
                SELECT nome_usuario, nome_completo
                FROM usuario
                WHERE (nome_usuario = ? OR email = ?)
                AND senha = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, usuarioOuEmail);
            comando.setString(2, usuarioOuEmail);
            comando.setString(3, senha);

            ResultSet resultado = comando.executeQuery();

            if (resultado.next()) {

                return "Login realizado com sucesso! Bem-vindo(a), "
                        + resultado.getString("nome_usuario") + "!";
            }

            return "Usuário/email ou senha incorretos.";

        } catch (SQLException e) {

            return "Erro ao realizar login: " + e.getMessage();
        }
    }
}