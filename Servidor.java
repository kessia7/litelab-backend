import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Servidor {

    private static UsuarioService service = new UsuarioService();

    public static void iniciar() throws IOException {
        int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8081")
        );
        
        HttpServer servidor = HttpServer.create(
            new InetSocketAddress("0.0.0.0", port), 0
        );

        servidor.createContext("/cadastrarUsuario", Servidor::cadastrarUsuario);
        servidor.createContext("/login", Servidor::login);
        servidor.createContext("/perfil", Servidor::perfil);
        servidor.createContext("/salvarFoto", Servidor::salvarFoto);
        servidor.createContext("/favoritar", Servidor::favoritar);
        servidor.createContext("/favoritos", Servidor::favoritos);
        servidor.createContext("/desfavoritar", Servidor::desfavoritar);
        servidor.createContext("/estatisticas", Servidor::estatisticas);
        servidor.createContext("/cadastrarLeitura", Servidor::cadastrarLeitura);
        servidor.createContext("/leituras", Servidor::leituras);
        servidor.createContext("/criarClube", Servidor::criarClube);
        servidor.createContext("/clubes", Servidor::listarClubes);
        servidor.createContext("/buscarClube", Servidor::buscarClube);
        servidor.createContext("/atualizarClube", Servidor::atualizarClube);
        servidor.createContext("/excluirClube", Servidor::excluirClube);
        servidor.createContext("/mensagens", Servidor::mensagens);
        servidor.createContext("/enviarMensagem", Servidor::enviarMensagem);
        servidor.createContext("/entrarClube", Servidor::entrarClube);
        servidor.createContext("/clubesParticipados", Servidor::clubesParticipados);
        servidor.createContext("/eventos", Servidor::eventos);
        servidor.createContext("/criarEvento", Servidor::criarEvento);
        servidor.createContext("/editarEvento", Servidor::editarEvento);
        servidor.createContext("/excluirEvento", Servidor::excluirEvento);
        servidor.createContext("/sairClube", Servidor::sairClube);
        servidor.createContext("/cadastrarResenha", Servidor::cadastrarResenha);
        servidor.createContext("/resenhas", Servidor::listarResenhas);
        servidor.createContext("/curtirResenha", Servidor::curtirResenha);
        servidor.createContext("/cadastrarComentario", Servidor::cadastrarComentario);
        servidor.createContext("/listarComentarios", Servidor::listarComentarios);
        servidor.createContext("/livros", Servidor::listarLivros);
        servidor.createContext("/excluirResenha", Servidor::excluirResenha);
        servidor.createContext("/ranking", Servidor::ranking);
        servidor.createContext("/recuperarSenha", Servidor::recuperarSenha);

        servidor.createContext(
            "/notificacoes",
            Servidor::listarNotificacoes
        );
        
        servidor.createContext(
            "/marcarNotificacao",
            Servidor::marcarNotificacao
        );
       
        servidor.start();

        System.out.println("Servidor HTTP iniciado na porta " + port + "!");
        System.out.println("Aguardando cadastro pelo LiteLAB...");
    }

    private static void cadastrarUsuario(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS"
            );

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
            );

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        InputStream entrada = exchange.getRequestBody();

        String json = new String(
            entrada.readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("Dados recebidos:");
        System.out.println(json);

        String nomeUsuario = extrair(json, "nome_usuario");
        String nomeCompleto = extrair(json, "nome_completo");
        String email = extrair(json, "email");
        String senha = extrair(json, "senha");

        Usuario usuario = new Usuario(
            nomeUsuario,
            nomeCompleto,
            email,
            senha
        );

        String resultado = service.cadastrarUsuario(usuario);

        enviarResposta(
            exchange,
            resultado,
            200
        );
    }

    private static void login(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS"
            );

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
            );

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("Dados de login recebidos:");
        System.out.println(json);

        String usuario = extrair(json, "usuario");
        String senha = extrair(json, "senha");

        String resultado = service.fazerLogin(
            usuario,
            senha
        );

        enviarResposta(
            exchange,
            resultado,
            200
        );
    }

    private static void perfil(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, OPTIONS"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );
    
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
    
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
    
            return;
        }
    
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
    
            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );
    
            return;
        }
    
        String query =
            exchange.getRequestURI().getQuery();
    
        if (
            query == null ||
            !query.startsWith("usuario=")
        ) {
    
            enviarResposta(
                exchange,
                "Usuário não informado.",
                400
            );
    
            return;
        }
    
        String nomeUsuario =
            java.net.URLDecoder.decode(
                query.substring("usuario=".length()),
                StandardCharsets.UTF_8
            );
    
        String sql = """
            SELECT
                nome_usuario,
                nome_completo,
                email,
                foto_perfil
            FROM usuario
            WHERE nome_usuario = ?
            """;
    
        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {
    
            comando.setString(
                1,
                nomeUsuario
            );
    
            ResultSet resultado =
                comando.executeQuery();
    
            if (resultado.next()) {
    
                String nome =
                    resultado.getString("nome_usuario");
    
                String nomeCompleto =
                    resultado.getString("nome_completo");
    
                String email =
                    resultado.getString("email");
    
                String foto =
                    resultado.getString("foto_perfil");
    
                if (foto == null) {
                    foto = "";
                }
    
                String resposta =
                    "{"
                    + "\"nome_usuario\":\""
                    + escaparJson(nome)
                    + "\","
                    + "\"nome_completo\":\""
                    + escaparJson(nomeCompleto)
                    + "\","
                    + "\"email\":\""
                    + escaparJson(email)
                    + "\","
                    + "\"foto_perfil\":\""
                    + escaparJson(foto)
                    + "\""
                    + "}";
    
                exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                );
    
                byte[] dados =
                    resposta.getBytes(
                        StandardCharsets.UTF_8
                    );
    
                exchange.sendResponseHeaders(
                    200,
                    dados.length
                );
    
                OutputStream saida =
                    exchange.getResponseBody();
    
                saida.write(dados);
                saida.close();
    
            } else {
    
                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );
            }
    
        } catch (SQLException e) {
    
            e.printStackTrace();
    
            enviarResposta(
                exchange,
                "Erro ao buscar perfil: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void recuperarSenha(HttpExchange exchange) throws IOException {

        // CORS
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );
    
    
        // OPTIONS
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
    
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
    
            return;
        }
    
    
        // Só aceita POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
    
            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );
    
            return;
        }
    
    
        // Recebe o JSON
        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );
    
    
        System.out.println(
            "Dados de recuperação recebidos:"
        );
    
        System.out.println(json);
    
    
        String email = extrair(
            json,
            "email"
        );
    
        String novaSenha = extrair(
            json,
            "novaSenha"
        );
    
    
        // Verifica se os dados foram enviados
        if (
            email == null ||
            email.trim().isEmpty() ||
            novaSenha == null ||
            novaSenha.trim().isEmpty()
        ) {
    
            enviarResposta(
                exchange,
                "Preencha todos os campos.",
                400
            );
    
            return;
        }
    
    
        String sql = """
            UPDATE usuario
            SET senha = ?
            WHERE email = ?
            """;
    
    
        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {
    
            comando.setString(
                1,
                novaSenha
            );
    
            comando.setString(
                2,
                email
            );
    
    
            int alterados =
                comando.executeUpdate();
    
    
            if (alterados > 0) {
    
                enviarResposta(
                    exchange,
                    "Senha alterada com sucesso!",
                    200
                );
    
            } else {
    
                enviarResposta(
                    exchange,
                    "E-mail não encontrado.",
                    404
                );
            }
    
    
        } catch (SQLException erro) {
    
            erro.printStackTrace();
    
            enviarResposta(
                exchange,
                "Erro ao alterar a senha.",
                500
            );
        }
    }
    private static void salvarFoto(HttpExchange exchange)
            throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS"
            );

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
            );

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("Foto recebida:");
        System.out.println("Dados recebidos.");

        String nomeUsuario = extrair(
            json,
            "usuario"
        );

        String foto = extrair(
            json,
            "foto"
        );

        if (nomeUsuario.isEmpty() || foto.isEmpty()) {

            enviarResposta(
                exchange,
                "Usuário ou foto não informado.",
                400
            );

            return;
        }

        String sql = """
            UPDATE usuario
            SET foto_perfil = ?
            WHERE nome_usuario = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(
                1,
                foto
            );

            comando.setString(
                2,
                nomeUsuario
            );

            int linhasAlteradas =
                comando.executeUpdate();

            if (linhasAlteradas > 0) {

                enviarResposta(
                    exchange,
                    "Foto salva com sucesso!",
                    200
                );

            } else {

                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao salvar foto: " + e.getMessage(),
                500
            );
        }
    }

    private static void favoritar(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String usuario = extrair(
            json,
            "usuario"
        );

        String livroNome = extrair(
            json,
            "livro_nome"
        );

        String livroImagem = extrair(
            json,
            "livro_imagem"
        );

        if (usuario.isEmpty() || livroNome.isEmpty()) {

            enviarResposta(
                exchange,
                "Usuário ou livro não informado.",
                400
            );

            return;
        }

        String buscarUsuario = """
            SELECT id
            FROM usuario
            WHERE nome_usuario = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement busca =
                conexao.prepareStatement(buscarUsuario)
        ) {

            busca.setString(
                1,
                usuario
            );

            ResultSet resultado =
                busca.executeQuery();

            if (!resultado.next()) {

                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );

                return;
            }

            int usuarioId =
                resultado.getInt("id");

            String verificar = """
                SELECT id
                FROM favoritos
                WHERE usuario_id = ?
                AND livro_nome = ?
                """;

            try (
                PreparedStatement verifica =
                    conexao.prepareStatement(verificar)
            ) {

                verifica.setInt(
                    1,
                    usuarioId
                );

                verifica.setString(
                    2,
                    livroNome
                );

                ResultSet favoritoExistente =
                    verifica.executeQuery();

                if (favoritoExistente.next()) {

                    enviarResposta(
                        exchange,
                        "Este livro já está nos favoritos.",
                        200
                    );

                    return;
                }
            }

            String inserir = """
                INSERT INTO favoritos
                (usuario_id, livro_nome, livro_imagem)
                VALUES (?, ?, ?)
                """;

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(inserir)
            ) {

                comando.setInt(
                    1,
                    usuarioId
                );

                comando.setString(
                    2,
                    livroNome
                );

                comando.setString(
                    3,
                    livroImagem
                );

                comando.executeUpdate();

                enviarResposta(
                    exchange,
                    "Livro adicionado aos favoritos!",
                    200
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao favoritar livro: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void desfavoritar(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String usuario = extrair(
            json,
            "usuario"
        );

        String livroNome = extrair(
            json,
            "livro_nome"
        );

        if (usuario.isEmpty() || livroNome.isEmpty()) {

            enviarResposta(
                exchange,
                "Usuário ou livro não informado.",
                400
            );

            return;
        }

        String sql = """
            DELETE FROM favoritos
            WHERE usuario_id = (
                SELECT id
                FROM usuario
                WHERE nome_usuario = ?
            )
            AND livro_nome = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(
                1,
                usuario
            );

            comando.setString(
                2,
                livroNome
            );

            int removido =
                comando.executeUpdate();

            if (removido > 0) {

                enviarResposta(
                    exchange,
                    "Livro removido dos favoritos!",
                    200
                );

            } else {

                enviarResposta(
                    exchange,
                    "Este livro não está nos favoritos.",
                    200
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao remover favorito: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void favoritos(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String query =
            exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("usuario=")) {

            enviarResposta(
                exchange,
                "Usuário não informado.",
                400
            );

            return;
        }

        String usuario =
            query.substring("usuario=".length());

        String sql = """
            SELECT f.livro_nome, f.livro_imagem
            FROM favoritos f
            INNER JOIN usuario u
            ON f.usuario_id = u.id
            WHERE u.nome_usuario = ?
            """;

        StringBuilder json =
            new StringBuilder();

        json.append("[");

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(
                1,
                usuario
            );

            ResultSet resultado =
                comando.executeQuery();

            boolean primeiro = true;

            while (resultado.next()) {

                if (!primeiro) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"livro_nome\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "livro_nome"
                            )
                        )
                    )
                    .append("\"");

                json.append(",");

                json.append("\"livro_imagem\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "livro_imagem"
                            )
                        )
                    )
                    .append("\"");

                json.append("}");

                primeiro = false;
            }

            json.append("]");

            exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
            );

            enviarResposta(
                exchange,
                json.toString(),
                200
            );

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao buscar favoritos: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void estatisticas(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String query =
            exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("usuario=")) {

            enviarResposta(
                exchange,
                "Usuário não informado.",
                400
            );

            return;
        }

        String nomeUsuario =
            query.substring("usuario=".length());

        String sql = """
            SELECT
                COUNT(*) FILTER (
                    WHERE l.status = 'em_leitura'
                ) AS em_leitura,

                COUNT(*) FILTER (
                    WHERE l.status = 'concluido'
                ) AS concluidos,

                COALESCE(
                    SUM(l.paginas_lidas),
                    0
                ) AS paginas_lidas

            FROM leituras l

            INNER JOIN usuario u
                ON l.usuario_id = u.id

            WHERE u.nome_usuario = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(
                1,
                nomeUsuario
            );

            ResultSet resultado =
                comando.executeQuery();

            if (resultado.next()) {

                int emLeitura =
                    resultado.getInt("em_leitura");

                int concluidos =
                    resultado.getInt("concluidos");

                int paginasLidas =
                    resultado.getInt("paginas_lidas");

                String resposta =
                    "{"
                    + "\"em_leitura\":"
                    + emLeitura
                    + ","
                    + "\"concluidos\":"
                    + concluidos
                    + ","
                    + "\"paginas_lidas\":"
                    + paginasLidas
                    + "}";

                exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                );

                byte[] dados =
                    resposta.getBytes(
                        StandardCharsets.UTF_8
                    );

                exchange.sendResponseHeaders(
                    200,
                    dados.length
                );

                OutputStream saida =
                    exchange.getResponseBody();

                saida.write(dados);
                saida.close();

            } else {

                enviarResposta(
                    exchange,
                    "Nenhuma estatística encontrada.",
                    404
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao buscar estatísticas: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void cadastrarLeitura(
            HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("Cadastro de leitura recebido:");
        System.out.println(json);

        String usuario =
            extrair(json, "usuario");

        String livroNome =
            extrair(json, "livro_nome");

        String status =
            extrair(json, "status");

        String paginasTexto =
            extrair(json, "paginas");

        if (
            usuario.isEmpty() ||
            livroNome.isEmpty() ||
            status.isEmpty() ||
            paginasTexto.isEmpty()
        ) {

            enviarResposta(
                exchange,
                "Dados obrigatórios não informados.",
                400
            );

            return;
        }

        if (
            !status.equals("em_leitura") &&
            !status.equals("concluido")
        ) {

            enviarResposta(
                exchange,
                "Status de leitura inválido.",
                400
            );

            return;
        }

        int paginas;

        try {

            paginas =
                Integer.parseInt(paginasTexto);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "Quantidade de páginas inválida.",
                400
            );

            return;
        }

        if (paginas < 0) {

            enviarResposta(
                exchange,
                "A quantidade de páginas não pode ser negativa.",
                400
            );

            return;
        }

        String buscarUsuario = """
            SELECT id
            FROM usuario
            WHERE nome_usuario = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement busca =
                conexao.prepareStatement(buscarUsuario)
        ) {

            busca.setString(
                1,
                usuario
            );

            ResultSet resultado =
                busca.executeQuery();

            if (!resultado.next()) {

                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );

                return;
            }

            int usuarioId =
                resultado.getInt("id");

            String verificar = """
                SELECT id
                FROM leituras
                WHERE usuario_id = ?
                AND livro_nome = ?
                """;

            try (
                PreparedStatement verifica =
                    conexao.prepareStatement(verificar)
            ) {

                verifica.setInt(
                    1,
                    usuarioId
                );

                verifica.setString(
                    2,
                    livroNome
                );

                ResultSet leituraExistente =
                    verifica.executeQuery();

                if (leituraExistente.next()) {

                    String atualizar = """
                        UPDATE leituras
                        SET status = ?,
                            paginas_lidas = ?
                        WHERE usuario_id = ?
                        AND livro_nome = ?
                        """;

                    try (
                        PreparedStatement comando =
                            conexao.prepareStatement(atualizar)
                    ) {

                        comando.setString(
                            1,
                            status
                        );

                        comando.setInt(
                            2,
                            paginas
                        );

                        comando.setInt(
                            3,
                            usuarioId
                        );

                        comando.setString(
                            4,
                            livroNome
                        );

                        comando.executeUpdate();

                        verificarConquistas(
                            conexao,
                            usuarioId
                        );
                        
                        enviarResposta(
                            exchange,
                            "Leitura atualizada com sucesso!",
                            200
                        );
                        
                        return;
                    }
                }
            }

            String inserir = """
                INSERT INTO leituras
                (
                    usuario_id,
                    livro_nome,
                    status,
                    paginas_lidas
                )
                VALUES (?, ?, ?, ?)
                """;

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(inserir)
            ) {

                comando.setInt(
                    1,
                    usuarioId
                );

                comando.setString(
                    2,
                    livroNome
                );

                comando.setString(
                    3,
                    status
                );

                comando.setInt(
                    4,
                    paginas
                );

                comando.executeUpdate();

                comando.executeUpdate();

                verificarConquistas(
                    conexao,
                    usuarioId
                );
                
                enviarResposta(
                    exchange,
                    "Livro cadastrado na leitura com sucesso!",
                    200
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao cadastrar leitura: "
                + e.getMessage(),
                500
            );
        }
    }
    private static String extrair(String json, String campo) {

        String busca = "\"" + campo + "\":";
    
        int inicio = json.indexOf(busca);
    
        if (inicio == -1) {
            return "";
        }
    
        inicio += busca.length();
    
        // Remove espaços depois dos dois pontos
        while (inicio < json.length() &&
               Character.isWhitespace(json.charAt(inicio))) {
            inicio++;
        }
    
        // Se o valor estiver entre aspas
        if (inicio < json.length() && json.charAt(inicio) == '"') {
    
            inicio++;
    
            int fim = json.indexOf("\"", inicio);
    
            if (fim == -1) {
                return "";
            }
    
            return json.substring(inicio, fim);
        }
    
        // Se o valor for número
        int fim = inicio;
    
        while (fim < json.length() &&
               json.charAt(fim) != ',' &&
               json.charAt(fim) != '}') {
            fim++;
        }
    
        return json.substring(inicio, fim).trim();
    }
    private static String escaparJson(
            String texto) {

        if (texto == null) {
            return "";
        }

        return texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }

    private static void enviarResposta(
            HttpExchange exchange,
            String resposta,
            int codigo)
            throws IOException {

        byte[] dados =
            resposta.getBytes(
                StandardCharsets.UTF_8
            );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Content-Type",
            "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
            codigo,
            dados.length
        );

        OutputStream saida =
            exchange.getResponseBody();

        saida.write(dados);
        saida.close();
    }

    private static void leituras(HttpExchange exchange)
    throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String query =
            exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("usuario=")) {

            enviarResposta(
                exchange,
                "Usuário não informado.",
                400
            );

            return;
        }

        String nomeUsuario =
            query.substring("usuario=".length());

        String sql = """
            SELECT
                l.livro_nome,
                l.status,
                l.paginas_lidas
            FROM leituras l
            INNER JOIN usuario u
                ON l.usuario_id = u.id
            WHERE u.nome_usuario = ?
            ORDER BY l.id DESC
            """;

        StringBuilder json =
            new StringBuilder();

        json.append("[");

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(
                1,
                nomeUsuario
            );

            ResultSet resultado =
                comando.executeQuery();

            boolean primeiro = true;

            while (resultado.next()) {

                if (!primeiro) {
                    json.append(",");
                }

                String livroNome =
                    resultado.getString("livro_nome");

                String status =
                    resultado.getString("status");

                int paginas =
                    resultado.getInt("paginas_lidas");

                json.append("{");

                json.append("\"livro_nome\":\"")
                    .append(
                        escaparJson(livroNome)
                    )
                    .append("\"");

                json.append(",");

                json.append("\"status\":\"")
                    .append(
                        escaparJson(status)
                    )
                    .append("\"");

                json.append(",");

                json.append("\"paginas_lidas\":")
                    .append(paginas);

                json.append("}");

                primeiro = false;
            }

            json.append("]");

            exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
            );

            byte[] dados =
                json.toString().getBytes(
                    StandardCharsets.UTF_8
                );

            exchange.sendResponseHeaders(
                200,
                dados.length
            );

            OutputStream saida =
                exchange.getResponseBody();

            saida.write(dados);
            saida.close();

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao buscar leituras: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void criarClube(HttpExchange exchange)
    throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("Cadastro de clube recebido:");
        System.out.println(json);

        String nome =
            extrair(json, "nome");

        String presidente =
            extrair(json, "presidente");

        String maxMembrosTexto =
            extrair(json, "max_membros");

        String descricao =
            extrair(json, "descricao");

        String generoPrincipal =
            extrair(json, "genero_principal");

        String generoSecundario =
            extrair(json, "genero_secundario");

        String diaReuniao =
            extrair(json, "dia_reuniao");

        String horarioReuniao =
            extrair(json, "horario_reuniao");

        String frequencia =
            extrair(json, "frequencia");

        String usuario =
            extrair(json, "usuario");

        if (
            nome.isEmpty() ||
            presidente.isEmpty() ||
            maxMembrosTexto.isEmpty() ||
            generoPrincipal.isEmpty() ||
            diaReuniao.isEmpty() ||
            horarioReuniao.isEmpty() ||
            frequencia.isEmpty()
        ) {

            enviarResposta(
                exchange,
                "Preencha todos os campos obrigatórios.",
                400
            );

            return;
        }

        int maxMembros;

        try {

            maxMembros =
                Integer.parseInt(maxMembrosTexto);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "Quantidade de membros inválida.",
                400
            );

            return;
        }

        String buscarUsuario = """
            SELECT id
            FROM usuario
            WHERE nome_usuario = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement busca =
                conexao.prepareStatement(buscarUsuario)
        ) {

            busca.setString(1, usuario);

            ResultSet resultado =
                busca.executeQuery();

            int usuarioId = 0;

            if (resultado.next()) {

                usuarioId =
                    resultado.getInt("id");

            } else {

                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );

                return;
            }

            String sql = """
                INSERT INTO clubes
                (
                    nome,
                    presidente,
                    max_membros,
                    descricao,
                    genero_principal,
                    genero_secundario,
                    dia_reuniao,
                    horario_reuniao,
                    frequencia,
                    criado_por
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(sql)
            ) {

                comando.setString(1, nome);
                comando.setString(2, presidente);
                comando.setInt(3, maxMembros);
                comando.setString(4, descricao);
                comando.setString(5, generoPrincipal);
                comando.setString(6, generoSecundario);
                comando.setString(7, diaReuniao);
                comando.setString(8, horarioReuniao);
                comando.setString(9, frequencia);
                comando.setInt(10, usuarioId);

                comando.executeUpdate();

                enviarResposta(
                    exchange,
                    "Clube criado com sucesso!",
                    200
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao criar clube: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void listarClubes(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String query =
            exchange.getRequestURI().getQuery();

        String nomeUsuario = "";

        if (query != null) {

            for (String parte : query.split("&")) {

                String[] campo =
                    parte.split("=", 2);

                if (
                    campo.length == 2 &&
                    campo[0].equals("usuario")
                ) {

                    nomeUsuario = campo[1];

                    break;
                }
            }
        }

        String sql;

        if (nomeUsuario.isEmpty()) {

            sql = """
                SELECT
                    c.id,
                    c.nome,
                    c.presidente,
                    c.max_membros,
                    c.descricao,
                    c.genero_principal,
                    c.genero_secundario,
                    c.dia_reuniao,
                    c.horario_reuniao,
                    c.frequencia,
                    u.nome_usuario AS criador
                FROM clubes c
                INNER JOIN usuario u
                    ON c.criado_por = u.id
                ORDER BY c.id DESC
                """;

        } else {

            sql = """
                SELECT
                    c.id,
                    c.nome,
                    c.presidente,
                    c.max_membros,
                    c.descricao,
                    c.genero_principal,
                    c.genero_secundario,
                    c.dia_reuniao,
                    c.horario_reuniao,
                    c.frequencia,
                    u.nome_usuario AS criador
                FROM clubes c
                INNER JOIN usuario u
                    ON c.criado_por = u.id
                WHERE u.nome_usuario = ?
                ORDER BY c.id DESC
                """;
        }

        StringBuilder json =
            new StringBuilder("[");

        boolean primeiro = true;

        try (
            Connection conexao = Conexao.conectar();

            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            if (!nomeUsuario.isEmpty()) {

                comando.setString(
                    1,
                    nomeUsuario
                );
            }

            ResultSet resultado =
                comando.executeQuery();

            while (resultado.next()) {

                if (!primeiro) {

                    json.append(",");

                }

                json.append("{");

                json.append("\"id\":")
                    .append(
                        resultado.getInt("id")
                    )
                    .append(",");

                json.append("\"nome\":\"")
                    .append(
                        escaparJson(
                            resultado.getString("nome")
                        )
                    )
                    .append("\",");

                json.append("\"presidente\":\"")
                    .append(
                        escaparJson(
                            resultado.getString("presidente")
                        )
                    )
                    .append("\",");

                json.append("\"max_membros\":")
                    .append(
                        resultado.getInt("max_membros")
                    )
                    .append(",");

                json.append("\"descricao\":\"")
                    .append(
                        escaparJson(
                            resultado.getString("descricao")
                        )
                    )
                    .append("\",");

                json.append("\"genero_principal\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "genero_principal"
                            )
                        )
                    )
                    .append("\",");

                json.append("\"genero_secundario\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "genero_secundario"
                            )
                        )
                    )
                    .append("\",");

                json.append("\"dia_reuniao\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "dia_reuniao"
                            )
                        )
                    )
                    .append("\",");

                json.append("\"horario_reuniao\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "horario_reuniao"
                            )
                        )
                    )
                    .append("\",");

                    json.append("\"frequencia\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "frequencia"
                            )
                        )
                    )
                    .append("\",");
                
                json.append("\"criador\":\"")
                    .append(
                        escaparJson(
                            resultado.getString(
                                "criador"
                            )
                        )
                    )
                    .append("\"");
                
                json.append("}");

                primeiro = false;
            }

            json.append("]");

            enviarResposta(
                exchange,
                json.toString(),
                200
            );

        } catch (SQLException e) {

            e.printStackTrace();

            enviarResposta(
                exchange,
                "Erro ao listar clubes: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void mensagens(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            enviarResposta(exchange, "Método não permitido.", 405);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String clubeIdTexto = "";

        if (query != null) {
            for (String parte : query.split("&")) {
                String[] campo = parte.split("=", 2);

                if (campo.length == 2 && campo[0].equals("id")) {
                    clubeIdTexto = campo[1];
                    break;
                }
            }
        }

        if (clubeIdTexto.isEmpty()) {
            enviarResposta(exchange, "Clube não informado.", 400);
            return;
        }

        int clubeId;

        try {
            clubeId = Integer.parseInt(clubeIdTexto);
        } catch (NumberFormatException e) {
            enviarResposta(exchange, "ID do clube inválido.", 400);
            return;
        }

        String sql = """
            SELECT m.mensagem, u.nome_usuario
            FROM mensagens_clube m
            INNER JOIN usuario u
                ON m.usuario_id = u.id
            WHERE m.clube_id = ?
            ORDER BY m.data_envio ASC
            """;

        StringBuilder json = new StringBuilder("[");
        boolean primeiro = true;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, clubeId);

            ResultSet resultado = comando.executeQuery();

            while (resultado.next()) {

                if (!primeiro) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"usuario\":\"")
                    .append(escaparJson(
                        resultado.getString("nome_usuario")
                    ))
                    .append("\",");

                json.append("\"mensagem\":\"")
                    .append(escaparJson(
                        resultado.getString("mensagem")
                    ))
                    .append("\"");

                json.append("}");

                primeiro = false;
            }

            json.append("]");

            enviarResposta(
                exchange,
                json.toString(),
                200
            );

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao carregar mensagens: " + e.getMessage(),
                500
            );
        }
    }

    private static void enviarMensagem(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            enviarResposta(exchange, "Método não permitido.", 405);
            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String clubeIdTexto = extrair(json, "clube_id");
        String usuario = extrair(json, "usuario");
        String mensagem = extrair(json, "mensagem");

        if (clubeIdTexto.isEmpty() ||
            usuario.isEmpty() ||
            mensagem.isEmpty()) {

            enviarResposta(
                exchange,
                "Dados da mensagem incompletos.",
                400
            );

            return;
        }

        int clubeId;

        try {
            clubeId = Integer.parseInt(clubeIdTexto);
        } catch (NumberFormatException e) {
            enviarResposta(exchange, "ID do clube inválido.", 400);
            return;
        }

        String buscarUsuario = """
            SELECT id
            FROM usuario
            WHERE nome_usuario = ?
            """;

        String sql = """
            INSERT INTO mensagens_clube
            (clube_id, usuario_id, mensagem)
            VALUES (?, ?, ?)
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement busca =
                conexao.prepareStatement(buscarUsuario)
        ) {

            busca.setString(1, usuario);

            ResultSet resultado = busca.executeQuery();

            if (!resultado.next()) {
                enviarResposta(exchange, "Usuário não encontrado.", 404);
                return;
            }

            int usuarioId = resultado.getInt("id");

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(sql)
            ) {

                comando.setInt(1, clubeId);
                comando.setInt(2, usuarioId);
                comando.setString(3, mensagem);

                comando.executeUpdate();
            }

            enviarResposta(
                exchange,
                "Mensagem enviada com sucesso!",
                200
            );

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao enviar mensagem: " + e.getMessage(),
                500
            );
        }
    }

    private static void entrarClube(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            enviarResposta(exchange, "Método não permitido.", 405);
            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String clubeIdTexto = extrair(json, "clube_id");
        String usuario = extrair(json, "usuario");

        if (clubeIdTexto.isEmpty() || usuario.isEmpty()) {
            enviarResposta(
                exchange,
                "Dados incompletos.",
                400
            );
            return;
        }

        int clubeId;

        try {
            clubeId = Integer.parseInt(clubeIdTexto);
        } catch (NumberFormatException e) {
            enviarResposta(
                exchange,
                "ID do clube inválido.",
                400
            );
            return;
        }

        String sql = """
            INSERT INTO membros_clube (clube_id, usuario_id)
            SELECT ?, id
            FROM usuario
            WHERE nome_usuario = ?
            ON CONFLICT (clube_id, usuario_id) DO NOTHING
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, clubeId);
            comando.setString(2, usuario);

            int linhas = comando.executeUpdate();

            if (linhas > 0) {
                enviarResposta(
                    exchange,
                    "Usuário entrou no clube!",
                    200
                );
            } else {
                enviarResposta(
                    exchange,
                    "Usuário já participa deste clube ou não foi encontrado.",
                    200
                );
            }

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao entrar no clube: " + e.getMessage(),
                500
            );
        }
    }

    private static void sairClube(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String clubeIdTexto =
            extrair(json, "clube_id");

        String usuario =
            extrair(json, "usuario");

        if (
            clubeIdTexto.isEmpty() ||
            usuario.isEmpty()
        ) {

            enviarResposta(
                exchange,
                "Dados incompletos.",
                400
            );

            return;
        }

        int clubeId;

        try {

            clubeId =
                Integer.parseInt(clubeIdTexto);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "ID do clube inválido.",
                400
            );

            return;
        }

        String sql = """
            DELETE FROM membros_clube
            WHERE clube_id = ?
            AND usuario_id = (
                SELECT id
                FROM usuario
                WHERE nome_usuario = ?
            )
            """;

        try (
            Connection conexao =
                Conexao.conectar();

            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, clubeId);

            comando.setString(
                2,
                usuario
            );

            int linhas =
                comando.executeUpdate();

            if (linhas > 0) {

                enviarResposta(
                    exchange,
                    "Usuário saiu do clube!",
                    200
                );

            } else {

                enviarResposta(
                    exchange,
                    "Usuário não participa deste clube.",
                    200
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            enviarResposta(
                exchange,
                "Erro ao sair do clube: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void clubesParticipados(HttpExchange exchange)
    throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );
            return;
        }

        String query =
            exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("usuario=")) {
            enviarResposta(
                exchange,
                "Usuário não informado.",
                400
            );
            return;
        }

        String nomeUsuario =
            query.substring("usuario=".length());

        String sql = """
            SELECT c.id, c.nome
            FROM membros_clube m
            INNER JOIN usuario u
                ON m.usuario_id = u.id
            INNER JOIN clubes c
                ON m.clube_id = c.id
            WHERE u.nome_usuario = ?
            ORDER BY c.id DESC
            """;

        StringBuilder json =
            new StringBuilder("[");

        boolean primeiro = true;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(
                1,
                nomeUsuario
            );

            ResultSet resultado =
                comando.executeQuery();

            while (resultado.next()) {

                if (!primeiro) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"id\":")
                    .append(resultado.getInt("id"))
                    .append(",");

                json.append("\"nome\":\"")
                    .append(
                        escaparJson(
                            resultado.getString("nome")
                        )
                    )
                    .append("\"");

                json.append("}");

                primeiro = false;
            }

            json.append("]");

            enviarResposta(
                exchange,
                json.toString(),
                200
            );

        } catch (SQLException e) {

            enviarResposta(
                exchange,
                "Erro ao buscar clubes: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void eventos(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            enviarResposta(exchange, "Método não permitido.", 405);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String clubeIdTexto = "";

        if (query != null) {
            for (String parte : query.split("&")) {

                String[] campo = parte.split("=", 2);

                if (campo.length == 2 && campo[0].equals("clube_id")) {
                    clubeIdTexto = campo[1];
                    break;
                }
            }
        }

        if (clubeIdTexto.isEmpty()) {
            enviarResposta(exchange, "Clube não informado.", 400);
            return;
        }

        String sql = """
            SELECT id, nome_evento, data_evento
            FROM eventos_clube
            WHERE clube_id = ?
            ORDER BY data_evento ASC
            """;

        StringBuilder json = new StringBuilder("[");

        boolean primeiro = true;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, Integer.parseInt(clubeIdTexto));

            ResultSet resultado = comando.executeQuery();

            while (resultado.next()) {

                if (!primeiro) {
                    json.append(",");
                }

                json.append("{");

                json.append("\"id\":")
                    .append(resultado.getInt("id"))
                    .append(",");

                json.append("\"nome_evento\":\"")
                    .append(escaparJson(resultado.getString("nome_evento")))
                    .append("\",");

                json.append("\"data_evento\":\"")
                    .append(resultado.getDate("data_evento"))
                    .append("\"");

                json.append("}");

                primeiro = false;
            }

            json.append("]");

            enviarResposta(exchange, json.toString(), 200);

        } catch (Exception e) {

            enviarResposta(
                exchange,
                "Erro ao buscar eventos: " + e.getMessage(),
                500
            );
        }
    }

    private static void editarEvento(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String corpo = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("DADOS DA EDIÇÃO:");
        System.out.println(corpo);

        String eventoId = extrair(corpo, "id");
        String nomeEvento = extrair(corpo, "nome_evento");
        String dataEvento = extrair(corpo, "data_evento");

        if (eventoId.isEmpty() ||
            nomeEvento.isEmpty() ||
            dataEvento.isEmpty()) {

            enviarResposta(
                exchange,
                "Dados incompletos.",
                400
            );

            return;
        }

        int id;

        try {

            id = Integer.parseInt(eventoId);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "ID do evento inválido.",
                400
            );

            return;
        }

        java.sql.Date data;

        try {

            data = java.sql.Date.valueOf(dataEvento);

        } catch (IllegalArgumentException e) {

            enviarResposta(
                exchange,
                "Data inválida.",
                400
            );

            return;
        }

        String sql = """
            UPDATE eventos_clube
            SET nome_evento = ?,
                data_evento = ?
            WHERE id = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(1, nomeEvento);
            comando.setDate(2, data);
            comando.setInt(3, id);

            int alterados = comando.executeUpdate();

            if (alterados == 0) {

                enviarResposta(
                    exchange,
                    "Evento não encontrado.",
                    404
                );

                return;
            }

            enviarResposta(
                exchange,
                "Evento editado com sucesso!",
                200
            );

        } catch (SQLException e) {

            e.printStackTrace();

            enviarResposta(
                exchange,
                "Erro ao editar evento: " + e.getMessage(),
                500
            );
        }
    }

    private static void excluirEvento(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String corpo = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("DADOS DA EXCLUSÃO:");
        System.out.println(corpo);

        String eventoId = extrair(corpo, "id");

        if (eventoId.isEmpty()) {

            enviarResposta(
                exchange,
                "ID do evento não informado.",
                400
            );

            return;
        }

        int id;

        try {

            id = Integer.parseInt(eventoId);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "ID do evento inválido.",
                400
            );

            return;
        }

        String sql = """
            DELETE FROM eventos_clube
            WHERE id = ?
            """;

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, id);

            int excluidos = comando.executeUpdate();

            if (excluidos == 0) {

                enviarResposta(
                    exchange,
                    "Evento não encontrado.",
                    404
                );

                return;
            }

            enviarResposta(
                exchange,
                "Evento excluído com sucesso!",
                200
            );

        } catch (SQLException e) {

            e.printStackTrace();

            enviarResposta(
                exchange,
                "Erro ao excluir evento: " + e.getMessage(),
                500
            );
        }
    }

    private static void criarEvento(HttpExchange exchange)
        throws IOException {

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin",
        "*"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Methods",
        "POST, OPTIONS"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Headers",
        "Content-Type"
    );

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

        enviarResposta(
            exchange,
            "Método não permitido.",
            405
        );

        return;
    }

    String corpo = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    System.out.println("DADOS DO EVENTO RECEBIDOS:");
    System.out.println(corpo);

    String clubeId = extrair(corpo, "clube_id");
    String nomeEvento = extrair(corpo, "nome_evento");
    String dataEvento = extrair(corpo, "data_evento");

    System.out.println("CLUBE ID: " + clubeId);
    System.out.println("NOME EVENTO: " + nomeEvento);
    System.out.println("DATA EVENTO: " + dataEvento);

    if (
        clubeId.isEmpty() ||
        nomeEvento.isEmpty() ||
        dataEvento.isEmpty()
    ) {

        enviarResposta(
            exchange,
            "Dados incompletos: clube, evento ou data não informado.",
            400
        );

        return;
    }

    int idClube;

    try {

        idClube = Integer.parseInt(clubeId);

    } catch (NumberFormatException e) {

        enviarResposta(
            exchange,
            "ID do clube inválido: " + clubeId,
            400
        );

        return;
    }

    java.sql.Date data;

    try {

        data = java.sql.Date.valueOf(dataEvento);

    } catch (IllegalArgumentException e) {

        enviarResposta(
            exchange,
            "Data inválida. Use AAAA-MM-DD. Recebido: "
            + dataEvento,
            400
        );

        return;
    }


    /*
     * =====================================================
     * 1. SALVA O EVENTO NO CALENDÁRIO
     * =====================================================
     */

    String sqlEvento = """
        INSERT INTO eventos_clube
        (clube_id, nome_evento, data_evento)
        VALUES (?, ?, ?)
        """;


    /*
     * =====================================================
     * 2. BUSCA O NOME DO CLUBE
     * =====================================================
     */

    String nomeClube = "seu clube";

    String sqlClube = """
        SELECT nome
        FROM clubes
        WHERE id = ?
        """;


    /*
     * =====================================================
     * 3. BUSCA TODOS OS MEMBROS DO CLUBE
     * =====================================================
     */

    String sqlMembros = """
        SELECT usuario_id
        FROM membros_clube
        WHERE clube_id = ?
        """;


    /*
     * =====================================================
     * 4. CRIA A NOTIFICAÇÃO
     * =====================================================
     */

    String sqlNotificacao = """
        INSERT INTO notificacoes
        (
            usuario_id,
            tipo,
            codigo,
            titulo,
            mensagem
        )
        VALUES (?, 'EVENTO_CLUBE', ?, ?, ?)
        ON CONFLICT
        (
            usuario_id,
            tipo,
            codigo
        )
        DO NOTHING
        """;


    try (
        Connection conexao = Conexao.conectar()
    ) {

        /*
         * -------------------------------------------------
         * SALVA O EVENTO
         * -------------------------------------------------
         */

        try (
            PreparedStatement comandoEvento =
                conexao.prepareStatement(sqlEvento)
        ) {

            comandoEvento.setInt(
                1,
                idClube
            );

            comandoEvento.setString(
                2,
                nomeEvento
            );

            comandoEvento.setDate(
                3,
                data
            );

            comandoEvento.executeUpdate();
        }


        /*
         * -------------------------------------------------
         * PEGA O NOME DO CLUBE
         * -------------------------------------------------
         */

        try (
            PreparedStatement comandoClube =
                conexao.prepareStatement(sqlClube)
        ) {

            comandoClube.setInt(
                1,
                idClube
            );

            try (
                ResultSet resultado =
                    comandoClube.executeQuery()
            ) {

                if (resultado.next()) {

                    nomeClube =
                        resultado.getString("nome");
                }
            }
        }


        /*
         * -------------------------------------------------
         * ENVIA NOTIFICAÇÃO PARA CADA MEMBRO
         * -------------------------------------------------
         */

        try (
            PreparedStatement comandoMembros =
                conexao.prepareStatement(sqlMembros);

            PreparedStatement comandoNotificacao =
                conexao.prepareStatement(sqlNotificacao)
        ) {

            comandoMembros.setInt(
                1,
                idClube
            );

            try (
                ResultSet resultado =
                    comandoMembros.executeQuery()
            ) {

                while (resultado.next()) {

                    int usuarioId =
                        resultado.getInt("usuario_id");


                    String codigo =
                        "evento-" +
                        idClube +
                        "-" +
                        nomeEvento +
                        "-" +
                        dataEvento;


                    String titulo =
                        "📅 Novo evento no clube";


                    String mensagem =
                        "O clube \"" +
                        nomeClube +
                        "\" tem o evento \"" +
                        nomeEvento +
                        "\" marcado para " +
                        dataEvento +
                        ".";


                    comandoNotificacao.setInt(
                        1,
                        usuarioId
                    );

                    comandoNotificacao.setString(
                        2,
                        codigo
                    );

                    comandoNotificacao.setString(
                        3,
                        titulo
                    );

                    comandoNotificacao.setString(
                        4,
                        mensagem
                    );

                    comandoNotificacao.executeUpdate();
                }
            }
        }


        /*
         * -------------------------------------------------
         * RESPOSTA
         * -------------------------------------------------
         */

        enviarResposta(
            exchange,
            "Evento criado e notificações enviadas aos membros do clube!",
            200
        );


    } catch (SQLException e) {

        e.printStackTrace();

        enviarResposta(
            exchange,
            "Erro no banco ao criar evento: "
            + e.getMessage(),
            500
        );
    }
}
    private static void listarResenhas(HttpExchange exchange)
    throws IOException {

exchange.getResponseHeaders().set(
    "Access-Control-Allow-Origin",
    "*"
);

exchange.getResponseHeaders().set(
    "Access-Control-Allow-Methods",
    "GET, OPTIONS"
);

exchange.getResponseHeaders().set(
    "Access-Control-Allow-Headers",
    "Content-Type"
);

if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

    exchange.sendResponseHeaders(204, -1);
    exchange.close();

    return;
}

