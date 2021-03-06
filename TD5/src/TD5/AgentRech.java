package TD5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentRech extends Agent {
	
	private static final long serialVersionUID = -9074818395516468244L;

	public void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new CyclicBehaviour() {
			//Attente d'une requ�te
			private static final long serialVersionUID = 1743225391371670328L;
			
			@Override
			public void action() {
				ACLMessage msg=receive();
				if(msg!=null){
					System.out.println(getLocalName() + " re�oit : "+msg.getContent());
					String query = msg.getContent();
					Model model = ModelFactory.createDefaultModel();
					try {
						model.read(new FileInputStream("ABOX.n3"),null, "TURTLE");	
						ACLMessage reply = msg.createReply();
						reply.setContent(runSelectQuery(query,model));
						send(reply);
						
					} catch (FileNotFoundException e) {
					e.printStackTrace();
					}
				}
				else block();
			}
		});
	}
	
	public static void test() {
		String query = "requete_foaf.sparql"; // fichier contenant la requ�te
		//String query = "requete.sparql";
		Model model = ModelFactory.createDefaultModel();
		try {
			//model.read(new FileInputStream("ABOX.n3"),null, "TURTLE");	
			model.read(new FileInputStream("foaf.n3"),null, "TURTLE");
			runSelectQuery(query, model);
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		}
	}
	
	public static void test2() {
		String query = "requete_distante.sparql"; // fichier contenant la requ�te
		runSelectQueryDistance(query);
	}
	
	public static String runSelectQuery(String qfilename, Model model) {
		//Query query = QueryFactory.read(qfilename);
		QueryExecution queryExecution = QueryExecutionFactory.create(qfilename, model);
		ResultSet r = queryExecution.execSelect();
		//ResultSetFormatter.out(System.out,r);
		String temp =  ResultSetFormatter.asText(r);
		queryExecution.close();
		return temp;
	}
	
	public static void runSelectQueryDistance(String qfilename) {
		Query query = QueryFactory.read(qfilename);
		System.setProperty("http.proxyHost","proxyweb.utc.fr");
		System.setProperty("http.proxyPort","3128");
		System.out.println("Query sent");
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService( "http://linkedgeodata.org/sparql", query);
		ResultSet r = queryExecution.execSelect();
		ResultSetFormatter.out(System.out,r);//r�cup�re le string � partir de cette ligne
		queryExecution.close();
	}
}

