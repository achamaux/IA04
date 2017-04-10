package Sudoku;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentEnv extends Agent {
	private static final long serialVersionUID = 3L;
	private Sudoku sudoku;
	
	public void setup() {
		sudoku = new Sudoku();
		this.addBehaviour(new Listen());
		for (int i=0; i<27; i++) {
			List<Cellule> ce = sudoku.getCellules(i);
			System.out.print("group : " + i + "---");
			for (Cellule c : ce)
				System.out.print(c.getValue() + " ");
			System.out.println();
			try {
				this.getContainerController().createNewAgent("analyse"+String.valueOf(i), "Sudoku.AgentAnalyse", null);
				this.getContainerController().getAgent("analyse"+String.valueOf(i)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public class Listen extends Behaviour {
		private static final long serialVersionUID = 31L;

		public void action() {
			ACLMessage message = null;
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			while ( (message = receive(mt)) != null) {
				int bloc = Integer.parseInt(message.getContent());
				AID agent = (AID) message.getAllReplyTo().next();
				String id = UUID.randomUUID().toString();
				
				this.getAgent().addBehaviour(new getAnalyse(bloc, agent, id));
			}
			mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			if ( (message = receive(mt)) != null) {
				if (message.getContent() != null)
					sudoku.print(true);
				else
					sudoku.print(false);
			}
		}

		public boolean done() {
			return false;
		}
		
	}
	
	public class getAnalyse extends Behaviour {
		private static final long serialVersionUID = 32L;
		private int bloc;
		private AID agentAna;
		private String UniqueID;
		private int state;
		
		getAnalyse(int bloc, AID analyste, String UniqueID) {
			this.bloc = bloc;
			this.agentAna = analyste;
			this.UniqueID = UniqueID;
			state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			if (state == 0) {
				try {
					message = new ACLMessage(ACLMessage.REQUEST);
					message.addReceiver(agentAna);
					message.setContent(new ObjectMapper().writeValueAsString(sudoku.getCellules(bloc)));
					message.setConversationId(UniqueID);
					send(message);
					state = 1;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (state == 1) {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(UniqueID));
				if ((message = receive(mt)) != null) {
					try {
						if (sudoku.isDone() == 0) {
							ObjectMapper mapper = new ObjectMapper();
							List<Cellule> cellules = mapper.readValue(message.getContent(), mapper.getTypeFactory().constructCollectionType(List.class, Cellule.class));
							sudoku.setCellules(bloc, cellules);
							if (sudoku.isDone() == 1) {
								// Envoyer "Fin"
								ACLMessage m = new ACLMessage(ACLMessage.INFORM);
								m.addReceiver(new AID("simulation", AID.ISLOCALNAME));
								m.setContent("Fin");
								send(m);
								sudoku.print(false);
								if (sudoku.isCorrect() == 1)
									System.out.println("Sudoku OK");
								else
									System.out.println("Erreur");
							}
						}
						state++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public boolean done() {
			return (state == 2);
		}
	}
}

