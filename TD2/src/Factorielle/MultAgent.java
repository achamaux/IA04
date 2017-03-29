package Factorielle;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class MultAgent extends Agent {

	private static final long serialVersionUID = -8736083706819984741L;

	protected void setup() 
	{ 
		System.out.println(getLocalName()+ "--> Installed");  
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operations");
		sd.setName("Multiplication");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new MultBehaviour()); 
	}
	
	public class MultBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 1367337407790475328L;

		private void answer(ACLMessage message) { 
			ObjectMapper mapper = new ObjectMapper();	
			try {
				String jsonInString = mapper.readValue(message.getContent(), String.class);
				String par = jsonInString;
				ACLMessage reply = message.createReply(); 
				if (par.contains("x")) { 
					String[] parameters = par.split("x"); 
					long n = Integer.parseInt(parameters[0].trim()) * Integer.parseInt(parameters[1].trim()); 
					reply.setPerformative(ACLMessage.INFORM); 
					ObjectMapper mapper2 = new ObjectMapper();	
					try {
						String jsonInString2 = mapper2.writeValueAsString(n);
						reply.setContent(jsonInString2);
					}
					catch (Exception fe) {
						fe.printStackTrace();
					}
				} 
				else { 
					reply.setPerformative(ACLMessage.FAILURE); 
					reply.setContent("unknown operator"); } 
				send(reply); 
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			}
		
		@Override
		public void action() {
			ACLMessage message = receive(); 
			if (message != null) { 
				answer(message); 
			} 
			else 
				block();
		}
	}
}
