package TD5;

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
			ac.createNewAgent("Recherche",  "TD5.AgentRech",  null).start();
			ac.createNewAgent("Propagation",  "TD5.PropagateSparql",  null).start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
