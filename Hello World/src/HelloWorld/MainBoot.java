package HelloWorld;
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
			AgentController ac = mc.createNewAgent("HelloWorld",
					"HelloWorld.HelloWorld", null);
			ac.start();

		}
		catch(Exception ex) {}
	}

}



