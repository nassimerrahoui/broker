package app;

import components.Consommateur;
import components.Courtier;
import components.Producteur;
import connectors.PublicationServiceConnector;
import connectors.ReceptionServiceConnector;
import connectors.SouscriptionServiceConnector;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import interfaces.PublicationServiceI;
import interfaces.ReceptionServiceI;
import interfaces.SouscriptionServiceI;

public class DistributedCVM extends AbstractDistributedCVM {
	
	// URI des JVM defini dans le fichier config.xml
	protected static String	COURTIER_1_JVM_URI = "jvm_courtier_1" ;
	protected static String	COURTIER_2_JVM_URI = "jvm_courtier_2" ;
	protected static String	COURTIER_3_JVM_URI = "jvm_courtier_3" ;
	protected static String	COURTIER_4_JVM_URI = "jvm_courtier_4" ;
	
	// URI Port des courtier pour l'interconnexion
	protected static String	URI_OPORT_COURTIER_1 = "oport1" ;
	protected static String	URI_OPORT_COURTIER_2 = "oport2" ;
	protected static String	URI_OPORT_COURTIER_3 = "oport3" ;
	protected static String	URI_OPORT_COURTIER_4 = "oport4" ;
	protected static String	URI_IPORT_COURTIER_1 = "iport1" ;
	protected static String	URI_IPORT_COURTIER_2 = "iport2" ;
	protected static String	URI_IPORT_COURTIER_3 = "iport3" ;
	protected static String	URI_IPORT_COURTIER_4 = "iport4" ;

	// Composants instancie
	protected Courtier uriCourtier_1;
	protected Courtier uriCourtier_2;
	protected Courtier uriCourtier_3;
	protected Courtier uriCourtier_4;
	protected Producteur uriProducteur_1 ;
	protected Producteur uriProducteur_2 ;
	protected Producteur uriProducteur_3 ;
	protected Producteur uriProducteur_4 ;
	protected Consommateur uriConsommateur_1 ;
	protected Consommateur uriConsommateur_2 ;
	protected Consommateur uriConsommateur_3 ;
	protected Consommateur uriConsommateur_4 ;

