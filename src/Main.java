import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TRUCO PAULISTA aaaaaaaaaaaaaaaaaaa===");
        System.out.println("1 - Jogar local (4 jogadores no mesmo PC)");
        System.out.println("2 - Entrar em partida (cliente)");
        System.out.println("3 - Criar partida (servidor)");
        System.out.print("Escolha: ");
        int op = scanner.nextInt();
        scanner.nextLine();

        switch (op) {
            case 1:
                System.out.print("Nome do Jogador 1 (Time 1): ");
                Jogador j1 = new Jogador(scanner.nextLine());

                System.out.print("Nome do Jogador 2 (Time 2): ");
                Jogador j2 = new Jogador(scanner.nextLine());

                System.out.print("Nome do Jogador 3 (Time 1): ");
                Jogador j3 = new Jogador(scanner.nextLine());

                System.out.print("Nome do Jogador 4 (Time 2): ");
                Jogador j4 = new Jogador(scanner.nextLine());

                System.out.println("\n=== TIMES ===");
                System.out.println("Time 1: " + j1.getNome() + " e " + j3.getNome());
                System.out.println("Time 2: " + j2.getNome() + " e " + j4.getNome());

                // Construtor local — sem Equipe!
                Jogo jogo = new Jogo(j1, j2, j3, j4, scanner);
                while (!jogo.EndGame()) {
                    jogo.iniciar();
                }

                System.out.println("\n=== FIM DE JOGO ===");
                if (jogo.getPontos1() >= 12) {
                    System.out.println("Time 1 venceu!");
                } else {
                    System.out.println("Time 2 venceu!");
                }
                scanner.close();
                break;

            case 2:
                Cliente.main(args);
                break;

            case 3:
                new Servidor().iniciar();
                break;

            default:
                System.out.println("Opção inválida!");
                break;
        }
    }
}