public class Main {

    public static void main(String[] args) {

        System.out.println("Backend do LiteLAB iniciado!");

        Conexao.conectar();

        try {
            Servidor.iniciar();
        } catch (Exception e) {
            System.out.println("Erro ao iniciar servidor:");
            e.printStackTrace();
        }
    }
}