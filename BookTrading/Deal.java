
package book;

import jade.content.AgentAction;

public class Deal implements AgentAction {

    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card c) {
        card = c;
    }
}

