package HelloWorld;

import jade.core.Agent;

public class HelloWorld extends Agent {
	private static final long serialVersionUID = 5375256485154119150L;

		protected void setup() {
		 System.out.println(getLocalName()+" : " + "Hello World");
		 }

}
