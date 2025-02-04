package net.demilich.metastone.gui.deckbuilder;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.cards.Card;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class AddCardToDeckCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);
		
		Card card = (Card) notification.getBody();
		if (deckProxy.addCardToDeck(card)) {
			getFacade().sendNotification(GameNotification.ACTIVE_DECK_CHANGED, deckProxy.getActiveDeck());
		} 
	}

}
