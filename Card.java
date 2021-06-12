package book;
import jade.content.Concept;
public class Card  implements  Concept{
    private String cardType;
    private String cardNumber;
    private int index;

    Card(String cardType, String cardNumber,int index)
    {
        this.cardNumber= cardNumber;
        this.cardType= cardType;
        this.index= index;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public int getIndex()
    {
        return index;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardType='" + cardType + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}
