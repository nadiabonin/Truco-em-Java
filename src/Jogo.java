import java.io.IOException;
import java.util.*;

public class Jogo {
    private Baralho baralho;
    private Jogador j1,j2,j3,j4;
    private int pontos1,pontos2;
    private int Rodadas = 0;
    private String manilha;
    private int rodadasTime1 = 0;
    private int rodadasTime2 = 0;
    private int valorMao = 1;
    private boolean trucoJaPedido = false;
    int op;
    private List<ConexaoJogador> conexoes; 
    private Servidor servidor;
    private Scanner s;

//sobrecarregar o contrutor
    //contrutor Local, se servidor = null local
    public Jogo(Jogador j1, Jogador j2, Jogador j3, Jogador j4, Scanner scanner) {
        this.j1 = j1;
        this.j2 = j2;
        this.j3 = j3;
        this.j4 = j4;
        this.s  = scanner;
        this.servidor = null;
        baralho = new Baralho();
    }

    // Construtor REDE, recebe as conexões
    public Jogo(Jogador j1, Jogador j2, Jogador j3, Jogador j4,
                List<ConexaoJogador> conexoes, Servidor servidor) {
        this.j1       = j1;
        this.j2       = j2;
        this.j3       = j3;
        this.j4       = j4;
        this.conexoes = conexoes;
        this.servidor = servidor;
        baralho = new Baralho();
    }
    //pontuação de time
    public void adicionarPontos1(int pontos1) {
        this.pontos1 += pontos1;
    }
    public void adicionarPontos2(int pontos2) {
        this.pontos2 += pontos2;
    }

    public int getPontos1() {
        return pontos1;
    }
    
    public int getPontos2() {
        return pontos2;
    }
//ve qual é a manilha apartir da carta manilha, faz uma ordem de maior ou menor.
    private String proximacarta(String valor){
        String[] ordem = {"4","5","6","7","Q","J","K","A","2","3"};

        for(int i = 0; i < ordem.length; i++){
            if(ordem[i].equals(valor)){
                return ordem[(i+ 1) % ordem.length];
            }
        }
        return "";
    }
//ve qual manilha é maior
    private int compararCartas(Carta c1, Carta c2){
        boolean c1Manilha = c1.getValor().equals(manilha);
        boolean c2Manilha = c2.getValor().equals(manilha);

        if(c1Manilha && c2Manilha){
            return c1.getValorNaipe() - c2.getValorNaipe();
        }

        if (c1Manilha) return 1;
        if (c2Manilha) return -1;

        return c1.getValorBase() - c2.getValorBase();
    }
//inicia o jogo rede e local
    public void iniciar() throws IOException {

        j1.limparMao();
        j2.limparMao();
        j3.limparMao();
        j4.limparMao();

        baralho = new Baralho();
        baralho.embaralhar();

        for (int i = 0; i < 3; i++) {
            j1.receberCarta(baralho.comprar());
            j2.receberCarta(baralho.comprar());
            j3.receberCarta(baralho.comprar());
            j4.receberCarta(baralho.comprar());
        }

        Carta vira = baralho.comprar();
        enviarMensagem("\n=== NOVA MÃO ===");
        enviarMensagem("Vira: " + vira);

        manilha = proximacarta(vira.getValor());
        enviarMensagem("Manilha: " + manilha);

        enviarMensagem("\n=== TIMES ===");
        enviarMensagem("Time 1: " + j1.getNome() + " e " + j3.getNome());
        enviarMensagem("Time 2: " + j2.getNome() + " e " + j4.getNome());

        // Manda as cartas individualmente se for rede
        if (servidor != null) {
            conexoes.get(0).enviarMao();
            conexoes.get(1).enviarMao();
            conexoes.get(2).enviarMao();
            conexoes.get(3).enviarMao();
        }

        rodadasTime1 = 0;
        rodadasTime2 = 0;
        valorMao = 1;
        trucoJaPedido = false;

        rodada();
    }

