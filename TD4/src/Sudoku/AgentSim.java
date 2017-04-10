package Sudoku;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentSim extends Agent {
	private static final long serialVersionUID = 1L;
	private List<AID> subscribed;
	
	public void setup() {
		//System.out.println(getLocalName() + "--> installed");
		subscribed = new ArrayList<AID>();
		addBehaviour(new SubscribingBehaviour());
	}
	
	public class SubscribingBehaviour extends Behaviour {
		private static final long serialVersionUID = 11L;
		private int state;
		
		public SubscribingBehaviour() {
			state = 0;
		}
		
		public void action() {
			if (state == 0) {
				ACLMessage message = null;
				if (subscribed.size() < 27) {
					//  Réception des demandes de souscription  
					MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
					while ((message = receive(mt)) != null) {
						subscribed.add(message.getSender());
					}
				}
				if (subscribed.size() >= 27) {
					this.getAgent().addBehaviour(new Ticker(this.getAgent(), 3000));
					state = 1;
				}
			}
		}

		public boolean done() {
			return (state == 1);
		}
		
		
	}
	
	public class Ticker extends TickerBehaviour {
		private static final long serialVersionUID = 12L;
		private AID EnvironnementAid;
		
		public Ticker(Agent a, long period) {
			super(a, period);
			EnvironnementAid = new AID("Environnement", AID.ISLOCALNAME);
		}
		
		protected void onTick() {
			
			// Envoi des messages du Ticker
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if ( message != null && message.getContent().equals("Fin"))
				this.getAgent().doDelete();
			else if (EnvironnementAid != null ){
				for (int i = 0 ; i < subscribed.size() ; i++) {
					message = new ACLMessage(ACLMessage.PROPAGATE);
					message.setContent(String.valueOf(i));
					message.addReceiver(EnvironnementAid);
					message.addReplyTo(subscribed.get(i));
					send(message);
				}
			} else
				EnvironnementAid = new AID("Environnement", AID.ISLOCALNAME);
		}
	}
}