if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

    enviarResposta(
        exchange,
        "Método não permitido.",
        405
    );

    return;
}

String query =
    exchange.getRequestURI().getQuery();

if (
    query == null ||
    !query.startsWith("usuario=")
) {

    enviarResposta(
        exchange,
        "Usuário não informado.",
        400
    );

    return;
}

String usuario =
    java.net.URLDecoder.decode(
        query.substring("usuario=".length()),
        StandardCharsets.UTF_8
    );

String sql = """
    SELECT
        r.id,
        r.nome_livro,
        r.autor,
        r.nota,
        r.texto,
        u.nome_usuario,

        (
            SELECT COUNT(*)
            FROM curtidas_resenha cr
            WHERE cr.resenha_id = r.id
        ) AS curtidas,

        EXISTS (
            SELECT 1
            FROM curtidas_resenha cr2
            INNER JOIN usuario u2
                ON cr2.usuario_id = u2.id
            WHERE cr2.resenha_id = r.id
            AND u2.nome_usuario = ?
        ) AS curtido

    FROM resenhas r

    INNER JOIN usuario u
        ON r.usuario_id = u.id

    ORDER BY r.id DESC
    """;

StringBuilder json =
    new StringBuilder("[");

boolean primeiro = true;

try (
    Connection conexao = Conexao.conectar();

    PreparedStatement comando =
        conexao.prepareStatement(sql)
) {

    comando.setString(
        1,
        usuario
    );

    ResultSet resultado =
        comando.executeQuery();

    while (resultado.next()) {

        if (!primeiro) {

            json.append(",");

        }

        json.append("{");

        json.append("\"id\":")
            .append(
                resultado.getInt("id")
            )
            .append(",");

        json.append("\"livro\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "nome_livro"
                    )
                )
            )
            .append("\",");

        json.append("\"autor\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "autor"
                    )
                )
            )
            .append("\",");

        json.append("\"nota\":")
            .append(
                resultado.getDouble("nota")
            )
            .append(",");

        json.append("\"curtidas\":")
            .append(
                resultado.getInt("curtidas")
            )
            .append(",");

        json.append("\"curtido\":")
            .append(
                resultado.getBoolean("curtido")
            )
            .append(",");

        json.append("\"texto\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "texto"
                    )
                )
            )
            .append("\",");

        json.append("\"usuario\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "nome_usuario"
                    )
                )
            )
            .append("\"");

        json.append("}");

        primeiro = false;
    }

    json.append("]");

    exchange.getResponseHeaders().set(
        "Content-Type",
        "application/json; charset=UTF-8"
    );

    byte[] dados =
        json.toString().getBytes(
            StandardCharsets.UTF_8
        );

    exchange.sendResponseHeaders(
        200,
        dados.length
    );

    OutputStream saida =
        exchange.getResponseBody();

    saida.write(dados);
    saida.close();

} catch (SQLException e) {

    e.printStackTrace();

    enviarResposta(
        exchange,
        "Erro ao listar resenhas: "
        + e.getMessage(),
        500
    );
}
}
    private static void cadastrarResenha(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        System.out.println("Resenha recebida:");
        System.out.println(json);

        String livro = extrair(json, "livro");
        String autor = extrair(json, "autor");
        String notaTexto = extrair(json, "nota");
        String texto = extrair(json, "texto");
        String usuario = extrair(json, "usuario");

        if (
            livro.isEmpty() ||
            autor.isEmpty() ||
            notaTexto.isEmpty() ||
            texto.isEmpty() ||
            usuario.isEmpty()
        ) {

            enviarResposta(
                exchange,
                "Dados da resenha incompletos.",
                400
            );

            return;
        }

        double nota;

        try {

            nota = Double.parseDouble(notaTexto);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "Nota inválida.",
                400
            );

            return;
        }

        if (nota < 1 || nota > 5) {

            enviarResposta(
                exchange,
                "A nota deve estar entre 1 e 5.",
                400
            );

            return;
        }
        String buscarUsuario =
        "SELECT id FROM usuario WHERE nome_usuario = ?";

        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement busca =
                conexao.prepareStatement(buscarUsuario)
        ) {

            busca.setString(
                1,
                usuario
            );

            ResultSet resultado =
                busca.executeQuery();

            if (!resultado.next()) {

                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );

                return;
            }

            int usuarioId =
                resultado.getInt("id");

            String inserir = """
                INSERT INTO resenhas
                (
                    nome_livro,
                    autor,
                    nota,
                    texto,
                    usuario_id
                )
                VALUES (?, ?, ?, ?, ?)
                """;

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(inserir)
            ) {

                comando.setString(
                    1,
                    livro
                );

                comando.setString(
                    2,
                    autor
                );

                comando.setDouble(
                    3,
                    nota
                );

                comando.setString(
                    4,
                    texto
                );

                comando.setInt(
                    5,
                    usuarioId
                );

                comando.executeUpdate();

                verificarConquistas(
                    conexao,
                    usuarioId
                );
                
                enviarResposta(
                    exchange,
                    "Resenha publicada com sucesso!",
                    200
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            enviarResposta(
                exchange,
                "Erro ao cadastrar resenha: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void curtirResenha(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );

            return;
        }

        String json = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String resenhaIdTexto =
            extrair(json, "resenha_id");

        String usuario =
            extrair(json, "usuario");

        if (
            resenhaIdTexto.isEmpty() ||
            usuario.isEmpty()
        ) {

            enviarResposta(
                exchange,
                "Dados incompletos.",
                400
            );

            return;
        }

        int resenhaId;

        try {

            resenhaId =
                Integer.parseInt(resenhaIdTexto);

        } catch (NumberFormatException e) {

            enviarResposta(
                exchange,
                "ID da resenha inválido.",
                400
            );

            return;
        }

        try (
            Connection conexao =
                Conexao.conectar()
        ) {

            String buscarUsuario = """
                SELECT id
                FROM usuario
                WHERE nome_usuario = ?
                """;

            int usuarioId;

            try (
                PreparedStatement busca =
                    conexao.prepareStatement(buscarUsuario)
            ) {

                busca.setString(
                    1,
                    usuario
                );

                ResultSet resultado =
                    busca.executeQuery();

                if (!resultado.next()) {

                    enviarResposta(
                        exchange,
                        "Usuário não encontrado.",
                        404
                    );

                    return;
                }

                usuarioId =
                    resultado.getInt("id");
            }

            String remover = """
                DELETE FROM curtidas_resenha
                WHERE resenha_id = ?
                AND usuario_id = ?
                """;

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(remover)
            ) {

                comando.setInt(
                    1,
                    resenhaId
                );

                comando.setInt(
                    2,
                    usuarioId
                );

                int removidas =
                    comando.executeUpdate();

                if (removidas > 0) {

                    enviarResposta(
                        exchange,
                        "Curtida removida.",
                        200
                    );

                    return;
                }
            }

            String adicionar = """
                INSERT INTO curtidas_resenha
                (
                    resenha_id,
                    usuario_id
                )
                VALUES (?, ?)
                """;

            try (
                PreparedStatement comando =
                    conexao.prepareStatement(adicionar)
            ) {

                comando.setInt(
                    1,
                    resenhaId
                );

                comando.setInt(
                    2,
                    usuarioId
                );

                comando.executeUpdate();

                enviarResposta(
                    exchange,
                    "Resenha curtida!",
                    200
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            enviarResposta(
                exchange,
                "Erro ao curtir resenha: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void cadastrarComentario(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "POST, OPTIONS"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );
    
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
    
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
    
            return;
        }
    
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
    
            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );
    
            return;
        }
    
        String corpo = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );
    
        String resenhaId =
            extrair(corpo, "resenha_id");
    
        String usuario =
            extrair(corpo, "usuario");
    
        String comentario =
            extrair(corpo, "comentario");
    
        if (
            resenhaId.isEmpty() ||
            usuario.isEmpty() ||
            comentario.isEmpty()
        ) {
    
            enviarResposta(
                exchange,
                "Dados incompletos.",
                400
            );
    
            return;
        }
    
        String sql = """
            INSERT INTO comentarios_resenha
            (resenha_id, usuario_id, comentario)
            SELECT ?, id, ?
            FROM usuario
            WHERE nome_usuario = ?
            """;
    
        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {
    
            comando.setInt(
                1,
                Integer.parseInt(resenhaId)
            );
    
            comando.setString(
                2,
                comentario
            );
    
            comando.setString(
                3,
                usuario
            );
    
            int linhas =
                comando.executeUpdate();
    
            if (linhas > 0) {
    
                enviarResposta(
                    exchange,
                    "Comentário publicado!",
                    200
                );
    
            } else {
    
                enviarResposta(
                    exchange,
                    "Usuário não encontrado.",
                    404
                );
            }
    
        } catch (NumberFormatException e) {
    
            enviarResposta(
                exchange,
                "ID da resenha inválido.",
                400
            );
    
        } catch (SQLException e) {
    
            e.printStackTrace();
    
            enviarResposta(
                exchange,
                "Erro ao salvar comentário: "
                + e.getMessage(),
                500
            );
        }
    }

    private static void listarComentarios(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin",
            "*"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, OPTIONS"
        );
    
        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );
    
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
    
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
    
            return;
        }
    
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
    
            enviarResposta(
                exchange,
                "Método não permitido.",
                405
            );
    
            return;
        }
    
        String query =
            exchange.getRequestURI().getQuery();
    
        if (query == null || !query.startsWith("resenha_id=")) {
    
            enviarResposta(
                exchange,
                "ID da resenha não informado.",
                400
            );
    
            return;
        }
    
        String resenhaId =
            java.net.URLDecoder.decode(
                query.substring("resenha_id=".length()),
                StandardCharsets.UTF_8
            );
    
        String sql = """
            SELECT
                c.id,
                c.comentario,
                u.nome_usuario
            FROM comentarios_resenha c
            INNER JOIN usuario u
                ON c.usuario_id = u.id
            WHERE c.resenha_id = ?
            ORDER BY c.id ASC
            """;
    
        try (
            Connection conexao = Conexao.conectar();
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {
    
            comando.setInt(
                1,
                Integer.parseInt(resenhaId)
            );
    
            ResultSet resultado =
                comando.executeQuery();
    
            StringBuilder resposta =
                new StringBuilder("[");
    
            boolean primeiro = true;
    
            while (resultado.next()) {
    
                if (!primeiro) {
                    resposta.append(",");
                }
    
                primeiro = false;
    
                String usuario =
                    resultado.getString("nome_usuario");
    
                String comentario =
                    resultado.getString("comentario");
    
                resposta.append("{");
    
                resposta.append(
                    "\"id\":\""
                );
    
                resposta.append(
                    resultado.getInt("id")
                );
    
                resposta.append("\",");
    
                resposta.append(
                    "\"usuario\":\""
                );
    
                resposta.append(
                    escaparJson(usuario)
                );
    
                resposta.append("\",");
    
                resposta.append(
                    "\"comentario\":\""
                );
    
                resposta.append(
                    escaparJson(comentario)
                );
    
                resposta.append("\"");
    
                resposta.append("}");
            }
    
            resposta.append("]");
    
            exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
            );
    
            byte[] dados =
                resposta.toString().getBytes(
                    StandardCharsets.UTF_8
                );
    
            exchange.sendResponseHeaders(
                200,
                dados.length
            );
    
            OutputStream saida =
                exchange.getResponseBody();
    
            saida.write(dados);
            saida.close();
    
        } catch (NumberFormatException e) {
    
            enviarResposta(
                exchange,
                "ID da resenha inválido.",
                400
            );
    
        } catch (SQLException e) {
    
            e.printStackTrace();
    
            enviarResposta(
                exchange,
                "Erro ao buscar comentários: "
                + e.getMessage(),
                500
            );
        }
    }

  
