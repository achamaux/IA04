package Sudoku;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.tools.sniffer.Message;

import java.util.HashMap;

public class AgentEnv extends Agent {

	private static final long serialVersionUID = 4491698575470555772L;

	protected void setup()
	{
		System.out.println(getLocalName()+ "--> Installed");  
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Environnement");
		sd.setName("Environnement");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
		addBehaviour(new ReceptionBehaviour());
		
	}
	
	public class ReceptionBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = -2918570422442842803L;

		@Override
		public void action() {

			ACLMessage message = receive();
			
			if (message != null){
				if (message.getPerformative() == ACLMessage.REQUEST){
					addBehaviour(new RedirectionBehaviour(message));
				}
			}
			else
				block();
		}
		}

	public class RedirectionBehaviour extends OneShotBehaviour {

		private static final long serialVersionUID = 1138017769430433896L;
		private ACLMessage messageI;
		
		public RedirectionBehaviour(ACLMessage message) {
			messageI = message;
		}

		@Override
		public void action() {
			try {
				messageI.addReplyTo((AID)messageI.getContentObject());
			} catch (UnreadableException e) {
				e.printStackTrace();
			};
		}

	}
}

