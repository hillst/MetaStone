package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.agents.PolicyRollout;
import edu.oregonstate.eecs.mcplan.agents.RandomAgent;
import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.value.ActionValueBehaviour;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.DeckFactory;
import net.demilich.metastone.game.decks.MetaDeck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.common.DeckStringConverter;
import net.demilich.metastone.gui.deckbuilder.DeckProxy;
import net.demilich.metastone.gui.deckbuilder.importer.HearthPwnImporter;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneArbiter {

    public static void main(String dicks[]){
        //simsPSecond();
        //int nMonkies = Integer.parseInt(dicks[0]);
        try {
            uctVRandom(10000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void simsPSecond(){
        Deck handlock = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/101155-oblivion-handlock-brm-update");
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

    public static void uctVRandom(int nMonkies) throws FileNotFoundException {
        //static deck makes debugging easier
        System.out.println("start loading");
        DeckProxy deckProxy = new DeckProxy();
        deckProxy.loadDecks();
        MetaDeck metaDeck = new MetaDeck(deckProxy.getDecks());
        // Obviously this is really rough and we should be *extremely* specific with the decks we have.
        Deck zoo = metaDeck.getDecks().get(3);
        Deck handlock = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/101155-oblivion-handlock-brm-update");
       // Deck zoo = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/129065-spark-demonic-zoo-s9-brm-update");
        System.out.println(zoo);

        double uctConstant = 1;
        //Agent base = new RandomAgent();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Agent base = new RandomAgent();
        Agent agent = new UctAgent(nMonkies, uctConstant);
        // This is just a useful agent, same as what the gui has.
        //new GreedyOptimizeMove(new WeightedHeuristic());


        //TODO keep working on this base rollout thing. It shouldn't be UTC all the way to the bottom. It should be UTC for one level then random.
        //Agent agent = new PolicyRollout(base, 1, -1);
        PlayerConfig pc = new PlayerConfig(zoo, new MCTSAgent(agent, base));

        pc.setName("Player 1");
        PlayerConfig pc2 = new PlayerConfig(zoo, new GreedyOptimizeMove(new WeightedHeuristic()));
        //PlayerConfig pc2 = new PlayerConfig(zoo, new ActionValueBehaviour());
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
