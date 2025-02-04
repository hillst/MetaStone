package net.demilich.metastone.gui.deckbuilder;

import net.demilich.metastone.GameNotification;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class ChangeDeckNameCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);

		String newDeckName = (String) notification.getBody();
		deckProxy.getActiveDeck().setName(newDeckName);
	}

}
