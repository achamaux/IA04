package Sudoku;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentAnalyse extends Agent {
	private static final long serialVersionUID = 2L;

	public void setup() {
		//System.out.println(getLocalName() + "--> installed");
		ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
		message.addReceiver(new AID("simulation", AID.ISLOCALNAME));
		send(message);
		addBehaviour(new SearchValue());
	}
	
	
	public class SearchValue extends Behaviour{
		private static final long serialVersionUID = 21L;
		public void action() {
			try {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage message = receive(mt);
				if (message != null) {
					ObjectMapper mapper = new ObjectMapper();
					List<Cellule> cellules = mapper.readValue(message.getContent(), mapper.getTypeFactory().constructCollectionType(List.class, Cellule.class));
					
					
					//   Retrait des valeurs déjà trouvées
					for (Cellule c : cellules) {
						int val = c.getValue();
						if (val > 0 ) {
							for (Cellule c2 : cellules) {
								if (c2.getPossibles().contains(val)) {
									c2.removePossible(val);
								}
							}							
						}
					}
					
					// recherche et definition des cellules n'ayant qu'une valeur possible
					for (Cellule c : cellules) {
						int val = c.getValue();
						List<Integer> possible = c.getPossibles();
						if (val == 0 && possible.size() == 1) {
							c.defineValue(possible.get(0));
						}
					}
					
					
					// Recherche des valeurs non encore définies
					List<Integer> notDefined = new ArrayList<Integer>();
					for (int i=1; i<10; i++)
						notDefined.add(i);
					for (Cellule c : cellules) {
						if (c.getValue() > 0)
							notDefined.remove((Integer)c.getValue());
					}
					//  Recherche des valeurs non encore définies n'aparaissant que dans une cellule					
					for (int i : notDefined) {
						int cell = 0;
						int nb = 0;
						for (int index=0; index<9; index++) {
							if (cellules.get(index).getPossibles().contains(i)) {
								cell = index;
								nb++;
							}
						}
						// Si elle n'apparait qu'une fois alors on la définit,
						// pas besoin de la retirer des autres possibles 
						// puisqu'elle n'apparait pas ailleurs
						if (nb == 1) {
							cellules.get(cell).defineValue(i);
						}
					}
					
					//  Recherche d'un doublon de valeurs n'aparaissant que dans deux cellules
					List<Integer> cells = new ArrayList<Integer>();
					for (int i=1; i<10; i++) {
						for (int j=i+1; j<10; j++) {
							for (int k=0; k<9; k++) {
								List<Integer> l = cellules.get(k).getPossibles();
								if (l.size() == 2 && l.contains(i) && l.contains(j)) {
									cells.add(k);
								}
							}
							//   Retrait des valeurs trouvées en doublon   
							if (cells.size() == 2) {
								int cell1 = cells.get(0);
								int cell2 = cells.get(1);
								for (int k=0; k<9; k++) {
									if (k != cell1 && k != cell2) {
										cellules.get(k).removePossible(i);
										cellules.get(k).removePossible(j);
									}
								}
							}
							cells.clear();
						}
					}
					
					message = message.createReply();
					message.setPerformative(ACLMessage.INFORM);
					message.setContent(new ObjectMapper().writeValueAsString(cellules));
					send(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		public boolean done() {
			return false;
		}
		
	}
}
