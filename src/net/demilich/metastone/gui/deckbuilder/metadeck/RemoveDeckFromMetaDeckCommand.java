package net.demilich.metastone.gui.deckbuilder.metadeck;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.MetaDeck;
import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class RemoveDeckFromMetaDeckCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		DeckProxy deckProxy = (DeckProxy) getFacade().retrieveProxy(DeckProxy.NAME);
		MetaDeck metaDeck = (MetaDeck) deckProxy.getActiveDeck();

		Deck deck = (Deck) notification.getBody();
		if (!metaDeck.getDecks().contains(deck)) {
			return;
		}

		metaDeck.getDecks().remove(deck);
		getFacade().sendNotification(GameNotification.ACTIVE_DECK_CHANGED, deckProxy.getActiveDeck());

	}

}
