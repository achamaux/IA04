package Sudoku;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
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
					ACLMessage reponse = new ACLMessage(ACLMessage.INFORM_REF);
					message.addReplyTo(message.getSender());
					message.addReceiver(new AID("AgentEnv", AID.ISLOCALNAME));
					map.put(i, message.getSender());
					i++;
					send(reponse);
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
				ACLMessage message = receive();
				if (message != null){
					if (message.getPerformative() == ACLMessage.CONFIRM)
						stop();
				} else {
					ACLMessage requete = new ACLMessage(ACLMessage.REQUEST);
					requete.addReceiver(new AID("AgentEnv", AID.ISLOCALNAME));
					map.forEach((key,value)->{
						try {
							requete.setContentObject(value);
							send(requete);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				
				}
			}
		    });
		
		//Ticker Behaviour : Envoi de message aux agents analyses toutes les period ms
		ResolutionBehaviour.addSubBehaviour(new OneShotBehaviour(){
				
			private static final long serialVersionUID = -2665058805363369990L;

			@Override
			public void action() {
				// TODO Auto-generated method stub
						
					}
			
	    });
	}
}	
