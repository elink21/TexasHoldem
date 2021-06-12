package book;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class gamblerPlayerAgent extends Agent{

    private static final long serialVersionUID = 1L;
    public ArrayList<String> cardTypes= new ArrayList<String>();
    public ArrayList<String> cardNumbers= new ArrayList<String>();
    public ArrayList<Card> cards= new ArrayList<>();

    public int wallet=100;
    public int actualBet=0;
    private String finalAction="";
    public ArrayList<Card> tableCards= new ArrayList<>();
    public ArrayList<Card> ownCards= new ArrayList<>();


    public void createCards()
    {
        cardTypes.add("clubs");
        cardTypes.add("diamonds");
        cardTypes.add("hearts");
        cardTypes.add("spades");
        //Adding numerations and combinations
        for (int i=2;i<=10;i++)
        {
            cardNumbers.add(Integer.toString(i));
        }
        cardNumbers.add("A");
        cardNumbers.add("J");
        cardNumbers.add("K");
        cardNumbers.add("Q");

        int index=0;

        //Putting all cards to array
        for(int i=0;i< cardTypes.size();i++)
        {
            for (int j=0;j<cardNumbers.size();j++)
            {
                Card actualCard= new Card(cardTypes.get(i), cardNumbers.get(j),index);
                index+=1;
                cards.add(actualCard);
            }
        }
    }

    @Override protected void setup()
    {
        //Template of agent service
        DFAgentDescription description = new DFAgentDescription();
        description.setName(getAID());
        description.addLanguages("en");
        //Service description template
        ServiceDescription service= new ServiceDescription();
        service.setType("player");
        service.setName("gambler");

        //Creating cards
        createCards();




        description.addServices(service);

        try{
            DFService.register(this,description);
        } catch (FIPAException e)
        {
            e.printStackTrace();
        }

        addBehaviour(new NoobBehaviour());
    }

    @Override
    protected void takeDown()
    {
        try
        {
            DFService.deregister(this);
        }
        catch (FIPAException fe)
        {
            fe.printStackTrace();
        }
        System.out.println("El agente "+getAID().getName()+" ya no ofrece sus servicios.");
    }


    class NoobBehaviour extends  SimpleBehaviour{
        @Override public void action(){
            System.out.println("Gambler is ready to take turn");
            ACLMessage msg= blockingReceive();
            System.out.println("Gambler received msg");
            if(msg!=null)
            {
                System.out.println(msg.getContent());
                splitMessage(msg.getContent());
                System.out.println("My cards are");
                System.out.println(ownCards);
                System.out.println("My highest rank is: ");
                int rank= getRankHand();
                String r="";
                System.out.println(rank);
                ACLMessage response= new ACLMessage(ACLMessage.CONFIRM);
                response.addReceiver(msg.getSender());
                response.setLanguage("en");

                //Chose behaviour
                if (finalAction=="fold")
                {
                    r+="fold|";
                    r+="0";

                }else
                if (actualBet==0)
                {
                    r+="bet|";
                    r+= Integer.toString(rank);
                }
                else{
                    if (rank>35)
                    {
                        r+="raise|";
                        r+=Integer.toString(rank);
                    }
                    else if(rank>20)
                    {
                        r+="check|";
                        r+= Integer.toString(rank);
                    }
                    else{
                        r+="fold|0";
                        finalAction="fold";
                    }
                }
                response.setContent(r);
                send(response);
            }
        }

        @Override public boolean done()
        {
            return false;
        }
    }

    public int getRankHand()
    {
        int highestHand=0;
        for(int i=0;i<tableCards.size();i++)
        {
            for (int j=0;j<tableCards.size();j++)
            {
                for(int k=0;k<tableCards.size();k++)
                {
                    if (i!=j && j!=k && i!=k)
                    {
                        //Creating comparisons
                        ArrayList<Card> actualHand= new ArrayList<>();
                        actualHand.add(ownCards.get(0));
                        actualHand.add(ownCards.get(1));
                        actualHand.add(tableCards.get(i));
                        actualHand.add(tableCards.get(j));
                        actualHand.add(tableCards.get(k));


                        int actualRank= rankHand(actualHand);


                        if(actualRank>highestHand)
                        {
                            highestHand=actualRank;
                        }
                    }
                }

            }
        }
        return highestHand;
    }

    public int rankHand(ArrayList<Card> hand)
    {
        int rank=0;


        //Looking for royal flush and Straight
        ArrayList<Card> royalFlush= new ArrayList<>();

        String[] flush={"A","K","Q","J","10"};

        //Getting hand rank

        Map<String,Integer> cardValues= new HashMap<>();
        cardValues.put("A",1);
        cardValues.put("2",2);
        cardValues.put("3",3);
        cardValues.put("4",4);
        cardValues.put("5",5);
        cardValues.put("6",6);
        cardValues.put("7",7);
        cardValues.put("8",8);
        cardValues.put("9",9);
        cardValues.put("10",10);
        cardValues.put("J",11);
        cardValues.put("Q",12);
        cardValues.put("K",13);

        for(Card c : hand)
        {
            rank+= cardValues.get(c.getCardNumber());
        }


        //First royal flush
        /*boolean isFlush=true;
        if(allSameSuit(hand))
        {
            for (int i=0;i<hand.size();i++)
            {
                for(int j=0;j<flush.length;j++)
                {
                    boolean contains = Arrays.stream(flush).anyMatch(hand.get(i).getCardNumber()::equals);
                    if(!contains)
                    {
                        isFlush= false;
                        break;
                    }

                }
            }

            if(isFlush) return 10;
            //Checking for straights
            boolean isStraight=true;
            String[] straight={"2","3","4","5","6","7","8","9","10","A","J","K","Q"};
            for(int i=0;i<7;i++)
            {
                ArrayList<String> extractedStraight= new ArrayList<>();
                String extractedHand="";
                for (int j=0;j<5;j++)
                {
                    extractedStraight.add(straight[i+j]);
                }

                for (int j=0;j<hand.size();j++)
                {
                    if(!extractedStraight.contains(hand.get(j).getCardNumber()))
                    {
                        isStraight=false;
                        break;
                    }
                }
            }
            if(isStraight) return 9;

            //Checking for four of a kind


        }
        */




        return rank;
    }

    public boolean allSameSuit(ArrayList<Card> hand)
    {
        String suit= hand.get(0).getCardType();
        for(int i=0;i<hand.size();i++)
        {
            if(!hand.get(i).getCardType().equalsIgnoreCase(suit))
            {
                return false;
            }
        }
        return true;
    }


    public void splitMessage(String msg)
    {
        //This message will set the table cards, own cards and bet
        String[] splitMsg= msg.split("\\|");
        //There will be 3 split parts, bet-tableCards-playerCards

        actualBet= Integer.parseInt(splitMsg[0]);

        //Now we resplit the tableCards
        String[] tableCardsStr= splitMsg[1].split(",");

        tableCards.clear();
        ownCards.clear();

        for (int i=0;i<tableCardsStr.length;i++)
        {
            tableCards.add(cards.get(Integer.parseInt(tableCardsStr[i])));
        }

        String[] ownCardsStr= splitMsg[2].split(",");

        for (int i=0;i<ownCardsStr.length;i++)
        {
            ownCards.add(cards.get(Integer.parseInt(ownCardsStr[i])));
        }

        System.out.println(ownCards);

    }

}
