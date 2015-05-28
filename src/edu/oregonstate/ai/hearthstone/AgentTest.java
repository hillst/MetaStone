package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.mcts.MonteCarloTreeSearch;
import net.demilich.metastone.game.behaviour.value.ActionValueBehaviour;
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
        Deck rogue = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/307-gang-up-miracle-rogue");
        Deck shaman = new HearthPwnImporter().importFrom("http://www.hearthpwn.com/decks/57818-tsafys-top-100-legend-shammy");
        System.out.println("Zoo:");
        random_random(zoo);
        random_heuristic(zoo);
        random_uct(zoo);
        System.out.println("Rogue:");
        random_random(rogue);
        random_heuristic(rogue);
        random_uct(rogue);
        System.out.println("Shaman:");
        random_random(shaman);
        random_heuristic(shaman);
        random_uct(shaman);
    }

    public static void random_random(Deck deck) {
        PlayerConfig randomPlayer = new PlayerConfig(deck, new PlayRandomBehaviour());
        randomPlayer.setName("Random");
        AgentTest randomTest = new AgentTest(randomPlayer, randomPlayer, 1000);
        randomTest.getResult().printResult();
    }

    public static void random_heuristic(Deck deck) {
        PlayerConfig randomPlayer = new PlayerConfig(deck, new PlayRandomBehaviour());
        PlayerConfig greedyPlayer = new PlayerConfig(deck, new ActionValueBehaviour());
        randomPlayer.setName("Random");
        greedyPlayer.setName("Greedy");
        AgentTest randomTest = new AgentTest(randomPlayer, greedyPlayer, 1000);
        randomTest.getResult().printResult();
    }

    public static void random_uct(Deck deck) {
        PlayerConfig randomPlayer = new PlayerConfig(deck, new PlayRandomBehaviour());
        PlayerConfig uctPlayer = new PlayerConfig(deck, new MCTSAgent(5, 1));
        randomPlayer.setName("Random");
        uctPlayer.setName("UCT");
        AgentTest randomTest = new AgentTest(randomPlayer, uctPlayer, 50);
        randomTest.getResult().printResult();
    }

    private Result result;
    private PlayerConfig[] players = new PlayerConfig[2];
    public AgentTest(PlayerConfig p1, PlayerConfig p2, int totalGames) {
        reset(p1, p2, totalGames);
    }

    public void reset(PlayerConfig p1, PlayerConfig p2, int totalGames) {
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
        private int totalGames;

        public Result(PlayerConfig[] players) {
            wins[0] = wins[1] = 0;
            totalGames = 0;
            names[0] = players[0].getName();
            names[1] = players[1].getName();
        }

        public void addWin(int playerNum) {
            totalGames++;
            if (playerNum != -1) {
                wins[playerNum]++;
            }
        }

        public void printResult() {
            for (int i = 0; i < 2; i++) {
                System.out.print(names[i]);
                System.out.print(": ");
                System.out.println(winRate(i));
            }
        }

        public int[] getWins() { return wins; }
        public float winRate(int player) { return (float)getWins()[player]/totalGames(); }
        public int totalGames() { return totalGames; }
    }
}
