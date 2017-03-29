package HelloWorld;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class HelloMain {

	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl("SecondProperties");
			ContainerController cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("HelloWorld1",
					"HelloWorld.HelloWorld", null);
			ac.start();
		} catch (Exception ex) {
		ex.printStackTrace();
		}
	}

}
