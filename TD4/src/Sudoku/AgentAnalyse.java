package Sudoku;

import java.io.IOException;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import java.util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentAnalyse extends Agent{

	private static final long serialVersionUID = 206720546310469258L;

	protected void setup() 
	{ 	
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Analyse");
		sd.setName("Analyse");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
		message.setContent("inscription");
		message.addReceiver(new AID("AgentSim", AID.ISLOCALNAME));
		send(message);
		
		
		addBehaviour(new AgentAnalyseBehaviour());
	}
	}
}
