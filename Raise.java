package book;

import jade.content.AgentAction;

public class Raise implements AgentAction {
    private Bet bet;

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet c) {
        bet = c;
    }
}