private static void listarLivros(HttpExchange exchange)
throws IOException {

exchange.getResponseHeaders().set(
"Access-Control-Allow-Origin", "*"
);

exchange.getResponseHeaders().set(
"Access-Control-Allow-Methods", "GET, OPTIONS"
);

exchange.getResponseHeaders().set(
"Access-Control-Allow-Headers", "Content-Type"
);

if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
exchange.sendResponseHeaders(204, -1);
exchange.close();
return;
}

if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
enviarResposta(exchange, "Método não permitido.", 405);
return;
}

String query = exchange.getRequestURI().getRawQuery();

String categoria = null;
String destaque = null;

if (query != null) {
for (String parametro : query.split("&")) {
    String[] partes = parametro.split("=", 2);

    String chave = java.net.URLDecoder.decode(
        partes[0], StandardCharsets.UTF_8
    );

    String valor = partes.length > 1
        ? java.net.URLDecoder.decode(
            partes[1], StandardCharsets.UTF_8
        )
        : "";

    if (chave.equals("categoria")) {
        categoria = valor;
    }

    if (chave.equals("destaque")) {
        destaque = valor;
    }
}
}

StringBuilder sql = new StringBuilder("""
SELECT id, titulo, autor, categoria,
       capa, arquivo, descricao, destaque
FROM livros
WHERE 1 = 1
""");

