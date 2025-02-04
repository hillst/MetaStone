package net.demilich.metastone.gui.playmode;

import net.demilich.metastone.ApplicationFacade;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.gameconfig.GameConfig;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;
import de.pferdimanzug.nittygrittymvc.SimpleCommand;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class StartGameCommand extends SimpleCommand<GameNotification> {

	@Override
	public void execute(INotification<GameNotification> notification) {
		GameConfig gameConfig = (GameConfig) notification.getBody();

		PlayerConfig playerConfig1 = gameConfig.getPlayerConfig1();
		PlayerConfig playerConfig2 = gameConfig.getPlayerConfig2();

		Player player1 = new Player(playerConfig1);
		Player player2 = new Player(playerConfig2);

		GameContext newGame = new GameContextVisualizable(player1, player2, new GameLogic());
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ApplicationFacade.getInstance().sendNotification(GameNotification.PLAY_GAME, newGame);
			}
		});
		t.setDaemon(true);
		t.start();
	}

}
