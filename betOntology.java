package book;

import jade.content.onto.*;
import jade.content.schema.*;

public class betOntology extends Ontology{

    public static final String ONTOLOGY_NAME = "betOntology";

    public static final String BET = "CARD";
    public static final String BET_AMMOUNT = "ammount";

    public static final String OFFER = "Offer";
    public static final String OFFER_BET = "bet";

    public static final String RAISE = "Raise";
    public static final String RAISE_BET = "bet";

    private static Ontology instance = new betOntology();


    public static Ontology getInstance() {
        return instance;
    }

    private betOntology()
    {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try
        {
            add(new ConceptSchema(BET),Bet.class);
            add(new PredicateSchema(OFFER), Offer.class);
            add(new AgentActionSchema(RAISE) , Raise.class);

            //Concept schema structure
            ConceptSchema cs = (ConceptSchema) getSchema(BET);
            cs.add(BET_AMMOUNT, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            //Structure for OFFER predicate
            PredicateSchema ps = (PredicateSchema) getSchema(OFFER);
            ps.add(OFFER_BET, (ConceptSchema) getSchema(BET));


            AgentActionSchema as = (AgentActionSchema) getSchema(RAISE);
            as.add(RAISE_BET, (ConceptSchema) getSchema(BET));



        }catch (OntologyException e)
        {
            e.printStackTrace();
        }
    }
}