if (categoria != null && !categoria.isBlank()) {
sql.append(" AND LOWER(categoria) = LOWER(?) ");
}

if ("true".equalsIgnoreCase(destaque)) {
sql.append(" AND destaque = TRUE ");
} else if ("false".equalsIgnoreCase(destaque)) {
sql.append(" AND destaque = FALSE ");
}

sql.append(" ORDER BY id ");

StringBuilder json = new StringBuilder("[");
boolean primeiro = true;

try (
Connection conexao = Conexao.conectar();
PreparedStatement comando =
    conexao.prepareStatement(sql.toString())
) {

int indice = 1;

if (categoria != null && !categoria.isBlank()) {
    comando.setString(indice++, categoria);
}

try (ResultSet resultado = comando.executeQuery()) {

    while (resultado.next()) {

        if (!primeiro) {
            json.append(",");
        }

        json.append("{")
            .append("\"id\":")
            .append(resultado.getInt("id"))
            .append(",\"titulo\":\"")
            .append(escaparJson(resultado.getString("titulo")))
            .append("\",\"autor\":\"")
            .append(escaparJson(resultado.getString("autor")))
            .append("\",\"categoria\":\"")
            .append(escaparJson(resultado.getString("categoria")))
            .append("\",\"capa\":\"")
            .append(escaparJson(resultado.getString("capa")))
            .append("\",\"arquivo\":\"")
            .append(escaparJson(resultado.getString("arquivo")))
            .append("\",\"descricao\":\"")
            .append(escaparJson(resultado.getString("descricao")))
            .append("\",\"destaque\":")
            .append(resultado.getBoolean("destaque"))
            .append("}");

        primeiro = false;
    }
}

json.append("]");

exchange.getResponseHeaders().set(
    "Content-Type", "application/json; charset=UTF-8"
);

enviarResposta(exchange, json.toString(), 200);

} catch (SQLException e) {
e.printStackTrace();

enviarResposta(
    exchange,
    "Erro ao buscar livros: " + e.getMessage(),
    500
);
}
}


