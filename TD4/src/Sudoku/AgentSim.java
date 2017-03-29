package Sudoku;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.HashMap;

public class AgentSim extends Agent {
	
	private static final long serialVersionUID = 997075021822159306L;
	//public AID[] AgentsAnalyse=new AID[27]; 
	HashMap<Integer, AID> map = new HashMap<Integer, AID>();
	
	public long period = 1000;

	protected void setup() 
	{ 
		System.out.println(getLocalName()+ "--> Installed");  
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Simulation");
		sd.setName("Simulation");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
		SequentialBehaviour ResolutionBehaviour = new SequentialBehaviour();
		
		//Attente de souscription
		ResolutionBehaviour.addSubBehaviour(new Behaviour(){

			private static final long serialVersionUID = -8978420565209344880L;
			protected int i = 0;
			@Override
			public void action() {
				ACLMessage message = myAgent.receive(); //reception des souscriptions
				if (message != null) {
					ACLMessage reponse = message.createReply();
					reponse.setPerformative(ACLMessage.INFORM);
					reponse.setContent("Tu as le num�ro : " + String.valueOf(i)); 
					map.put(i, message.getSender());
					System.out.println("L \' agent " + message.getSender() + "re�oit le num�ro " + i);
					i++;
					myAgent.send(reponse);
				}
			}
			
			public boolean done() {
				if (i>=27) return true;
				else return false;
			}
		});
		
		//Ticker Behaviour : Envoi de message aux agents analyses toutes les period ms
		ResolutionBehaviour.addSubBehaviour(new TickerBehaviour(this, period){
		
			private static final long serialVersionUID = -2665058805363369990L;

			protected void onTick() {
		        
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.addReceiver(new AID("AgentEnv", AID.ISLOCALNAME));
				map.forEach((key,value)->{
					try {
						message.setContentObject(value);
						send(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				
		      } 
		    });
	}
}	
