
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

    public static Connection conectar() {
        try {
            String url = System.getenv("DATABASE_URL");

            if (url != null && !url.isBlank()) {

                URI uri = new URI(url);

                String host = uri.getHost();
                int porta = uri.getPort() == -1 ? 5432 : uri.getPort();
                String banco = uri.getPath().substring(1);

                String informacoes = uri.getRawUserInfo();
                String[] credenciais = informacoes.split(":", 2);

                String usuario = URLDecoder.decode(
                    credenciais[0], StandardCharsets.UTF_8
                );

                String senha = URLDecoder.decode(
                    credenciais[1], StandardCharsets.UTF_8
                );

                String parametros = uri.getRawQuery();

                if (parametros == null || parametros.isBlank()) {
                    parametros = "sslmode=require";
                } else if (!parametros.contains("sslmode=")) {
                    parametros += "&sslmode=require";
                }

                String urlJDBC = "jdbc:postgresql://" + host
                    + ":" + porta + "/" + banco + "?" + parametros;

                Class.forName("org.postgresql.Driver");

                Connection conexao = DriverManager.getConnection(
                    urlJDBC, usuario, senha
                );

                System.out.println("Conectado ao banco do Render!");
                return conexao;

            } else {
                Class.forName("org.postgresql.Driver");

                Connection conexao = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/litelab",
                    "postgres",
                    "1234"
                );

                System.out.println("Conectado ao PostgreSQL local!");
                return conexao;
            }

        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco:");
            e.printStackTrace();
            return null;
        }
    }
}