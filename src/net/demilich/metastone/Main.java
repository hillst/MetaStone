package net.demilich.metastone;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.mcts.MonteCarloTreeSearch;
import net.demilich.metastone.game.decks.RandomDeck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;

/**
 * Created by Hill on 5/26/15.
 */
public class Main {

    public static void main(String args[]){
        int NUM_SIMS = 10;
        int width = 1;
        PlayerConfig pc = new PlayerConfig(new RandomDeck(HeroClass.HUNTER), new MonteCarloTreeSearch(NUM_SIMS));
        pc.setName("Player 1");
        PlayerConfig pc2 = new PlayerConfig(new RandomDeck(HeroClass.HUNTER), new PlayRandomBehaviour());
        pc2.setName("Player 2");
        Player p1 = new Player(pc);
        Player p2 = new Player(pc2);

        int NUM_THREADS = 2;
        GameLogic logic = new GameLogic();
        GameContext context = new GameContext(p1, p2, logic);

        context.play();
        System.out.println("Width: " + width);
       // System.out.println("Num Sims: " + NUM_SIMS);
        System.out.println("Winner: " + context.getWinningPlayerId());
    }
}
