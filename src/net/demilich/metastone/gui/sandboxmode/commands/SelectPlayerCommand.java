package net.demilich.metastone.gui.sandboxmode.commands;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.gui.sandboxmode.SandboxProxy;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class SelectPlayerCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		Player player = (Player) notification.getBody();
		SandboxProxy sandboxProxy = (SandboxProxy) getFacade().retrieveProxy(SandboxProxy.NAME);
		
		sandboxProxy.setSelectedPlayer(player);
	}

}
