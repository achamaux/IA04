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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import Sudoku.AgentEnv.EnvironnementAnalyseCorresp;

public class AgentEnv extends Agent {

	private static final long serialVersionUID = 4491698575470555772L;
	public static enum Zone {LINE, COLUMN, SQUARE};
	public static Sudoku sudoku=new Sudoku("grille5");
	HashMap<AID, EnvironnementAnalyseCorresp> connectionArray = new HashMap<AID, EnvironnementAnalyseCorresp>();
	
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
			
			//Gestion des diff�rents types de message
			if (message != null){
				if (message.getPerformative() == ACLMessage.REQUEST){
					addBehaviour(new EnvUpdateSudokuBehaviour(message));
				}else if (message.getPerformative() == ACLMessage.INFORM_REF){
					//Message provenant de Simul pour une registration d'Analyse : on met � jour la table de correspondance
					addBehaviour(new EnvCreateLinkBehaviour(message));
				}else if (message.getPerformative() == ACLMessage.INFORM){
					//Message provenant de Simul pour une registration d'Analyse : on met � jour la table de correspondance
					//
				}else{
					System.out.println("Message with performative " + message.getPerformative() + " is not processed");
				}
			}
			else
				block();
		}
		
		
		
		public class EnvCreateLinkBehaviour extends OneShotBehaviour {

			private static final long serialVersionUID = 2630777042847231504L;

			public EnvCreateLinkBehaviour(ACLMessage message) {
				m_listOfAnalAgentmessage = message;
			}

			@Override
			public void action() {
				
				//Creating the link between Analyse Agent and Zone
				AID anaAgentId = (AID) m_listOfAnalAgentmessage.getAllReplyTo().next();
				setConnectionAnalyseStructure(anaAgentId);
				
				//Envoi de la premi�re requ�te d'envoi
				addBehaviour(new EnvSendRequestToAnalBehaviour(anaAgentId, m_listOfAnalAgentmessage.getSender()));
				
			}
			
			//Members
			private ACLMessage m_listOfAnalAgentmessage;
			
			public class EnvSendRequestToAnalBehaviour extends OneShotBehaviour {
				
				private static final long serialVersionUID = -6688619890337227120L;
				
				public EnvSendRequestToAnalBehaviour(AID receiver, AID simul) {
					m_receiver = receiver;
					m_simul = simul;
				}

				@Override
				public void action() {
					ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
					order.addReceiver(m_receiver);
					
					//On v�rifie d'abord que le sudoku n'est pas d�j� r�solu
					if (!sudoku.isDone()){
						//On r�cup�re les listes de case pour un agent d'Analyse donn�
						ArrayList<Cellule> zoneCases = getListOfCasesFromAID(m_receiver);
						
						//Parsing de la requ�te
						ObjectMapper writerMapper = new ObjectMapper();
						try{
							HashMap<String, ArrayList<Cellule>> casesMap = new HashMap<String, ArrayList<Cellule>>();
							casesMap.put("cases", zoneCases);
							HashMap<String, Object> contentMap = new HashMap<String, Object>();
							contentMap.put("content", casesMap);
							StringWriter sw = new StringWriter();
							writerMapper.writeValue(sw, contentMap);
							order.setContent(sw.toString());
						}catch (Exception e){
							e.printStackTrace();
						}
						
						myAgent.send(order);
						
					} else {
						//On envoie un message � Simul pour lui dire que le Sudoku est r�solu
						ACLMessage sudokuSolvedMessage = new ACLMessage(ACLMessage.CONFIRM);
						sudokuSolvedMessage.addReceiver(m_simul);
						myAgent.send(sudokuSolvedMessage);
					}

				}
				
				//Members
				private AID m_receiver;
				private AID m_simul;

			}

		}	
	}
		
	
	public void setConnectionAnalyseStructure (AID analyseId) {
		int numberOfEntries = EnvironnementAnalyseCorresp.getNumberOfEntries();
		if(numberOfEntries < 27){
			Zone type;
			if (numberOfEntries < 9) //from 0 to 8 -> LINE
				type = Zone.LINE;
			else if (numberOfEntries < 18) //from 9 to 17 -> COLUMN
				type = Zone.COLUMN;
			else //from 18 to 26 -> SQUARE
				type = Zone.SQUARE;
			connectionArray.put(analyseId, new EnvironnementAnalyseCorresp(numberOfEntries%9, type, analyseId));
		}
		else
			System.out.println("Too many agents try to resolve the sudoku");
	}
	
	public ArrayList<Cellule> getListOfCasesFromAID(AID id){
		/**
		 * @brief Construit la liste des cases pour un agent d'analyse donn�
		 */
		
		//L'index est le num�ro de ligne, colonne ou carr�
		//Le type peut �tre ligne, colonne ou carr�
		Zone type = connectionArray.get(id).getType();
		int index = connectionArray.get(id).getIndex();
		ArrayList<Cellule> newList = new ArrayList<>(9);
		
		if (type == AgentEnv.Zone.LINE){
			//La zone est une ligne
			for(int i = 0; i < 9; i++){
				newList.add(sudoku.Grille[index][i]);
			}
			
		} else if (type == AgentEnv.Zone.COLUMN){
			//La zone est une colonne
			for(int i = 0; i < 9; i++){
				newList.add(sudoku.Grille[i][index]);
			}
			
		} else{
			//La zone est un carr�
			
			//On prend pour point de d�part la case en haut � gauche du carr�
			//Ainsi pour le carr� num�ro 3 (centre gauche du sudoku) :
			//i = (3 / 3) * 3 = 1 * 3 = 3
			//j = (3 % 3) * 3 = 0 * 3 = 0
			int starti = (index / 3) * 3, i = starti;
			int startj = (index % 3) * 3, j = startj;
			
			while(i != starti + 3){
				newList.add(sudoku.Grille[i][j]);

				if ((j+1) % 3 == 0){
					//Si on a visit� trois cases on passe � la colonne suivante
					i++; j = startj;
				}
				else
					//Sinon on passe � la ligne suivante pour une m�me colonne
					j++;
			}
			
		}
		
		return newList;
	}
	
	static class EnvironnementAnalyseCorresp {
		private Integer m_index;	//Le num�ro de ligne, colonne, carr�
		private Zone m_type;	//Le type de zone (ligne colonne ou carr�)
		private AID m_analyseId;	//L'AID de l'agent
		private static int numberOfEntries = 0;	//Le nombre d'agents enregistr�s
		
		public EnvironnementAnalyseCorresp(int index, Zone type, AID analyseId) {
			m_index = index;
			m_type = type;
			m_analyseId = analyseId;
			numberOfEntries++;
		}

		public Integer getIndex() {
			return m_index;
		}
		
		public static int getNumberOfEntries() {return numberOfEntries;}

		public void setIndex(Integer m_index) {
			this.m_index = m_index;
		}

		public Zone getType() {
			return m_type;
		}

		public void setType(Zone m_type) {
			this.m_type = m_type;
		}

		public AID getAnalyseId() {
			return m_analyseId;
		}

		public void setAnalyseId(AID m_analyseId) {
			this.m_analyseId = m_analyseId;
		}
	}
}

