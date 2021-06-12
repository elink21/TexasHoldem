package book;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class dealerGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private String add="C:/Users/DQ/Pictures/texasResources/";

    private DealerAgent dealerAgent;
    private boolean playerCardsVisible= false;



    private JTextField betField, raiseField;

    dealerGUI(DealerAgent a) {
        super(a.getLocalName());


        dealerAgent = a;

        getContentPane().setLayout(new GridLayout(4,6,10,10));
        //Adding dealer symbol
        for(int i=0;i<2;i++)
        {
            getContentPane().add(new JPanel());
        }
        Image dealerIcon=this.createImage("dealer.png",90,90);
        getContentPane().add(new JLabel(new ImageIcon(dealerIcon)));

        for(int i=0;i<3;i++)
        {
            getContentPane().add(new JPanel());
        }

        //Adding flop cards
        getContentPane().add(new JPanel());
        for(int i=0;i<5;i++)
        {
            JLabel card= new JLabel();

            Image cardImg=this.createImage("back/Emerald.png", 76,114);
            getContentPane().add(new JLabel(new ImageIcon(cardImg)));
        }

        //Adding buttons
        for (int i=0;i<12;i++)
        {
            getContentPane().add(new JButton(Integer.toString(i)));
        }



        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dealerAgent.doDelete();
            }
        });

        setResizable(false);
    }

    public Image createImage(String imagePath, int width, int height)
    {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(add+imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = img.getScaledInstance(width,height,
                Image.SCALE_SMOOTH);
        return dimg;
    }

    public void paintScene(String dealerMSG,ArrayList<Card> dealerCards,
                          ArrayList<Card> playerCards, ArrayList<Card> gamblerCards,
                           ArrayList<Card> noobCards,String phase,
                           int actualWallet, int actualBet, boolean PlayerTurn)
    {
        if(phase.equalsIgnoreCase("dealing"))
        {
            playerCardsVisible=true;
        }
        getContentPane().removeAll();
        getContentPane().setLayout(new GridLayout(4,6,10,10));
        //Adding dealer symbol
        for(int i=0;i<2;i++)
        {
            getContentPane().add(new JPanel());
        }
        Image dealerIcon=this.createImage("dealer.png",90,90);
        getContentPane().add(new JLabel(new ImageIcon(dealerIcon)));

        JLabel dealerMsgLabel= new JLabel(dealerMSG);
        dealerMsgLabel.setSize(400,100);
        getContentPane().add(dealerMsgLabel);


        for(int i=0;i<1;i++)
        {
            getContentPane().add(new JPanel());
        }
        JButton restartButton= new JButton("Restart dealer");
        getContentPane().add(restartButton);


        //Adding flop cards
        getContentPane().add(new JLabel("Table cards"));
        for(int i=0;i<5;i++)
        {
            JLabel card= new JLabel();
            Image cardImg=this.createImage("back/Pomegranate.png", 76,114);

            if(i<dealerCards.size()){
                Card actualFlipped= dealerCards.get(i);
                cardImg= this.createImage(
                        actualFlipped.getCardType()+"/"+actualFlipped.getCardNumber()+".png",
                        76,114);
            }
            getContentPane().add(new JLabel(new ImageIcon(cardImg)));
        }

        //Adding playerCards

            Image cardImg=this.createImage("back/Pomegranate.png", 76,114);

            for(int i=0;i<gamblerCards.size();i++)
            {
                if(phase.equalsIgnoreCase("flop") || phase.equalsIgnoreCase("winnerDeclaration")|| phase.equalsIgnoreCase("a")) {
                    Card actualCard = gamblerCards.get(i);
                    cardImg = this.createImage(
                            actualCard.getCardType() + "/" + actualCard.getCardNumber() + ".png",
                            76, 114);
                }
                getContentPane().add(new JLabel(new ImageIcon(cardImg)));
            }

        cardImg=this.createImage("back/Pomegranate.png", 76,114);


        for(int i=0;i<playerCards.size();i++)
        {
            if(playerCardsVisible) {
                Card actualCard = playerCards.get(i);
                cardImg = this.createImage(
                        actualCard.getCardType() + "/" + actualCard.getCardNumber() + ".png",
                        76, 114);
            }
            getContentPane().add(new JLabel(new ImageIcon(cardImg)));
        }

        cardImg=this.createImage("back/Pomegranate.png", 76,114);



        for(int i=0;i<noobCards.size();i++)
        {
            if(phase.equalsIgnoreCase("flop")|| phase.equalsIgnoreCase("winnerDeclaration")|| phase.equalsIgnoreCase("a")) {
                Card actualCard = noobCards.get(i);
                cardImg = this.createImage(
                        actualCard.getCardType() + "/" + actualCard.getCardNumber() + ".png",
                        76, 114);
            }
            getContentPane().add(new JLabel(new ImageIcon(cardImg)));
        }

        JButton foldButton= new JButton("Fold");
        JButton betButton= new JButton("Bet 10");
        JButton raiseButton= new JButton("Raise x2");
        JButton checkButton= new JButton("Check");


        getContentPane().add(foldButton);
        getContentPane().add(betButton);
        getContentPane().add(raiseButton);
        getContentPane().add(checkButton);

        //Adding functions to the button
        foldButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    dealerAgent.setPlayerAction("fold");
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(dealerGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );

        restartButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    dealerAgent.restartBehaviour();
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(dealerGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );

        if(phase!="a")
        {
            restartButton.setEnabled(false);
        }

        if(dealerAgent.getBet()>0)
        {
            betButton.setEnabled(false);
        }

        if(dealerAgent.getBet()==0)
        {
            raiseButton.setEnabled(false);
        }



        betButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    dealerAgent.setPlayerAction("bet");
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(dealerGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );

        raiseButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    dealerAgent.setPlayerAction("raise");
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(dealerGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );

        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    dealerAgent.setPlayerAction("check");
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(dealerGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );

        if(!PlayerTurn)
        {
            betButton.setEnabled(false);
            checkButton.setEnabled(false);
            raiseButton.setEnabled(false);
            foldButton.setEnabled(false);
        }

        getContentPane().add(new JLabel("Wallet: "+ Integer.toString(actualWallet)));
        getContentPane().add(new JLabel("Bet:"+ Integer.toString(actualBet)));

        getContentPane().revalidate();
        getContentPane().repaint();
    }




    public void showGui() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        super.setSize(900,700);
        super.setVisible(true);
    }

}
