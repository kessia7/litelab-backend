import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

    public static Connection conectar() {
        try {

            String url = System.getenv("DATABASE_URL");

            if (url != null && !url.isBlank()) {

                // Converte a URL do Render para o formato JDBC
                if (url.startsWith("postgresql://")) {
                    url = "jdbc:" + url;
                }

                Class.forName("org.postgresql.Driver");

                Connection conexao = DriverManager.getConnection(url);

                System.out.println("Conectado ao banco do Render!");
                return conexao;

            } else {

                String urlLocal = "jdbc:postgresql://localhost:5432/litelab";
                String usuarioLocal = "postgres";
                String senhaLocal = "1234";

                Class.forName("org.postgresql.Driver");

                Connection conexao = DriverManager.getConnection(
                    urlLocal,
                    usuarioLocal,
                    senhaLocal
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