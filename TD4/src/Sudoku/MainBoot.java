package Sudoku;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;


public class MainBoot {

	public static void main(String[] args) {
		String PROPERTIES_FILE = "MainProperties";
		
		Runtime rt = Runtime.instance();
		Profile p = null;
		
		try {
			p = new ProfileImpl(PROPERTIES_FILE);
			
			rt.createMainContainer(p);
			
			AgentContainer ac = rt.createAgentContainer(p);
			ac.createNewAgent("Environnement",  "Sudoku.AgentEnv",  null).start();
			ac.createNewAgent("Simulation",  "Sudoku.AgentSim",  null).start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
