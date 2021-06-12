package book;

import jade.content.Concept;



public class Bet implements Concept {
    private int betAmmount;

    public Bet(int betAmmount) {
        this.betAmmount = betAmmount;
    }

    public int getBetAmmount() {
        return betAmmount;
    }

    public void setBetAmmount(int betAmmount) {
        this.betAmmount = betAmmount;
    }

    @Override
    public String toString() {
        return "Bet{" +
                "betAmmount=" + betAmmount +
                '}';
    }
}
