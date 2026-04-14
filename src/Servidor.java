import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {

    private List<ConexaoJogador> conexoes = new ArrayList<>();

    public void iniciar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(Configuracao.PORTA);
        System.out.println("Servidor iniciado! Aguardando 4 jogadores...");
        System.out.println("Porta: " + Configuracao.PORTA);

        // Espera os 4 jogadores conectarem
        while (conexoes.size() < Configuracao.NUM_JOGADORES) {
            Socket socket = serverSocket.accept();

            int numero = conexoes.size() + 1;
            System.out.println("Jogador " + numero + " conectou!");

            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            saida.println("Bem vindo ao Truco Paulista!");
            saida.println("Digite seu nome: ");
            String nome = entrada.readLine();

            Jogador jogador = new Jogador(nome);
            ConexaoJogador conexao = new ConexaoJogador(socket, jogador);
            conexoes.add(conexao);

            System.out.println(nome + " entrou! (" + conexoes.size() + "/4)");
            enviarParaTodos(nome + " entrou! (" + conexoes.size() + "/4)");
        }

        enviarParaTodos("\n=== TODOS CONECTADOS! INICIANDO JOGO ===");
        iniciarJogo();
        serverSocket.close();
    }

    public void enviarParaTodos(String mensagem) {
        for (ConexaoJogador conexao : conexoes) {
            conexao.enviar(mensagem);
        }
    }
//se comunica com os metodos public, jogo, jogador, conexaojogador, define os times
    private void iniciarJogo() throws IOException {
        // Pega os 4 jogadores
        Jogador j1 = conexoes.get(0).getJogador();
        Jogador j2 = conexoes.get(1).getJogador();
        Jogador j3 = conexoes.get(2).getJogador();
        Jogador j4 = conexoes.get(3).getJogador();

        // Avisa os times
        enviarParaTodos("\n=== TIMES ===");
        enviarParaTodos("Time 1: " + j1.getNome() + " e " + j3.getNome());
        enviarParaTodos("Time 2: " + j2.getNome() + " e " + j4.getNome());

        // Cria o jogo passando os 4 jogadores e as conexões
        Jogo jogo = new Jogo(j1, j2, j3, j4, conexoes, this);

        while (!jogo.EndGame()) {
            jogo.iniciar();
        }

        // Resultado final
        if (jogo.getPontos1() >= 12) {
            enviarParaTodos("Time 1 venceu o jogo!");
        } else {
            enviarParaTodos("Time 2 venceu o jogo!");
        }

        for (ConexaoJogador conexao : conexoes) {
            conexao.fechar();
        }
    }

    public static void main(String[] args) throws IOException {
        new Servidor().iniciar();
    }
}