public class Carta {
    private String valor;
    private String naipe;

    public Carta(String valor, String naipe) {
        this.valor = valor;
        this.naipe = naipe;
    }
    
    public String getValor() {
        return valor;
    }
    //converte em numerico, maior e menor valor
    public int getValorBase(){
        switch (valor){

        case "4": return 1;
        case "5": return 2;
        case "6": return 3;
        case "7": return 4;
        case "Q": return 5;
        case "J": return 6;
        case "K": return 7;
        case "A": return 8;
        case "2": return 9;
        case "3": return 10;
        }
        return 0;
    }

    public String getNaipe() {
        return naipe;
    }
    //converte em numerico
    public int getValorNaipe(){
        switch (naipe) {
        case "Paus": return 4;
        case "Copas": return 3;
        case "Espadas": return 2;
        case "Ouros": return 1;
    }
        return 0;
    }

    @Override
    public String toString() {
        return valor + " de " + naipe;
    }
}