    private boolean pedirTruco(int indiceJogador, int timeQuePediu) throws IOException {

        if (trucoJaPedido || valorMao == 12) return false;

        //MODO REDE
        if (servidor != null) {

            ConexaoJogador quemPede = conexoes.get(indiceJogador);

            // escolhe um adversário do outro time
            ConexaoJogador adversario =
                    (timeQuePediu == 1) ? conexoes.get(1) : conexoes.get(0);

            quemPede.enviar("Deseja pedir truco? (s/n): ");

            String resp;
            
            do {
                resp = quemPede.recebe();
            } while (resp == null || resp.trim().isEmpty());

            if (!resp.trim().equalsIgnoreCase("s")) {
                return false;
            }

            trucoJaPedido = true;

            int valorAnterior = valorMao;
            int novoValor;

            switch (valorMao) {
                case 1: novoValor = 3; break;
                case 3: novoValor = 6; break;
                case 6: novoValor = 9; break;
                case 9: novoValor = 12; break;
                default: return false;
            }

            servidor.enviarParaTodos(
                "\nTRUCO pedido pelo Time " + timeQuePediu + "!"
            );

            adversario.enviar("Aceitam o truco? (s/n): ");

            String aceita;
            do {
                aceita = adversario.recebe();
            } while (aceita == null || aceita.trim().isEmpty());

            if (aceita.trim().equalsIgnoreCase("n")) {
                servidor.enviarParaTodos("Time adversário correu!");

                if (timeQuePediu == 1)
                    pontos1 += valorAnterior;
                else
                    pontos2 += valorAnterior;

                return true; // encerra a mão
            }

            valorMao = novoValor;
            servidor.enviarParaTodos(
                "Truco aceito! Agora valendo " + valorMao + " pontos!"
            );

            return false;
        }

        // MODO LOCAL
        System.out.print("Deseja pedir truco? (s/n): ");
        String resp = s.next().trim();

        if (!resp.equalsIgnoreCase("s")) return false;

        trucoJaPedido = true;

        int valorAnterior = valorMao;

        switch (valorMao) {
            case 1: valorMao = 3; break;
            case 3: valorMao = 6; break;
            case 6: valorMao = 9; break;
            case 9: valorMao = 12; break;
            default: return false;
        }

        System.out.print("Time adversário aceita? (s/n): ");
        String aceita = s.next().trim();

        if (aceita.equalsIgnoreCase("n")) {
            if (timeQuePediu == 1)
                pontos1 += valorAnterior;
            else
                pontos2 += valorAnterior;
            return true;
        }

        System.out.println("Truco aceito!");
        return false;
    }

