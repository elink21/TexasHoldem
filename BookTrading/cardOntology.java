package book;


import jade.content.onto.*;
import jade.content.schema.*;
public class cardOntology extends Ontology{
    public static final String ONTOLOGY_NAME = "cardOntology";

    public static final String CARD = "CARD";
    public static final String CARD_TYPE = "type";
    public static final String CARD_NUMBER = "number";

    public static final String OFFER = "Offer";
    public static final String OFFER_CARD = "card";

    public static final String DEAL = "Deal";
    public static final String DEAL_CARD = "card";

    private static Ontology instance = new cardOntology();


    public static Ontology getInstance() {
        return instance;
    }

    private cardOntology()
    {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try
        {
            add(new ConceptSchema(CARD),Card.class);
            add(new PredicateSchema(OFFER), Offer.class);
            add(new AgentActionSchema(DEAL) , Deal.class);

            //Concept schema structure
            ConceptSchema cs = (ConceptSchema) getSchema(CARD);
            cs.add(CARD_TYPE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(CARD_NUMBER, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            //Structure for OFFER predicate
            PredicateSchema ps = (PredicateSchema) getSchema(OFFER);
            ps.add(OFFER_CARD, (ConceptSchema) getSchema(CARD));


            AgentActionSchema as = (AgentActionSchema) getSchema(DEAL);
            as.add(DEAL_CARD, (ConceptSchema) getSchema(CARD));



        }catch (OntologyException e)
        {
            e.printStackTrace();
        }
    }

}
