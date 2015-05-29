package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.agents.RandomAgent;
import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.value.ActionValueBehaviour;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.deckbuilder.importer.HearthPwnImporter;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;

/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneArbiter {

    public static void main(String dicks[]){
        //simsPSecond();
        uctVRandom();

    }
    public static void simsPSecond(){
        Deck zoo = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/129065-spark-demonic-zoo-s9-brm-update");

        PlayerConfig pc = new PlayerConfig(zoo, new PlayRandomBehaviour());
        pc.setName("Player 1");

        PlayerConfig pc2 = new PlayerConfig(zoo, new PlayRandomBehaviour());
        pc2.setName("Player 2");
        // UctAgent agent = new UctAgent(nSims, uctConstant);
        int numEvals = 10000;
        double start = System.currentTimeMillis();
        for (int i = 0; i < numEvals; i++) {
            System.out.println("Run i: " + i);
            Player p1 = new Player(pc);
            Player p2 = new Player(pc2);
            GameLogic logic = new GameLogic();
            GameContext context = new GameContext(p1, p2, logic);
            context.play();
        }
        double runtime = System.currentTimeMillis() - start;

        System.out.println("Total runtime (ms): " + runtime );
        System.out.println("Total runtime/simulation (ms): " + runtime/numEvals );
        System.out.println("Total runtime/simulation (s): " + runtime/numEvals/1000);
        System.out.println("Simulations: " + numEvals);

    }

    public static void uctVRandom(){
        //static deck makes debugging easier
        Deck zoo = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/129065-spark-demonic-zoo-s9-brm-update");

        int nMonkies = 10;
        int uctConstant = 1;
        UctAgent agent = new UctAgent(nMonkies, uctConstant);
        PlayerConfig pc = new PlayerConfig(zoo, new MCTSAgent(agent, new RandomAgent()));
        //PlayerConfig pc = new PlayerConfig(new RandomDeck(HeroClass.HUNTER), new PlayRandomBehaviour());

        pc.setName("Player 1");
        PlayerConfig pc2 = new PlayerConfig(zoo, new ActionValueBehaviour());
        pc2.setName("Player 2");
        Player p1 = new Player(pc);
        Player p2 = new Player(pc2);

        // UctAgent agent = new UctAgent(nSims, uctConstant);
        GameLogic logic = new GameLogic();
        GameContext context = new GameContext(p1, p2, logic);

        context.play();
        System.out.println("Winner");
        System.out.println(context.getWinningPlayerId());

    }

}
