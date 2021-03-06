package ports;

import basics.Souscription;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.SouscriptionServiceI;

public class SouscriptionOutboundPort extends AbstractOutboundPort implements SouscriptionServiceI {

	public SouscriptionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, SouscriptionServiceI.class, owner);
		assert uri != null && owner != null;
	}

	private static final long serialVersionUID = 1L;

	public void souscrire(Souscription s) throws Exception {
		((SouscriptionServiceI) this.connector).souscrire(s);
	}
}
