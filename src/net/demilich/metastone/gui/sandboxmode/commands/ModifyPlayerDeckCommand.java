package net.demilich.metastone.gui.sandboxmode.commands;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.gui.sandboxmode.SandboxProxy;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class ModifyPlayerDeckCommand extends SimpleCommand<GameNotification>{

	@Override
	public void execute(INotification<GameNotification> notification) {
		SandboxProxy sandboxProxy = (SandboxProxy) getFacade().retrieveProxy(SandboxProxy.NAME);
		
		Player player = sandboxProxy.getSelectedPlayer();
		CardCollection modifiedDeck = (CardCollection) notification.getBody();
		
		player.getDeck().removeAll();
		player.getDeck().addAll(modifiedDeck);
		
		sendNotification(GameNotification.UPDATE_SANDBOX_STATE, sandboxProxy.getSandbox());
	}

}
