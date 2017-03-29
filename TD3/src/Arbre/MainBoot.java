package Arbre;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class MainBoot {
	
	public static void main(String[] args) {
		String MAIN_PROPERTIES_FILE = "MainProperties";
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			AgentContainer mc = rt.createMainContainer(p);
			AgentController ac = mc.createNewAgent("AgentInt","Arbre.AgentInt", null);
			AgentController ac2 = mc.createNewAgent("Root","Arbre.AgentNode", null);
			ac.start();
			ac2.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}



