
package  book;
import jade.content.onto.Ontology;
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
import java.util.HashMap;
import java.util.Map;


public class DealerAgent extends Agent {
    private static final long serialVersionUID = 1L;
    public dealerGUI casinoGUI;
    private String phase="start";

    private AID[] playerAgents;//List of players

    public ArrayList<String> cardTypes= new ArrayList<String>();
    public ArrayList<Card> cards= new ArrayList<Card>();
    public ArrayList<String> cardNumbers= new ArrayList<String>();
    public ArrayList<Card> dealerCards= new ArrayList<Card>();
    public ArrayList<Card> gamblerCards= new ArrayList<Card>();
    public ArrayList<Card> noobCards= new ArrayList<Card>();
    public ArrayList<Card> playerCards= new ArrayList<>();
    private int[] playersRanks={0,0,0};

    public boolean isPlayerTurn= false;

    private String playerAction="";
    private int actualWallet=500;
    private boolean noobFold=false;
    private boolean gamblerFold=false;
    private int actualBet=0;

    private Ontology cardOntology = book.cardOntology.getInstance();
    private boolean needsRepaint= false;


    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private Card extractCard()
    {
        int min=0;
        int max= cards.size()-1;
        int chosenIndex= this.getRandomNumber(min,max);
        Card selected= cards.get(chosenIndex);
        cards.remove(chosenIndex);
        return selected;

    }