private static void excluirResenha(HttpExchange exchange)
        throws IOException {

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin", "*"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Methods", "POST, OPTIONS"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Headers", "Content-Type"
    );

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        enviarResposta(exchange, "Método não permitido.", 405);
        return;
    }

    String json = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    String idTexto = extrair(json, "resenha_id");
    String usuario = extrair(json, "usuario");

    if (idTexto.isEmpty() || usuario.isEmpty()) {
        enviarResposta(exchange, "Dados incompletos.", 400);
        return;
    }

    int resenhaId;

    try {
        resenhaId = Integer.parseInt(idTexto);
    } catch (NumberFormatException e) {
        enviarResposta(exchange, "ID da resenha inválido.", 400);
        return;
    }

    String sql = """
        DELETE FROM resenhas r
        USING usuario u
        WHERE r.id = ?
          AND r.usuario_id = u.id
          AND u.nome_usuario = ?
        """;

    try (
        Connection conexao = Conexao.conectar();
        PreparedStatement comando = conexao.prepareStatement(sql)
    ) {

        comando.setInt(1, resenhaId);
        comando.setString(2, usuario);

        int removidas = comando.executeUpdate();

        if (removidas > 0) {
            enviarResposta(
                exchange,
                "Resenha excluída com sucesso!",
                200
            );
        } else {
            enviarResposta(
                exchange,
                "Resenha não encontrada ou você não é o autor.",
                403
            );
        }

    } catch (SQLException e) {
        e.printStackTrace();

        enviarResposta(
            exchange,
            "Erro ao excluir resenha: " + e.getMessage(),
            500
        );
    }
}
private static void excluirClube(HttpExchange exchange)
        throws IOException {

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin",
        "*"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Methods",
        "POST, OPTIONS"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Headers",
        "Content-Type"
    );

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

        exchange.sendResponseHeaders(204, -1);
        exchange.close();

        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

        enviarResposta(
            exchange,
            "Método não permitido.",
            405
        );

        return;
    }

    String json = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    System.out.println("Exclusão de clube recebida:");
    System.out.println(json);

    String clubeIdTexto =
        extrair(json, "clube_id");

    String usuario =
        extrair(json, "usuario");

    if (
        clubeIdTexto.isEmpty() ||
        usuario.isEmpty()
    ) {

        enviarResposta(
            exchange,
            "Dados incompletos.",
            400
        );

        return;
    }

    int clubeId;

    try {

        clubeId =
            Integer.parseInt(clubeIdTexto);

    } catch (NumberFormatException e) {

        enviarResposta(
            exchange,
            "ID do clube inválido.",
            400
        );

        return;
    }

    String buscarUsuario = """
        SELECT id
        FROM usuario
        WHERE nome_usuario = ?
        """;

    try (
        Connection conexao = Conexao.conectar();

        PreparedStatement buscaUsuario =
            conexao.prepareStatement(buscarUsuario)
    ) {

        buscaUsuario.setString(1, usuario);

        ResultSet resultado =
            buscaUsuario.executeQuery();

        if (!resultado.next()) {

            enviarResposta(
                exchange,
                "Usuário não encontrado.",
                404
            );

            return;
        }

        int usuarioId =
            resultado.getInt("id");

        String sql = """
            DELETE FROM clubes
            WHERE id = ?
            AND criado_por = ?
            """;

        try (
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, clubeId);
            comando.setInt(2, usuarioId);

            int linhasAfetadas =
                comando.executeUpdate();

            if (linhasAfetadas == 0) {

                enviarResposta(
                    exchange,
                    "Você não tem permissão para excluir este clube.",
                    403
                );

                return;
            }

            enviarResposta(
                exchange,
                "Clube excluído com sucesso!",
                200
            );
        }

    } catch (SQLException e) {

        e.printStackTrace();

        enviarResposta(
            exchange,
            "Erro ao excluir clube: "
            + e.getMessage(),
            500
        );
    }
}
private static void buscarClube(HttpExchange exchange)
        throws IOException {

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin",
        "*"
    );

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

        enviarResposta(
            exchange,
            "Método não permitido.",
            405
        );

        return;
    }

    String query =
        exchange.getRequestURI().getQuery();

    if (query == null || !query.startsWith("id=")) {

        enviarResposta(
            exchange,
            "ID do clube não informado.",
            400
        );

        return;
    }

    String idTexto =
        query.substring(3);

    int clubeId;

    try {

        clubeId =
            Integer.parseInt(idTexto);

    } catch (NumberFormatException e) {

        enviarResposta(
            exchange,
            "ID do clube inválido.",
            400
        );

        return;
    }

    String sql = """
        SELECT
            id,
            nome,
            presidente,
            max_membros,
            descricao,
            genero_principal,
            genero_secundario,
            dia_reuniao,
            horario_reuniao,
            frequencia
        FROM clubes
        WHERE id = ?
        """;

    try (
        Connection conexao =
            Conexao.conectar();

        PreparedStatement comando =
            conexao.prepareStatement(sql)
    ) {

        comando.setInt(1, clubeId);

        ResultSet resultado =
            comando.executeQuery();

        if (!resultado.next()) {

            enviarResposta(
                exchange,
                "Clube não encontrado.",
                404
            );

            return;
        }

        StringBuilder json =
            new StringBuilder();

        json.append("{");

        json.append("\"id\":")
            .append(resultado.getInt("id"))
            .append(",");

        json.append("\"nome\":\"")
            .append(
                escaparJson(
                    resultado.getString("nome")
                )
            )
            .append("\",");

        json.append("\"presidente\":\"")
            .append(
                escaparJson(
                    resultado.getString("presidente")
                )
            )
            .append("\",");

        json.append("\"max_membros\":")
            .append(
                resultado.getInt("max_membros")
            )
            .append(",");

        json.append("\"descricao\":\"")
            .append(
                escaparJson(
                    resultado.getString("descricao")
                )
            )
            .append("\",");

        json.append("\"genero_principal\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "genero_principal"
                    )
                )
            )
            .append("\",");

        json.append("\"genero_secundario\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "genero_secundario"
                    )
                )
            )
            .append("\",");

        json.append("\"dia_reuniao\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "dia_reuniao"
                    )
                )
            )
            .append("\",");

        json.append("\"horario_reuniao\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "horario_reuniao"
                    )
                )
            )
            .append("\",");

        json.append("\"frequencia\":\"")
            .append(
                escaparJson(
                    resultado.getString(
                        "frequencia"
                    )
                )
            )
            .append("\"");

        json.append("}");

        enviarResposta(
            exchange,
            json.toString(),
            200
        );

    } catch (SQLException e) {

        e.printStackTrace();

        enviarResposta(
            exchange,
            "Erro ao buscar clube: "
            + e.getMessage(),
            500
        );
    }
}
private static void atualizarClube(HttpExchange exchange)
        throws IOException {

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin",
        "*"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Methods",
        "POST, OPTIONS"
    );

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Headers",
        "Content-Type"
    );

    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

        exchange.sendResponseHeaders(204, -1);
        exchange.close();

        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

        enviarResposta(
            exchange,
            "Método não permitido.",
            405
        );

        return;
    }

    String json =
        new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

    String idTexto =
        extrair(json, "clube_id");

    String nome =
        extrair(json, "nome");

    String presidente =
        extrair(json, "presidente");

    String maxMembrosTexto =
        extrair(json, "max_membros");

    String descricao =
        extrair(json, "descricao");

    String generoPrincipal =
        extrair(json, "genero_principal");

    String generoSecundario =
        extrair(json, "genero_secundario");

    String diaReuniao =
        extrair(json, "dia_reuniao");

    String horarioReuniao =
        extrair(json, "horario_reuniao");

    String frequencia =
        extrair(json, "frequencia");

    String usuario =
        extrair(json, "usuario");

    if (
        idTexto.isEmpty() ||
        nome.isEmpty() ||
        presidente.isEmpty() ||
        maxMembrosTexto.isEmpty() ||
        usuario.isEmpty()
    ) {

        enviarResposta(
            exchange,
            "Preencha os campos obrigatórios.",
            400
        );

        return;
    }

    int clubeId;
    int maxMembros;

    try {

        clubeId =
            Integer.parseInt(idTexto);

        maxMembros =
            Integer.parseInt(maxMembrosTexto);

    } catch (NumberFormatException e) {

        enviarResposta(
            exchange,
            "ID ou quantidade de membros inválida.",
            400
        );

        return;
    }

    String buscarUsuario = """
        SELECT id
        FROM usuario
        WHERE nome_usuario = ?
        """;

    try (
        Connection conexao =
            Conexao.conectar();

        PreparedStatement buscaUsuario =
            conexao.prepareStatement(
                buscarUsuario
            )
    ) {

        buscaUsuario.setString(
            1,
            usuario
        );

        ResultSet resultado =
            buscaUsuario.executeQuery();

        if (!resultado.next()) {

            enviarResposta(
                exchange,
                "Usuário não encontrado.",
                404
            );

            return;
        }

        int usuarioId =
            resultado.getInt("id");

        String sql = """
            UPDATE clubes
            SET
                nome = ?,
                presidente = ?,
                max_membros = ?,
                descricao = ?,
                genero_principal = ?,
                genero_secundario = ?,
                dia_reuniao = ?,
                horario_reuniao = ?,
                frequencia = ?
            WHERE id = ?
            AND criado_por = ?
            """;

        try (
            PreparedStatement comando =
                conexao.prepareStatement(sql)
        ) {

            comando.setString(1, nome);
            comando.setString(2, presidente);
            comando.setInt(3, maxMembros);
            comando.setString(4, descricao);
            comando.setString(5, generoPrincipal);
            comando.setString(6, generoSecundario);
            comando.setString(7, diaReuniao);
            comando.setString(8, horarioReuniao);
            comando.setString(9, frequencia);
            comando.setInt(10, clubeId);
            comando.setInt(11, usuarioId);

            int linhasAfetadas =
                comando.executeUpdate();

            if (linhasAfetadas == 0) {

                enviarResposta(
                    exchange,
                    "Você não tem permissão para editar este clube.",
                    403
                );

                return;
            }

            enviarResposta(
                exchange,
                "Clube atualizado com sucesso!",
                200
            );
        }

    } catch (SQLException e) {

        e.printStackTrace();

        enviarResposta(
            exchange,
            "Erro ao atualizar clube: "
            + e.getMessage(),
            500
        );
    }
}
private static void ranking(HttpExchange exchange)
        throws IOException {

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin",
        "*"
    );

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

        enviarResposta(
            exchange,
            "Método não permitido.",
            405
        );

        return;
    }

    String sql = """
        SELECT
            u.id,
            u.nome_usuario,

            (
                SELECT COUNT(*)
                FROM leituras l
                WHERE l.usuario_id = u.id
            ) AS livros,

            (
                SELECT COALESCE(SUM(l.paginas_lidas), 0)
                FROM leituras l
                WHERE l.usuario_id = u.id
            ) AS paginas,

            (
                SELECT COUNT(*)
                FROM resenhas r
                WHERE r.usuario_id = u.id
            ) AS resenhas,

            (
                SELECT COALESCE(AVG(r.nota), 0)
                FROM resenhas r
                WHERE r.usuario_id = u.id
            ) AS media

        FROM usuario u

        ORDER BY livros DESC, paginas DESC, resenhas DESC
        """;

    StringBuilder json =
        new StringBuilder("[");

    boolean primeiro = true;

    try (
        Connection conexao = Conexao.conectar();

        PreparedStatement comando =
            conexao.prepareStatement(sql)
    ) {

        ResultSet resultado =
            comando.executeQuery();

        while (resultado.next()) {

            if (!primeiro) {
                json.append(",");
            }

            json.append("{");

            json.append("\"nome\":\"")
                .append(
                    escaparJson(
                        resultado.getString("nome_usuario")
                    )
                )
                .append("\",");

            json.append("\"livros\":")
                .append(
                    resultado.getInt("livros")
                )
                .append(",");

            json.append("\"paginas\":")
                .append(
                    resultado.getInt("paginas")
                )
                .append(",");

            json.append("\"resenhas\":")
                .append(
                    resultado.getInt("resenhas")
                )
                .append(",");

            json.append("\"avg\":")
                .append(
                    resultado.getDouble("media")
                );

            json.append("}");

            primeiro = false;
        }

        json.append("]");

        exchange.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        byte[] dados =
            json.toString().getBytes(
                StandardCharsets.UTF_8
            );

        exchange.sendResponseHeaders(
            200,
            dados.length
        );

        OutputStream saida =
            exchange.getResponseBody();

        saida.write(dados);
        saida.close();

    } catch (SQLException e) {

        e.printStackTrace();

        enviarResposta(
            exchange,
            "Erro ao carregar ranking: "
            + e.getMessage(),
            500
        );
    }
}
private static void verificarConquistas(
    Connection conexao,
    int usuarioId)
    throws SQLException {

String sqlLivros = """
    SELECT COUNT(*)
    FROM leituras
    WHERE usuario_id = ?
    AND status = 'concluido'
    """;

int livrosConcluidos = 0;

try (
    PreparedStatement comando =
        conexao.prepareStatement(sqlLivros)
) {

    comando.setInt(1, usuarioId);

    ResultSet resultado =
        comando.executeQuery();

    if (resultado.next()) {

        livrosConcluidos =
            resultado.getInt(1);
    }
}


String sqlResenhas = """
    SELECT COUNT(*)
    FROM resenhas
    WHERE usuario_id = ?
    """;

int quantidadeResenhas = 0;

try (
    PreparedStatement comando =
        conexao.prepareStatement(sqlResenhas)
) {

    comando.setInt(1, usuarioId);

    ResultSet resultado =
        comando.executeQuery();

    if (resultado.next()) {

        quantidadeResenhas =
            resultado.getInt(1);
    }
}


// PRIMEIRO LIVRO

if (livrosConcluidos >= 1) {

    registrarConquista(
        conexao,
        usuarioId,
        "primeiro-livro",
        "Primeiro Livro",
        "Parabéns! Você terminou seu primeiro livro."
    );
}


// LEITOR FREQUENTE

if (livrosConcluidos >= 20) {

    registrarConquista(
        conexao,
        usuarioId,
        "leitor-frequente",
        "Leitor Frequente",
        "Você alcançou a marca de 20 livros lidos!"
    );
}


// CRÍTICO LITERÁRIO

if (quantidadeResenhas >= 10) {

    registrarConquista(
        conexao,
        usuarioId,
        "critico-literario",
        "Crítico Literário",
        "Você publicou 10 ou mais resenhas no LiteLAB!"
    );
}
}
private static void registrarConquista(
    Connection conexao,
    int usuarioId,
    String codigo,
    String nomeConquista,
    String mensagem)
    throws SQLException {

String sql = """
    INSERT INTO notificacoes
    (
        usuario_id,
        tipo,
        codigo,
        titulo,
        mensagem
    )
    VALUES (?, 'CONQUISTA', ?, ?, ?)
    ON CONFLICT
    (
        usuario_id,
        tipo,
        codigo
    )
    DO NOTHING
    """;

try (
    PreparedStatement comando =
        conexao.prepareStatement(sql)
) {

    comando.setInt(1, usuarioId);

    comando.setString(
        2,
        codigo
    );

    comando.setString(
        3,
        "🏆 Nova conquista: " + nomeConquista
    );

    comando.setString(
        4,
        mensagem
    );

    comando.executeUpdate();
}
}
private static void listarNotificacoes(
    HttpExchange exchange)
    throws IOException {

exchange.getResponseHeaders().set(
    "Access-Control-Allow-Origin",
    "*"
);

if (!exchange.getRequestMethod()
        .equalsIgnoreCase("GET")) {

    enviarResposta(
        exchange,
        "Método não permitido.",
        405
    );
    return;
}

String query =
    exchange.getRequestURI().getQuery();

if (
    query == null ||
    !query.startsWith("usuario=")
) {
    enviarResposta(
        exchange,
        "Usuário não informado.",
        400
    );
    return;
}

String nomeUsuario =
    query.substring(
        "usuario=".length()
    );

String sql = """
    SELECT
        n.id,
        n.tipo,
        n.codigo,
        n.titulo,
        n.mensagem,
        n.lida,
        n.criada_em
    FROM notificacoes n
    INNER JOIN usuario u
        ON n.usuario_id = u.id
    WHERE u.nome_usuario = ?
    ORDER BY n.id DESC
    """;

StringBuilder json =
    new StringBuilder("[");

boolean primeiro = true;

try (
    Connection conexao =
        Conexao.conectar();

    PreparedStatement comando =
        conexao.prepareStatement(sql)
) {

    comando.setString(
        1,
        nomeUsuario
    );

    ResultSet resultado =
        comando.executeQuery();

    while (resultado.next()) {

        if (!primeiro) {
            json.append(",");
        }

        json.append("{");

        json.append("\"id\":")
            .append(
                resultado.getInt("id")
            )
            .append(",");

        json.append("\"tipo\":\"")
            .append(
                escaparJson(
                    resultado.getString("tipo")
                )
            )
            .append("\",");

        json.append("\"codigo\":\"")
            .append(
                escaparJson(
                    resultado.getString("codigo")
                )
            )
            .append("\",");

        json.append("\"titulo\":\"")
            .append(
                escaparJson(
                    resultado.getString("titulo")
                )
            )
            .append("\",");

        json.append("\"mensagem\":\"")
            .append(
                escaparJson(
                    resultado.getString("mensagem")
                )
            )
            .append("\",");

        json.append("\"lida\":")
            .append(
                resultado.getBoolean("lida")
            );

        json.append("}");

        primeiro = false;
    }

    json.append("]");

    exchange.getResponseHeaders().set(
        "Content-Type",
        "application/json; charset=UTF-8"
    );

    byte[] dados =
        json.toString().getBytes(
            StandardCharsets.UTF_8
        );

    exchange.sendResponseHeaders(
        200,
        dados.length
    );

    OutputStream saida =
        exchange.getResponseBody();

    saida.write(dados);
    saida.close();

} catch (SQLException e) {

    e.printStackTrace();

    enviarResposta(
        exchange,
        "Erro ao carregar notificações: "
        + e.getMessage(),
        500
    );
}
}
private static void marcarNotificacao(
    HttpExchange exchange)
    throws IOException {

exchange.getResponseHeaders().set(
    "Access-Control-Allow-Origin",
    "*"
);

if (!exchange.getRequestMethod()
        .equalsIgnoreCase("POST")) {

    enviarResposta(
        exchange,
        "Método não permitido.",
        405
    );
    return;
}

String json = new String(
    exchange.getRequestBody().readAllBytes(),
    StandardCharsets.UTF_8
);

String idTexto =
    extrair(json, "id");

String usuario =
    extrair(json, "usuario");

if (
    idTexto.isEmpty() ||
    usuario.isEmpty()
) {
    enviarResposta(
        exchange,
        "Dados incompletos.",
        400
    );
    return;
}

int id;

try {
    id = Integer.parseInt(idTexto);
} catch (NumberFormatException e) {
    enviarResposta(
        exchange,
        "ID inválido.",
        400
    );
    return;
}

String sql = """
    UPDATE notificacoes n
    SET lida = TRUE
    FROM usuario u
    WHERE n.id = ?
    AND n.usuario_id = u.id
    AND u.nome_usuario = ?
    """;

try (
    Connection conexao =
        Conexao.conectar();

    PreparedStatement comando =
        conexao.prepareStatement(sql)
) {

    comando.setInt(1, id);
    comando.setString(2, usuario);

    comando.executeUpdate();

    enviarResposta(
        exchange,
        "Notificação marcada como lida.",
        200
    );

} catch (SQLException e) {

    e.printStackTrace();

    enviarResposta(
        exchange,
        "Erro ao marcar notificação: "
        + e.getMessage(),
        500
    );
}
}
}