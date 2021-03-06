package TD5;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PropagateSparql extends Agent {
	
	private static final long serialVersionUID = -9074818395516468244L;

	public void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new CyclicBehaviour() {
			String head =  "PREFIX dc:      <http://purl.org/dc/elements/1.1/>"+"\n"+
						   "PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>"+"\n"+
						   "PREFIX wot:     <http://xmlns.com/wot/0.1/>"+"\n"+
					       "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"+"\n"+
					       "PREFIX owl:     <http://www.w3.org/2002/07/owl#>"+"\n"+
					       "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+"\n"+
					       "PREFIX vs:      <http://www.w3.org/2003/06/sw-vocab-status/ns#>";
			
			//Envoi de la requete sparql vers l'Agent KB.
			private static final long serialVersionUID = 1743225391381670328L;
			
			@Override
			public void action() {
				ACLMessage msg=receive();
				if(msg!=null){
					addBehaviour(new OneShotBehaviour() {
						//Envoi de la requete sparql vers l'Agent KB.
						private static final long serialVersionUID = 1743225391381670328L;
						
						@Override
						public void action() {
							System.out.println(getLocalName() + " re�oit : "+msg.getContent());
							String requete = msg.getContent();
							msg.setContent(head + "\n" + requete);
							msg.addReplyTo(new AID("Recherche",AID.ISLOCALNAME));
							msg.setPerformative(ACLMessage.PROPAGATE);
							send(msg);
						}
					});
				}
			}
		});
	}
}

