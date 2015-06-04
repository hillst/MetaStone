package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import edu.oregonstate.eecs.mcplan.agents.RandomAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.value.ActionValueBehaviour;
import net.demilich.metastone.game.decks.Deck;
import net.demilich.metastone.game.logic.GameLogic;
import net.demilich.metastone.gui.deckbuilder.importer.HearthPwnImporter;
import net.demilich.metastone.gui.gameconfig.PlayerConfig;

import java.io.*;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by eiii on 5/28/15.
 */
public class AgentTest {

    public static void main(String args[]) {
        int field = 0;
        int id = Integer.parseInt(args[field++]);
        String p1Deck = args[field++];
        String p1Method = args[field++];
        String p2Deck = args[field++];
        String p2Method = args[field++];
        int count = Integer.parseInt(args[field++]);
        System.out.println(String.format("{%d} Starting %s (%s) vs %s (%s), %d trials.", id, p1Deck, p1Method, p2Deck, p2Method, count));

        PlayerConfig p1 = makePlayer(p1Method, p1Deck);
        p1.setName(p1Deck + ", " + p1Method);
        PlayerConfig p2 = makePlayer(p2Method, p2Deck);
        p2.setName(p2Deck + ", " + p2Method);
        AgentTest test = new AgentTest(p1, p2, count);
        Result result = test.getResult();
        result.printResult();
        try {
            String fname = ClusterManager.makeFilename(id);
            FileOutputStream fileStream = new FileOutputStream(fname);
            ObjectOutputStream objOut = new ObjectOutputStream(fileStream);
            objOut.writeObject(result);
            objOut.close();
            fileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PlayerConfig makePlayer(String methodName, String deckName) {
        Deck deck = loadDeck(deckName);
        Behaviour behaviour = loadBehaviour(methodName);
        return new PlayerConfig(deck, behaviour);
    }

    public static Behaviour loadBehaviour(String name) {
        String[] nameArray = name.split("\\.");
        if (nameArray[0].equals("UCT")) {
            int num = Integer.parseInt(nameArray[1]);
            double c = Double.parseDouble(nameArray[2]);
            return new MCTSAgent(new UctAgent(num, c), new RandomAgent());
        } else if (nameArray[0].equals("RANDOM")) {
            return new PlayRandomBehaviour();
        } else if (nameArray[0].equals("FIXED")) {
            return new GreedyOptimizeMove(new WeightedHeuristic());
        }
        //TODO: Exception
        throw new RuntimeException("Couldn't find behaviour!");
    }

    public static Deck loadDeck(String name) {
        String url;
        if (name.equals("ZOO")) {
            url = "http://www.hearthpwn.com/decks/129065-spark-demonic-zoo-s9-brm-update";
        } else if (name.equals("WARRIOR")) {
            url = "http://www.hearthpwn.com/decks/81605-breebotjr-control-warrior";
        } else if (name.equals("HUNTER")) {
            url = "http://www.hearthpwn.com/decks/136213-gvg-face-hunter-season-9-legend-24-na";
        } else if (name.equals("PRIEST")) {
            url = "http://www.hearthpwn.com/decks/235995-rank-4-to-legend-wombo-combo-priest";
        } else if (name.equals("HANDLOCK")) {
            url = "http://www.hearthpwn.com/decks/101155-oblivion-handlock-brm-update";
        } else {
            throw new RuntimeException("Couldn't find deck!");
        }
        Deck deck = new HearthPwnImporter().importFrom(url);
        return deck;
    }

    public static void random_random(Deck deck) {
        PlayerConfig randomPlayer = new PlayerConfig(deck, new PlayRandomBehaviour());
        randomPlayer.setName("Random");
        AgentTest randomTest = new AgentTest(randomPlayer, randomPlayer, 1000);
        randomTest.getResult().printResult();
    }

    public static void random_heuristic(Deck deck) {
        PlayerConfig randomPlayer = new PlayerConfig(deck, new PlayRandomBehaviour());
        PlayerConfig greedyPlayer = new PlayerConfig(deck, new GreedyOptimizeMove(new WeightedHeuristic()));
        randomPlayer.setName("Random");
        greedyPlayer.setName("Greedy");
        AgentTest randomTest = new AgentTest(randomPlayer, greedyPlayer, 1000);
        randomTest.getResult().printResult();
    }

    public static void uct_random(Deck deck) {
        PlayerConfig randomPlayer = new PlayerConfig(deck, new PlayRandomBehaviour());
        PlayerConfig uctPlayer = new PlayerConfig(deck, new MCTSAgent(new UctAgent(100, 1.0), new RandomAgent()));
        randomPlayer.setName("Random");
        uctPlayer.setName("UCT");
        AgentTest randomTest = new AgentTest(uctPlayer, randomPlayer, 10);
        randomTest.getResult().printResult();
    }

    public static void uct_heuristic(Deck deck) {
        PlayerConfig uctPlayer = new PlayerConfig(deck, new MCTSAgent(new UctAgent(100, 1.0), new RandomAgent()));
        PlayerConfig greedyPlayer = new PlayerConfig(deck, new GreedyOptimizeMove(new WeightedHeuristic()));
        uctPlayer.setName("UCT");
        greedyPlayer.setName("Greedy");
        AgentTest randomTest = new AgentTest(uctPlayer, greedyPlayer, 10);
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
        double startTime = System.currentTimeMillis();
        for (int i = 0; i < totalGames; i++) {
            Player p1 = new Player(players[0]);
            Player p2 = new Player(players[1]);
            GameLogic logic = new GameLogic();
            GameContext context = new GameContext(p1, p2, logic);
            context.play();
            result.addWin(context.getWinningPlayerId());
        }
        double time = System.currentTimeMillis() - startTime;
        double timePerGame = time / totalGames;
        result.setTimePerGame(timePerGame);
    }

    public Result getResult() { return result; }
}

class Result implements Serializable {
    private int[] wins = new int[2];
    private String[] names = new String[2];
    private int totalGames;
    private double timePerGame;

    public Result(PlayerConfig[] players) {
        wins[0] = wins[1] = 0;
        totalGames = 0;
        names[0] = players[0].getName();
        names[1] = players[1].getName();
    }

    public void addResult(Result other) {
        if (names[0].equals(other.names[0]) && names[1].equals(other.names[1])) {
            //Update time calculation
            int newTotalGames = totalGames + other.totalGames;
            double myTotalTime = timePerGame * totalGames;
            double otherTotalTime = other.timePerGame * other.totalGames;
            double totalTime = myTotalTime + otherTotalTime;
            timePerGame = totalTime / (double)newTotalGames;
            totalGames = newTotalGames;

            //Update wins
            wins[0] += other.wins[0];
            wins[1] += other.wins[1];
        } else {
            throw new RuntimeException();
        }
    }

    public void addWin(int playerNum) {
        totalGames++;
        if (playerNum != -1) {
            wins[playerNum]++;
        }
    }

    public void printResult() {
        System.out.println("Result:");
        for (int i = 0; i < 2; i++) {
            System.out.println(String.format("%s: %d (%f)", names[i], wins[i], winRate(i)));
        }
        System.out.println(timePerGame + "ms per game");
        System.out.print("\n");
    }

    public int[] getWins() { return wins; }
    public String[] getNames() { return names; }
    public float winRate(int player) { return (float)getWins()[player]/totalGames(); }
    public int totalGames() { return totalGames; }
    public void setTimePerGame(double timePerGame) { this.timePerGame = timePerGame; }
}
