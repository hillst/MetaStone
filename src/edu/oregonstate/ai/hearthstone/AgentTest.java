package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.mcts.MonteCarloTreeSearch;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.decks.RandomDeck;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.deckbuilder.importer.HearthPwnImporter;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;

/**
 * Created by eiii on 5/28/15.
 */
public class AgentTest {

    public static void main(String args[]) {
        Deck zoo = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/129065-spark-demonic-zoo-s9-brm-update");
        PlayerConfig zooRandom = new PlayerConfig(zoo, new PlayRandomBehaviour());
        zooRandom.setName("Zoo Random");
        AgentTest test = new AgentTest(zooRandom, zooRandom, 100);
        test.getResult().printResult();

    }

    private Result result;
    private PlayerConfig[] players = new PlayerConfig[2];
    public AgentTest(PlayerConfig p1, PlayerConfig p2, int totalGames) {
        players[0] = p1;
        players[1] = p2;
        runTest(totalGames);
    }

    private void runTest(int totalGames) {
        result = new Result(players);
        for (int i = 0; i < totalGames; i++) {
            Player p1 = new Player(players[0]);
            Player p2 = new Player(players[1]);
            GameLogic logic = new GameLogic();
            GameContext context = new GameContext(p1, p2, logic);
            context.play();
            result.addWin(context.getWinningPlayerId());
        }
    }

    public Result getResult() { return result; }

    public class Result {
        private int[] wins = new int[2];
        private String[] names = new String[2];

        public Result(PlayerConfig[] players) {
            wins[0] = wins[1] = 0;
            names[0] = players[0].getName();
            names[1] = players[1].getName();
        }

        public void addWin(int playerNum) { wins[playerNum]++; }

        public void printResult() {
            for (int i = 0; i < 2; i++) {
                System.out.print(names[i]);
                System.out.print(": ");
                System.out.println(winRate(i));
            }
        }

        public int[] getWins() { return wins; }
        public float winRate(int player) { return (float)getWins()[player]/totalGames(); }
        public int totalGames() { return wins[0] + wins[1]; }
    }
}
