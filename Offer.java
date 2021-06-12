package book;
import jade.content.Predicate;

public class Offer implements Predicate {

    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card c) {
        card = c;
    }
}