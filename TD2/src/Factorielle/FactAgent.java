package Factorielle;
import jade.core.AID;
import jade.core.Agent;
import java.util.*;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FactAgent extends Agent {
	
	private static final long serialVersionUID = 3152901013935862384L;

	protected void setup() 
	{ 
		System.out.println(getLocalName()+ "--> Installed");  
		addBehaviour(new attente()); 
	}
	
	public class attente extends CyclicBehaviour {
	
		private static final long serialVersionUID = 2201141493158510933L;
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt); 
			if (message != null) { 
				addBehaviour(new CalculFact(message));
			} 
			else 
				block();
		}
		public class CalculFact extends OneShotBehaviour {

			private static final long serialVersionUID = -1115864184981212646L;
			int Fact;
			long timer_start = 0;
			
			public CalculFact(ACLMessage message) {
				Fact = Integer.parseInt(message.getContent());
			}
			
			public void envoiMult(String message) {
				ACLMessage envoi = new ACLMessage(ACLMessage.REQUEST);
				ObjectMapper mapper = new ObjectMapper();	
				try {
					String jsonInString = mapper.writeValueAsString(message);
					envoi.setContent(jsonInString);
					AID Receiver = getMultiplicator();
					envoi.addReceiver(getMultiplicator());
					System.out.println(getName()+ " envoie "+ jsonInString + " à " + Receiver.getName());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				send(envoi);
			}
			
			protected AID getMultiplicator() throws FIPAException {
				DFAgentDescription template = new DFAgentDescription();
				//dfd.setName(getAID());
				ServiceDescription sdd = new ServiceDescription();
				sdd.setType("Operations");
				sdd.setName("Multiplication");
				template.addServices(sdd);
				DFAgentDescription[] result =
						DFService.search(myAgent, template);
				return result[(int)(Math.random() * result.length)].getName();
			}
			
			public long receptMult(ACLMessage message) {
				ObjectMapper mapper = new ObjectMapper();	
				try {
					String jsonInString = mapper.readValue(message.getContent(), String.class);
					System.out.println(getName()+ " reçoit "+ jsonInString + " de " + message.getSender().getName());
					return Integer.parseInt(jsonInString);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
			
			public void action() {
				
				System.out.println("");
				System.out.println("-------------------------------------");
				System.out.println("Calcul de la factorielle : " + Fact);
				System.out.println("-------------------------------------");
				System.out.println("");
				timer_start = java.lang.System.currentTimeMillis();
				Stack<Long> st = new Stack<Long>();
				long i,tmp,resultat,compteur;
				String mes;
				i = Fact; compteur = 0;
				
				//Initialization of stack
				while (i>1) {
					st.push(i);
					i--;
				}
				//Stack show
				System.out.println("Pile: " + st);
				MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				
				while (!((st.size() == 1) && (compteur == 0)))  {
					ACLMessage message = receive(mt2);
					if (message != null) { 
						try {
							tmp = receptMult(message);
							st.push(tmp);
							System.out.println("Pile: " + st);
							compteur--;
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					} 
					else 
					{
						while (st.size() > 1) {
							try {
								mes = st.pop() + " x " + st.pop();
								envoiMult(mes);
								//Stack show
								System.out.println("Pile: " + st);
								compteur++;					
							}
							catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				
				}
				resultat = st.pop();
				long timer = java.lang.System.currentTimeMillis() - timer_start ; 
				System.out.println("Le résultat est : " + resultat);
				System.out.println("Calcul effectué en " + timer + "ms"); 
			}	
		}
	}
 }