    private int lerJogadaHost() {
        int escolha = s.nextInt() - 1;
        s.nextLine();
        return escolha;
    }
//faz uma rodada 
    private void rodada() throws IOException {

        enviarMensagem("-- NOVA RODADA --");

        for (int i = 0; i < 3; i++) {

            // ===== J1 — índice 0 — Time 1 =====
            if (pedirTruco(0, 1)) return;

            if (servidor != null) {
                servidor.enviarParaTodos(j1.getNome() + " está jogando...");
                conexoes.get(0).enviar("Sua vez! Escolha sua carta:");
            } else {
                System.out.println("\n" + j1.getNome() + " escolha sua carta:");
            }
            Carta c1 = escolherCarta(j1, conexoes != null ? conexoes.get(0) : null);

            // ===== J2 — índice 1 — Time 2 =====
            if (pedirTruco(1, 2)) return;

            if (servidor != null) {
                servidor.enviarParaTodos(j2.getNome() + " está jogando...");
                conexoes.get(1).enviar("Sua vez! Escolha sua carta:");
            } else {
                System.out.println("\n" + j2.getNome() + " escolha sua carta:");
            }
            Carta c2 = escolherCarta(j2, conexoes != null ? conexoes.get(1) : null);

            // ===== J3 — índice 2 — Time 1 =====
            if (pedirTruco(2, 1)) return;

            if (servidor != null) {
                servidor.enviarParaTodos(j3.getNome() + " está jogando...");
                conexoes.get(2).enviar("Sua vez! Escolha sua carta:");
            } else {
                System.out.println("\n" + j3.getNome() + " escolha sua carta:");
            }
            Carta c3 = escolherCarta(j3, conexoes != null ? conexoes.get(2) : null);

            // ===== J4 — índice 3 — Time 2 =====
            if (pedirTruco(3, 2)) return;

            if (servidor != null) {
                servidor.enviarParaTodos(j4.getNome() + " está jogando...");
                conexoes.get(3).enviar("Sua vez! Escolha sua carta:");
            } else {
                System.out.println("\n" + j4.getNome() + " escolha sua carta:");
            }
            Carta c4 = escolherCarta(j4, conexoes != null ? conexoes.get(3) : null);

            // Mostrar cartas jogadas
            enviarMensagem(j1.getNome() + " jogou: " + c1);
            enviarMensagem(j2.getNome() + " jogou: " + c2);
            enviarMensagem(j3.getNome() + " jogou: " + c3);
            enviarMensagem(j4.getNome() + " jogou: " + c4);

            // Verificar vencedor da rodada
            Carta[] cartas = {c1, c2, c3, c4};
            int vencedor = 0;

            for (int k = 1; k < cartas.length; k++) {
                if (compararCartas(cartas[k], cartas[vencedor]) > 0) {
                    vencedor = k;
                }
            }

            if (vencedor == 0 || vencedor == 2) {
                rodadasTime1++;
                enviarMensagem("Time 1 ganhou a rodada!");
            } else {
                rodadasTime2++;
                enviarMensagem("Time 2 ganhou a rodada!");
            }

            enviarMensagem("----------------------");

            if (rodadasTime1 == 2) {
                pontos1 += valorMao;
                enviarMensagem("Time 1 ganhou a mão!");
                break;
            }

            if (rodadasTime2 == 2) {
                pontos2 += valorMao;
                enviarMensagem("Time 2 ganhou a mão!");
                break;
            }
        }

        if (rodadasTime1 == rodadasTime2) {
            enviarMensagem("Empate na mão!");
        }

        enviarMensagem("Placar -> Time 1: " + pontos1 + " | Time 2: " + pontos2);
    }

    // Escolhe carta para local ou rede
    private Carta escolherCarta(Jogador jogador, ConexaoJogador conexao) throws IOException {
        if (conexao != null) {
            // REDE manda mão para o cliente e recebe escolha
            conexao.enviarMao();
            conexao.enviar("Escolha uma carta (1, 2 ou 3): ");
            int escolha = -1;
            while (escolha < 1 || escolha > jogador.getMao().size()) {
                try {
                    escolha = Integer.parseInt(conexao.recebe());
                } catch (NumberFormatException e) {
                    conexao.enviar("Inválido! Digite 1, 2 ou 3: ");
                }
            }
            return jogador.jogarCarta(escolha - 1);
        } else {
            // LOCAL usa o Scanner
            jogador.mostrarMao();
            System.out.print("Escolha carta (1, 2 ou 3): ");
            int escolha = -1;
            do {
                escolha = s.nextInt();
                if (escolha < 1 || escolha > jogador.getMao().size())
                    System.out.print("Inválido! Escolha novamente: ");
            } while (escolha < 1 || escolha > jogador.getMao().size());
            return jogador.jogarCarta(escolha - 1);
        }
    }

    // Envia mensagem local ou rede
    private void enviarMensagem(String mensagem) {
        if (servidor != null) {
            servidor.enviarParaTodos(mensagem);
        } else {
            System.out.println(mensagem);
        }
    }

    public void AddRodadas(){
    this.Rodadas++;
    

    }
//final do jogo vence quem marca 12 pontos
    public boolean EndGame(){
        return pontos1 >= 12 || pontos2 >= 12;
    }
}