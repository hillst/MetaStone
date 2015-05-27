package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.mcts.MonteCarloTreeSearch;
import net.demilich.metastone.game.decks.RandomDeck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;

/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneArbiter {

    public static void main(String dicks[]){
        int nSims = 100;
        double uctConstant = 1;
        PlayerConfig pc = new PlayerConfig(new RandomDeck(HeroClass.HUNTER), new MCTSAgent(nSims, uctConstant));
        pc.setName("Player 1");
        PlayerConfig pc2 = new PlayerConfig(new RandomDeck(HeroClass.HUNTER), new PlayRandomBehaviour());
        pc2.setName("Player 2");
        Player p1 = new Player(pc);
        Player p2 = new Player(pc2);

        UctAgent agent = new UctAgent(nSims, uctConstant);
        GameLogic logic = new GameLogic();
        GameContext context = new GameContext(p1, p2, logic);

        context.play();
        System.out.println("Winner");
        System.out.println(context.getWinningPlayerId());
    }

}
