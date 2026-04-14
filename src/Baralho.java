import java.util.*;

public class Baralho {
    private List<Carta> cartas = new ArrayList<>();

    private final String[] valores = {"A", "2", "3", "4", "5", "6", "7", "Q", "J", "K"};
    private final String[] naipes = {"Ouros", "Copas", "Espadas", "Paus"};
//cria a carta
    public Baralho() {
        for (String naipe : naipes) {
            for (String valor : valores) {
                cartas.add(new Carta(valor, naipe));
            }
        }
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }
//remove o primeiro da fila
    public Carta comprar() {
        return cartas.remove(0);
    }
}