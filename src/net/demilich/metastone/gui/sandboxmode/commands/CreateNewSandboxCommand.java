package net.demilich.metastone.gui.sandboxmode.commands;

import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.DoNothingBehaviour;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.gameconfig.GameConfig;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;
import net.demilich.metastone.gui.playmode.GameContextVisualizable;
import net.demilich.metastone.gui.sandboxmode.SandboxProxy;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class CreateNewSandboxCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				GameConfig gameConfig = (GameConfig) notification.getBody();
				SandboxProxy sandboxProxy = (SandboxProxy) getFacade().retrieveProxy(SandboxProxy.NAME);

				PlayerConfig player1Config = gameConfig.getPlayerConfig1();
				player1Config.setName("Player 1");
				Player player1 = new Player(player1Config);
				player1.setBehaviour(new DoNothingBehaviour());

				PlayerConfig player2Config = gameConfig.getPlayerConfig2();
				player2Config.setName("Player 2");
				Player player2 = new Player(player2Config);
				player2.setBehaviour(new DoNothingBehaviour());

				GameContext sandbox = new GameContextVisualizable(player1, player2, new GameLogic());
				sandboxProxy.setSandbox(sandbox);
				sendNotification(GameNotification.UPDATE_SANDBOX_STATE, sandbox);

				player1.setBehaviour(player1Config.getBehaviour());
				player2.setBehaviour(player2Config.getBehaviour());
				sandbox.setIgnoreEvents(true);
				sandbox.play();

			}
		});
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler((t, exception) -> exception.printStackTrace());
		thread.start();
	}

}
