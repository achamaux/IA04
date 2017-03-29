package Factorielle;
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
			AgentController ac = mc.createNewAgent("MultAgent",
					"Factorielle.MultAgent", null);
			AgentController ac2 = mc.createNewAgent("MultAgent2",
					"Factorielle.MultAgent", null);
			AgentController ac3 = mc.createNewAgent("StockAgent",
					"Factorielle.StockAgent", null);
			AgentController ac4 = mc.createNewAgent("FactAgent",
					"Factorielle.FactAgent", null);
			ac.start();
			ac2.start();
			ac3.start();
			ac4.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}



