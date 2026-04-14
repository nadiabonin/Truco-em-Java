import java.util.*;

public class Jogador {
    private String nome;
    private List<Carta> mao = new ArrayList<>();
    //novo jogador
    public Jogador(String nome) {
        this.nome = nome;
    }
//cria o vetor mao
    public List<Carta> getMao() {
    return mao;
    }
    //add uma carta na mao
    public void receberCarta(Carta carta) {
        mao.add(carta);
    }
//remove a carta escolhida pelo jogador da mao
    public Carta jogarCarta(int index) {
        return mao.remove(index);
    }
//imprime a mao
    public void mostrarMao() {
        for (int i = 0; i < mao.size(); i++) {
            System.out.println(1+i + " - " + mao.get(i));
        }
    }
//remore todas as cartas da mao
    public void limparMao(){
        mao.clear();
    }

    public String getNome() {
        return nome;
    }
}