package Arbre;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Scanner;

public class AgentInt extends Agent {
	

	private static final long serialVersionUID = 4424337548881283562L;

	protected void setup() 
	{ 
		System.out.println(getLocalName()+ "--> Installed");  
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Intermediaire");
		sd.setName("AgentIntermediaire");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
		addBehaviour(new attente()); 
	}
	
	public class attente extends CyclicBehaviour {

		private static final long serialVersionUID = 3216635722535044680L;

		@SuppressWarnings("null")
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null) { 
				try {
					String s = message.getContent();
					Scanner sc = new Scanner(s);
					if (sc.nextLine().equalsIgnoreCase("insert")) {
						//insertion
						message.removeReceiver(getAID());
						message.addReceiver(new AID("Root",AID.ISLOCALNAME));
						send(message);						
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

