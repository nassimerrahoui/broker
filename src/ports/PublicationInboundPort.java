package ports;

import basics.Message;
import components.Courtier;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.PublicationServiceI;

public class PublicationInboundPort extends AbstractInboundPort implements PublicationServiceI {

	private static final long serialVersionUID = 1L;

	public PublicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, PublicationServiceI.class, owner);
		assert uri != null;
	}

	public void createTopic(final String uri) throws Exception {

		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Courtier) this.owner).createTopic(uri);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}

	public void publierMessage(final Message msg) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Courtier) this.owner).firstTransmission(msg);
				return null;
			}
		};
		
		this.owner.handleRequestAsync(0, task);	
	}

	@Override
	public void transfererMessage(Message msg) throws Exception {
		AbstractComponent.AbstractService<Void> task = new AbstractComponent.AbstractService<Void>() {

			public Void call() throws Exception {
				((Courtier) this.owner).transfererMessage(msg);
				return null;
			}
		};

		this.owner.handleRequestAsync(0, task);
	}
}