	public DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
	}
	
	@Override
	public boolean startStandardLifeCycle(long duration) {
		try {
			assert	duration	> 0 ;
			this.deploy() ;
			System.out.println("starting...") ;
			this.start() ;
			
			// on synchronise toute les jvm afin que
			// toutes methodes start de chaque jvm soit termine avant
			// les methodes execute
			this.waitOnCyclicBarrier();
			
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
	
	@Override
	public void	initialise() throws Exception {
		super.initialise() ;
	}

	@Override
	public void	instantiateAndPublish() throws Exception {
		
		if (thisJVMURI.equals(COURTIER_1_JVM_URI)) {

			this.uriCourtier_1 = new Courtier(URI_OPORT_COURTIER_1, URI_IPORT_COURTIER_1,1);
			this.uriProducteur_1 = new Producteur() ;
			this.uriConsommateur_1 = new Consommateur();
			
			this.addDeployedComponent(uriCourtier_1) ;
			this.addDeployedComponent(uriProducteur_1) ;
			this.addDeployedComponent(uriConsommateur_1) ;

		} else if (thisJVMURI.equals(COURTIER_2_JVM_URI)) {

			this.uriCourtier_2 = new Courtier(URI_OPORT_COURTIER_2, URI_IPORT_COURTIER_2,1);
			this.uriProducteur_2 = new Producteur() ;
			this.uriConsommateur_2 = new Consommateur();
			
			this.addDeployedComponent(uriCourtier_2) ;
			this.addDeployedComponent(uriProducteur_2) ;
			this.addDeployedComponent(uriConsommateur_2) ;

		} else if (thisJVMURI.equals(COURTIER_3_JVM_URI)) {
			
			this.uriCourtier_3 = new Courtier(URI_OPORT_COURTIER_3, URI_IPORT_COURTIER_3,1);
			this.uriProducteur_3 = new Producteur() ;
			this.uriConsommateur_3 = new Consommateur();
			
			this.addDeployedComponent(uriCourtier_3) ;
			this.addDeployedComponent(uriProducteur_3) ;
			this.addDeployedComponent(uriConsommateur_3) ;
			
		} else if (thisJVMURI.equals(COURTIER_4_JVM_URI)) {
			
			this.uriCourtier_4 = new Courtier(URI_OPORT_COURTIER_4, URI_IPORT_COURTIER_4,1);
			this.uriProducteur_4 = new Producteur() ;
			this.uriConsommateur_4 = new Consommateur();
			
			this.addDeployedComponent(uriCourtier_4) ;
			this.addDeployedComponent(uriProducteur_4) ;
			this.addDeployedComponent(uriConsommateur_4) ;
		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.instantiateAndPublish();
	}

	
	@Override
	public void	interconnect() throws Exception {

		if (thisJVMURI.equals(COURTIER_1_JVM_URI)) {
			
			this.uriProducteur_1.doPortConnection(
					uriProducteur_1.findOutboundPortURIsFromInterface(PublicationServiceI.class)[0],
					uriCourtier_1.findInboundPortURIsFromInterface(PublicationServiceI.class)[0],
					PublicationServiceConnector.class.getCanonicalName());
			
			this.uriCourtier_1.doPortConnection(
					URI_OPORT_COURTIER_1,
					URI_IPORT_COURTIER_2,
					PublicationServiceConnector.class.getCanonicalName());
			
			this.uriCourtier_1.doPortConnection(
					uriCourtier_1.findOutboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
					uriConsommateur_1.findInboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
					ReceptionServiceConnector.class.getCanonicalName());
			
			this.uriConsommateur_1.doPortConnection(
					uriConsommateur_1.findOutboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					uriCourtier_1.findInboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					SouscriptionServiceConnector.class.getCanonicalName());
			
		} else if (thisJVMURI.equals(COURTIER_2_JVM_URI)) {
			
			this.uriProducteur_2.doPortConnection(
					uriProducteur_2.findOutboundPortURIsFromInterface(PublicationServiceI.class)[0],
					uriCourtier_2.findInboundPortURIsFromInterface(PublicationServiceI.class)[0],
					PublicationServiceConnector.class.getCanonicalName());

			this.uriCourtier_2.doPortConnection(
					URI_OPORT_COURTIER_2,
					URI_IPORT_COURTIER_3,
					PublicationServiceConnector.class.getCanonicalName());
			
			this.uriCourtier_2.doPortConnection(
					uriCourtier_2.findOutboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
					uriConsommateur_2.findInboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
					ReceptionServiceConnector.class.getCanonicalName());
			
			this.uriConsommateur_2.doPortConnection(
					uriConsommateur_2.findOutboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					uriCourtier_2.findInboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					SouscriptionServiceConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(COURTIER_3_JVM_URI)) {
			
			this.uriProducteur_3.doPortConnection(
					uriProducteur_3.findOutboundPortURIsFromInterface(PublicationServiceI.class)[0],
					uriCourtier_3.findInboundPortURIsFromInterface(PublicationServiceI.class)[0],
					PublicationServiceConnector.class.getCanonicalName());

			this.uriCourtier_3.doPortConnection(
					URI_OPORT_COURTIER_3,
					URI_IPORT_COURTIER_4,
					PublicationServiceConnector.class.getCanonicalName());
			
			this.uriCourtier_3.doPortConnection(
				uriCourtier_3.findOutboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
				uriConsommateur_3.findInboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
				ReceptionServiceConnector.class.getCanonicalName());
			
			this.uriConsommateur_3.doPortConnection(
					uriConsommateur_3.findOutboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					uriCourtier_3.findInboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					SouscriptionServiceConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(COURTIER_4_JVM_URI)) {
			
			this.uriProducteur_4.doPortConnection(
					uriProducteur_4.findOutboundPortURIsFromInterface(PublicationServiceI.class)[0],
					uriCourtier_4.findInboundPortURIsFromInterface(PublicationServiceI.class)[0],
					PublicationServiceConnector.class.getCanonicalName());

			this.uriCourtier_4.doPortConnection(
					URI_OPORT_COURTIER_4,
					URI_IPORT_COURTIER_1,
					PublicationServiceConnector.class.getCanonicalName());
			
			this.uriCourtier_4.doPortConnection(
				uriCourtier_4.findOutboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
				uriConsommateur_4.findInboundPortURIsFromInterface(ReceptionServiceI.class)[0], 
				ReceptionServiceConnector.class.getCanonicalName());
			
			this.uriConsommateur_4.doPortConnection(
					uriConsommateur_4.findOutboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					uriCourtier_4.findInboundPortURIsFromInterface(SouscriptionServiceI.class)[0],
					SouscriptionServiceConnector.class.getCanonicalName());

		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.interconnect();
	}


	@Override
	public void	shutdown() throws Exception {
		super.shutdown();
	}

	public static void main(String[] args) {
		try {
			DistributedCVM da  = new DistributedCVM(args, 2, 5) ;
			da.startStandardLifeCycle(150000L) ;
			Thread.sleep(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
//-----------------------------------------------------------------------------
