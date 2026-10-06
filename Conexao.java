import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection conectar() {

        try {

            String url = System.getenv("DATABASE_URL");

            if (url != null && !url.isBlank()) {

                Connection conexao = DriverManager.getConnection(url);

                System.out.println("Conectado ao banco do Render!");

                return conexao;

            } else {

                String urlLocal = "jdbc:postgresql://localhost:5432/litelab";
                String usuarioLocal = "postgres";
                String senhaLocal = "1234";

                Connection conexao = DriverManager.getConnection(
                    urlLocal,
                    usuarioLocal,
                    senhaLocal
                );

                System.out.println("Conectado ao PostgreSQL local!");

                return conexao;
            }

        } catch (SQLException e) {

            System.out.println("Erro ao conectar ao banco:");
            e.printStackTrace();

            return null;
        }
    }
}