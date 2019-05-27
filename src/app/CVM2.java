package app;

import java.util.Vector;
import components.Consommateur;
import components.Courtier;
import components.Producteur;
import connectors.PublicationServiceConnector;
import connectors.ReceptionServiceConnector;
import connectors.SouscriptionServiceConnector;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import interfaces.PublicationServiceI;
import interfaces.ReceptionServiceI;
import interfaces.SouscriptionServiceI;

public class CVM2 extends AbstractCVM {

	protected Courtier courtier;
	protected Vector<Consommateur> consommateurs = new Vector<Consommateur>();
	protected Vector<Producteur> producteurs = new Vector<Producteur>();
	protected long debut;

	public CVM2() throws Exception {
		super();
		for (int i = 0; i < 5; i++) {
			Consommateur c = new Consommateur();
			consommateurs.add(c);
		}

		for (int i = 0; i < 10; i++) {
			Producteur c = new Producteur();
			producteurs.add(c);
		}
	}

	@Override
	public void deploy() throws Exception {

		assert !this.deploymentDone();

		this.courtier = new Courtier("outTransfertC1", "inTransfertC1", 5);
		this.deployedComponents.add(courtier);

		for (Producteur p : producteurs) {
			this.deployedComponents.add(p);
			p.doPortConnection(p.findOutboundPortURIsFromInterface(PublicationServiceI.class)[0],
					courtier.findInboundPortURIsFromInterface(PublicationServiceI.class)[0],
					PublicationServiceConnector.class.getCanonicalName());
		}

		int i = 0;
		for (Consommateur c : this.consommateurs) {

			this.deployedComponents.add(c);

			// Connexion entre courtier et consommateur
			this.courtier.doPortConnection(courtier.findOutboundPortURIsFromInterface(ReceptionServiceI.class)[i],
					c.findInboundPortURIsFromInterface(ReceptionServiceI.class)[0],
					ReceptionServiceConnector.class.getCanonicalName());

			// Connexion entre consommateur et courtier
			c.doPortConnection(c.findOutboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					courtier.findInboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					SouscriptionServiceConnector.class.getCanonicalName());
			i++;
		}

		super.deploy();
		assert this.deploymentDone();
	}

	@Override
	public void shutdown() throws Exception {
		assert this.allFinalised();

		// print logs on files, if activated
		this.courtier.toggleLogging();

		for (Producteur p : producteurs) {
			p.toggleLogging();
			p.printExecutionLogOnFile("Producteur");
		}
		for (Consommateur c : consommateurs) {
			c.toggleLogging();
			c.printExecutionLogOnFile("Consommateur");

		}
		this.courtier.printExecutionLogOnFile("Courtier");
		super.shutdown();
	}
	
	@Override
	public boolean	startStandardLifeCycle(long duration)
	{
		try {
			assert duration	> 0 ;
			this.deploy() ;
			System.out.println("starting...") ;
			this.start() ;
			debut = System.currentTimeMillis();
			System.out.println("executing...") ;
			this.execute() ;
			Thread.sleep(duration) ;
			System.out.println("finalising...") ;
			this.finalise() ;
			System.out.println("shutting down...") ;
			this.shutdown() ;
			System.out.println("ending...") ;
			
			return true ;
		} catch (Exception e) {
			e.printStackTrace() ;
			return false ;
		}
	}

	public static void main(String[] args) {
		try {
			// Create an instance of the defined component virtual machine.
			CVM2 a = new CVM2();
			// Execute the application.
			a.startStandardLifeCycle(15000L);
			
			while(true) {
				if(a.isShutdown()) {
					long fin = 0;
					for (ComponentI c : a.deployedComponents) {
						if(c instanceof Consommateur) {
							if(fin < ((Consommateur) c).getLastDateMessage()) {
								fin = ((Consommateur) c).getLastDateMessage();
							}
						}
					}
					System.out.println("Temps de reception de tout les messages par les consommateurs : "
							+ String.valueOf(fin-a.debut-2000) + " ms");
					break;
				} else {
					Thread.currentThread().wait();
				}
			}
			
			// Give some time to see the traces (convenience).
			Thread.sleep(10000L);
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}