    protected void setup()
    {

        //Setting up services
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("card-dealing");
        sd.setName("Dealer");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        dealerCards.clear();
        gamblerCards.clear();
        noobCards.clear();
        playerCards.clear();
        cards.clear();

        //Creating game cards
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

        //Dealing players initial hands
        gamblerCards.add(extractCard());
        gamblerCards.add(extractCard());
        playerCards.add(extractCard());
        playerCards.add(extractCard());
        noobCards.add(extractCard());
        noobCards.add(extractCard());

        System.out.println("Player initial hand");

        System.out.println(playerCards.toString());

        System.out.println("Table initial cards");

        //Selecting 3 initial table cards
        dealerCards.add(extractCard());
        dealerCards.add(extractCard());
        dealerCards.add(extractCard());

        //Printing initial Choice
        System.out.println(dealerCards.toString());


        //Creating UI
        casinoGUI = new dealerGUI(this);

        //Looking for player agents
        ServiceDescription servicePlayers= new ServiceDescription();
        servicePlayers.setType("player");
        DFAgentDescription description = new DFAgentDescription();
        description.addLanguages("en");
        description.addServices(servicePlayers);

        try{
            DFAgentDescription[] actualPlayers= DFService.search(this,description);
            if(actualPlayers.length==0)
            {
                System.out.println("No active players yet");
            }
            playerAgents= new AID[actualPlayers.length];


            for (int i=0;i<actualPlayers.length;i++)
            {
                playerAgents[i]= actualPlayers[i].getName();
                System.out.println("Agent "+actualPlayers[i].getName()+" is an active player now");
            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }



        addBehaviour(new dealerBehaviour());
    }

    public int getBet()
    {
        return actualBet;
    }

    public void handlePlayerAction(String action,String type)
    {
        switch (action) {
            case "bet":
                if(type.equalsIgnoreCase("player")){
                actualWallet -= 10;}
                actualBet = 10;
                break;

            case "check":
                if(type.equalsIgnoreCase("player")) {
                    actualWallet -= 10;
                }
                actualBet+=10;
                break;

            case "fold":
                if(type.equalsIgnoreCase("player"))
                {
                    playerAction="fold";
                }
                if(type.equalsIgnoreCase("noob"))
                {
                    noobFold=true;
                }
                else if(type.equalsIgnoreCase("gambler"))
                {
                    gamblerFold=true;
                }
                break;
            case "raise":
                if(type.equalsIgnoreCase("player")) {
                    actualWallet -= actualBet;
                }
                actualBet *= 2;
                break;
        }
    }

    public void setPlayerAction(final String action) {
        addBehaviour(new OneShotBehaviour() {
            private static final long serialVersionUID = 1L;
            public void action() {
                System.out.print("Player Action is: ");
                System.out.println(action);
                playerAction= action;
            }
        } );
    }

    public void restartBehaviour() {
        addBehaviour(new OneShotBehaviour() {
            private static final long serialVersionUID = 1L;
            public void action() {
                phase="restart";
            }
        } );
    }

    private class dealerBehaviour extends SimpleBehaviour
    {

        public void action()
        {
            isPlayerTurn= false;
            int sleepTime= 4000;

            switch (phase)
            {
                case "start":
                    System.out.println("Hello there im the dealer");
                    casinoGUI.showGui();
                    casinoGUI.paintScene("Welcome players",
                            new ArrayList<Card>(), playerCards,gamblerCards,noobCards,phase, actualWallet, actualBet,false);

                    block(5000);
                    System.out.println("Gui was showed");
                    phase="dealing";
                    break;

                case "dealing":
                    System.out.println("Flopping cards");
                    casinoGUI.paintScene("Dealing initial hands",
                            new ArrayList<Card>(), playerCards,gamblerCards,noobCards,phase, actualWallet,actualBet,false);
                    block(3000);
                    System.out.println("Cards were deal");
                    phase= "ask1";
                    needsRepaint= true;

                    break;

                case "ask1":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Your turn", dealerCards, playerCards, gamblerCards, noobCards, phase, actualWallet, actualBet, true);
                        needsRepaint=false;
                    }
                    //repeating until player action is retrieved
                    if(!playerAction.equalsIgnoreCase("") && !playerAction.equalsIgnoreCase("fold"))
                    {
                        handlePlayerAction(playerAction,"player");
                        casinoGUI.paintScene("U: "+playerAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        block(sleepTime);
                        phase="askNoob1";
                        needsRepaint=true;
                        playerAction="";
                    }else if(playerAction.equalsIgnoreCase("fold"))
                    {
                        System.out.println("You arent longer in the game");
                        casinoGUI.paintScene("U: "+playerAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        block(sleepTime);
                        phase="askNoob1";
                        needsRepaint=true;
                    }

                    break;
                case "askNoob1":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Noob turn 1", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        needsRepaint=false;
                    }


                    ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
                    msg.setSender(getAID());
                    msg.addReceiver(playerAgents[0]);
                    msg.setOntology(cardOntology.getName());
                    //Creating encoded msg
                    String encodedMsg= createEncodedMsg(noobCards);
                    msg.setContent(encodedMsg);
                    send(msg);
                    System.out.println("Dealer asked noob for his action");

                    //Blocking until response is received
                    ACLMessage noobAction= blockingReceive();
                    if(noobAction!=null)
                    {
                        //Decoding response
                        String nAction= decodeMsg(noobAction.getContent(),0);
                        handlePlayerAction(nAction,"noob");
                        casinoGUI.paintScene("Noob: "+nAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        System.out.print("Noob response was: ");
                        System.out.print(noobAction.getContent());
                        phase="askGambler1";
                        needsRepaint=true;
                    }

                    break;

                case "askGambler1":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Gambler turn 1", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        needsRepaint=false;
                    }


                    msg= new ACLMessage(ACLMessage.INFORM);
                    msg.setSender(getAID());
                    msg.addReceiver(playerAgents[1]);
                    msg.setOntology(cardOntology.getName());
                    //Creating encoded msg
                    encodedMsg= createEncodedMsg(gamblerCards);
                    msg.setContent(encodedMsg);
                    send(msg);
                    System.out.println("Dealer asked gambler for his action");

                    //Blocking until response is received
                    noobAction= blockingReceive();
                    if(noobAction!=null)
                    {
                        //Decoding response
                        System.out.println(noobAction.getContent());
                        String nAction= decodeMsg(noobAction.getContent(),1);
                        handlePlayerAction(nAction,"gambler");
                        casinoGUI.paintScene("Gambler: "+nAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(2000);
                        System.out.print("Gambler response was: ");
                        System.out.print(noobAction.getContent());
                        phase="fourthStreet";
                    }
                    break;

                case "fourthStreet": //Flop another card
                    dealerCards.add(extractCard());
                    casinoGUI.paintScene("Fourth street!",
                            dealerCards, playerCards,gamblerCards,noobCards,phase, actualWallet, actualBet, false);
                    sleep(sleepTime);
                    phase="ask2";
                    needsRepaint=true;
                    break;

                case "ask2":
                    if(needsRepaint && !playerAction.equalsIgnoreCase("fold"))
                    {
                        casinoGUI.paintScene("Your turn 2", dealerCards, playerCards, gamblerCards, noobCards, phase, actualWallet, actualBet, true);
                        needsRepaint=false;
                    }
                    //repeating until player action is retrieved
                    if(!playerAction.equalsIgnoreCase("") && !playerAction.equalsIgnoreCase("fold"))
                    {
                        handlePlayerAction(playerAction,"player");
                        casinoGUI.paintScene("U: "+playerAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        block(sleepTime);
                        phase="askNoob2";
                        needsRepaint=true;
                        playerAction="";
                    }else if(playerAction.equalsIgnoreCase("fold"))
                    {
                        casinoGUI.paintScene("U: "+playerAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        block(sleepTime);
                        System.out.println("You arent longer in the game");
                        phase="askNoob2";
                    }

                    break;
                case "askNoob2":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Noob turn 2", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        needsRepaint=false;
                    }

                    msg= new ACLMessage(ACLMessage.INFORM);
                    msg.setSender(getAID());
                    msg.addReceiver(playerAgents[0]);
                    msg.setOntology(cardOntology.getName());
                    //Creating encoded msg
                    encodedMsg= createEncodedMsg(noobCards);
                    msg.setContent(encodedMsg);
                    send(msg);
                    System.out.println("Dealer asked noob for his action");

                    //Blocking until response is received

                        noobAction = blockingReceive();
                        if (noobAction != null) {
                            //Decoding response
                            String nAction = decodeMsg(noobAction.getContent(), 0);
                            handlePlayerAction(nAction, "noob");
                            casinoGUI.paintScene("Noob: " + nAction, dealerCards, playerCards, gamblerCards, noobCards, phase, actualWallet, actualBet, false);
                            sleep(sleepTime);
                            System.out.print("Noob response was: ");
                            System.out.print(noobAction.getContent());
                            phase = "askGambler2";
                            needsRepaint = true;
                        }


                    break;

                case "askGambler2":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Gambler turn 2", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        needsRepaint=false;
                    }


                    msg= new ACLMessage(ACLMessage.INFORM);
                    msg.setSender(getAID());
                    msg.addReceiver(playerAgents[1]);
                    msg.setOntology(cardOntology.getName());
                    //Creating encoded msg
                    encodedMsg= createEncodedMsg(gamblerCards);
                    msg.setContent(encodedMsg);
                    send(msg);
                    System.out.println("Dealer asked gambler for his action");


                    //Blocking until response is received
                        noobAction= blockingReceive();
                        if(noobAction!=null)
                        {
                            //Decoding response
                            String nAction= decodeMsg(noobAction.getContent(),1);
                            handlePlayerAction(nAction,"gambler");
                            casinoGUI.paintScene("Gambler: "+nAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                            sleep(2000);
                            System.out.print("Gambler response was: ");
                            System.out.print(noobAction.getContent());
                            phase="theRiver";
                        }

                    break;

                case "theRiver": //Flop another card
                    dealerCards.add(extractCard());
                    casinoGUI.paintScene("The River!",
                            dealerCards, playerCards,gamblerCards,noobCards,phase, actualWallet, actualBet, false);
                    sleep(sleepTime);
                    phase="ask3";
                    needsRepaint=true;
                    break;

                case "ask3":
                    if(needsRepaint && !playerAction.equalsIgnoreCase("fold"))
                    {
                        casinoGUI.paintScene("Your turn 3", dealerCards, playerCards, gamblerCards, noobCards, phase, actualWallet, actualBet, true);
                        needsRepaint=false;
                    }
                    //repeating until player action is retrieved
                    if(!playerAction.equalsIgnoreCase("") && !playerAction.equalsIgnoreCase("fold"))
                    {
                        handlePlayerAction(playerAction,"player");
                        casinoGUI.paintScene("U: "+playerAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        block(sleepTime);
                        phase="askNoob3";
                        needsRepaint=true;
                        playerAction="";
                    }else if(playerAction.equalsIgnoreCase("fold"))
                    {
                        casinoGUI.paintScene("U: "+playerAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        block(sleepTime);
                        System.out.println("You arent longer in the game");
                        phase="askNoob3";
                    }

                    break;
                case "askNoob3":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Noob turn 3", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        needsRepaint=false;
                    }


                    msg= new ACLMessage(ACLMessage.INFORM);
                    msg.setSender(getAID());
                    msg.addReceiver(playerAgents[0]);
                    msg.setOntology(cardOntology.getName());
                    //Creating encoded msg
                    encodedMsg= createEncodedMsg(noobCards);
                    msg.setContent(encodedMsg);
                    send(msg);
                    System.out.println("Dealer asked noob for his action");

                    //Blocking until response is received

                        noobAction= blockingReceive();
                        if(noobAction!=null)
                        {
                            //Decoding response
                            String nAction= decodeMsg(noobAction.getContent(),0);
                            handlePlayerAction(nAction,"noob");
                            casinoGUI.paintScene("Noob: "+nAction, dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                            sleep(sleepTime);
                            System.out.print("Noob response was: ");
                            System.out.print(noobAction.getContent());
                            phase="askGambler3";
                            needsRepaint=true;
                        }

                    break;

                case "askGambler3":
                    if(needsRepaint)
                    {
                        casinoGUI.paintScene("Gambler turn 3", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        needsRepaint=false;
                    }


                    msg= new ACLMessage(ACLMessage.INFORM);
                    msg.setSender(getAID());
                    msg.addReceiver(playerAgents[1]);
                    msg.setOntology(cardOntology.getName());
                    //Creating encoded msg
                    encodedMsg= createEncodedMsg(gamblerCards);
                    msg.setContent(encodedMsg);
                    send(msg);
                    System.out.println("Dealer asked gambler for his action");

                    //Blocking until response is received

                    noobAction= blockingReceive();
                    if(noobAction!=null) {
                        //Decoding response
                        String nAction = decodeMsg(noobAction.getContent(), 1);
                        handlePlayerAction(nAction, "gambler");
                        casinoGUI.paintScene("Gambler: " + nAction, dealerCards, playerCards, gamblerCards, noobCards, phase, actualWallet, actualBet, false);
                        sleep(2000);
                        System.out.print("Gambler response was: ");
                        System.out.print(noobAction.getContent());
                        phase = "flop";
                        needsRepaint=true;
                    }
                    break;
                case "flop":
                    if(needsRepaint)
                    {
                        needsRepaint=false;
                        casinoGUI.paintScene("Flop!", dealerCards,playerCards,gamblerCards,noobCards,phase,actualWallet,actualBet,false);
                        sleep(sleepTime);
                        phase= "winnerDeclaration";
                    }
                case "winnerDeclaration":
                    //Ranking player hands
                    playersRanks[2]= getRankHand(dealerCards);

                    if(playerAction.equalsIgnoreCase("fold"))
                    {
                        playersRanks[2]=0;
                    }
                    System.out.println("Player ranks");
                    System.out.println(playersRanks[0]);
                    System.out.println(playersRanks[1]);
                    System.out.println(playersRanks[2]);
                    //Deliberating about winner
                    if(playersRanks[0]> playersRanks[1] && playersRanks[0]>playersRanks[2])
                    {
                        //Noob wins
                        casinoGUI.paintScene("Noob wins!!", dealerCards,playerCards,gamblerCards,noobCards,"a",actualWallet,actualBet,false);
                        sleep(sleepTime);
                    }else
                        if(playersRanks[0]+playersRanks[1]+playersRanks[2]==0)
                        {
                            casinoGUI.paintScene("House wins!!", dealerCards,playerCards,gamblerCards,noobCards,"a",actualWallet,actualBet,false);
                            sleep(sleepTime);
                        }
                        else if(playersRanks[1]>playersRanks[0] && playersRanks[1]> playersRanks[2])
                        {
                            casinoGUI.paintScene("Gambler wins!!", dealerCards,playerCards,gamblerCards,noobCards,"a",actualWallet,actualBet,false);
                            sleep(sleepTime);
                        }
                        else{
                            casinoGUI.paintScene("You win!!", dealerCards,playerCards,gamblerCards,noobCards,"a",actualWallet+actualBet,actualBet,false);
                            sleep(sleepTime);
                        }
                        phase="a";
                    break;
            }

        }

        //Ranking functions
        public int getRankHand(ArrayList<Card> tableCards)
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
                            actualHand.add(playerCards.get(0));
                            actualHand.add(playerCards.get(1));
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

        public void sleep(int ms)
        {
            try{
                Thread.sleep(ms);
            } catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }


        public String decodeMsg(String msg,int playerIndex)
        {
            String[] splitMsg= msg.split("\\|");
            playersRanks[playerIndex]= Integer.parseInt(splitMsg[1]);
            return splitMsg[0];
        }

        public String createEncodedMsg(ArrayList<Card> playerCards)
        {
            String msg="";
            msg+= Integer.toString(actualBet);
            msg+="|";
            for (int i=0;i<dealerCards.size();i++)
            {
                msg+=Integer.toString(dealerCards.get(i).getIndex());
                if(i<dealerCards.size()-1)
                {
                    msg+=",";
                }
            }
            msg+="|";

            for (int i=0;i<playerCards.size();i++)
            {
                msg+=Integer.toString(playerCards.get(i).getIndex());
                if(i<playerCards.size()-1)
                {
                    msg+=",";
                }
            }
            return msg;
        }

        public void onStart()
        {
            actualBet=0;
            actualWallet=500;
            playerAction="";
            phase="start";
            gamblerFold=false;
            noobFold=false;

            //Redealing cards
            dealerCards.clear();
            gamblerCards.clear();
            noobCards.clear();
            playerCards.clear();
            cards.clear();
            cardTypes.clear();
            cardNumbers.clear();

            //Creating game cards
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

            //Dealing players initial hands
            gamblerCards.add(extractCard());
            gamblerCards.add(extractCard());
            playerCards.add(extractCard());
            playerCards.add(extractCard());
            noobCards.add(extractCard());
            noobCards.add(extractCard());

            System.out.println("Player initial hand");

            System.out.println(playerCards.toString());

            System.out.println("Table initial cards");

            //Selecting 3 initial table cards
            dealerCards.add(extractCard());
            dealerCards.add(extractCard());
            dealerCards.add(extractCard());

        }

        public boolean done()
        {
            if(phase=="restart")
            {
                System.out.println("Im restarted");

                return true;
            }
            return false;

        }

        public int onEnd(){
            // Hace que el comportamiento se reinicie al finalizar.
            reset();
            myAgent.addBehaviour(this);

            return 0;
        }
    }
}
