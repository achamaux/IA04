package Factorielle;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.HashMap;
import jade.util.leap.Map;

public class StockAgent extends Agent {

	private static final long serialVersionUID = -7912588028253788685L;

	protected void setup() 
	{ 
		System.out.println(getLocalName()+ "--> Installed");  
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Stockage");
		sd.setName("Factorielle");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new attente()); 
	}
	
	public class attente extends CyclicBehaviour {

		private static final long serialVersionUID = -7826181608873106603L;
		Map tab = new HashMap();
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message_inform = receive(mt); 
			ACLMessage message_request = receive(mt2);
			
			//message reception
			if (message_inform != null) { 
				stockage(message_inform); 
			} 
			else
			{
				if (message_request != null) {
					answer(message_inform);
				}
				else 
				{
					block();
				}
			}
		}

		private void answer(ACLMessage message) {

			int n = Integer.parseInt(message.getContent());
			ACLMessage reply = message.createReply();
		    if (tab.containsKey(n)) {
		    	String res = (String) tab.get(n);
		    	reply.setPerformative(ACLMessage.INFORM); 
				reply.setContent(res);
		    }
		    else {
				reply.setPerformative(ACLMessage.FAILURE); 
				reply.setContent("fact not found");
		    }
		}

		private void stockage(ACLMessage message) {

			String par = message.getContent();
			if (par.contains(":")) { 
				String[] parameters = par.split(":"); 
				int n = Integer.parseInt(parameters[0].trim());		
				long res = Integer.parseInt(parameters[1].trim()); 
				try {
					tab.put(n,res);
				}
				catch (Exception fe) {
					fe.printStackTrace();
				}
			}  
		}
	}
}

