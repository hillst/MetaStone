package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.agents.PolicyRollout;
import edu.oregonstate.eecs.mcplan.agents.RandomAgent;
import edu.oregonstate.eecs.mcplan.agents.ThreadedPolicyRolloutAgent;
import edu.oregonstate.eecs.mcplan.agents.UctAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Hill on 5/27/15.
 */
public class MCTSAgent extends Behaviour {
    private Agent agent;
    private int nMonkies;
    private double uctConstant;
    private Behaviour baseBehaviour;




    public MCTSAgent(int nSimulations ,double uctConstant){
        this.nMonkies = nSimulations;
        this.uctConstant = uctConstant;
        UctAgent agent = new UctAgent(nSimulations, uctConstant);
        this.agent = agent;
    }

    @Override
    public String getName() {
        return "MCTSAgent ";
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        //not smart enough to try mulligan
        return new ArrayList<Card>();
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        HearthstoneState triggerState = new HearthstoneState(context);
        triggerState.setLegalActions(validActions);
        return (GameAction) this.agent.selectAction(triggerState, new HearthstoneSimulator(context));
    }

    public void setAgent(Agent agent){
        this.agent = agent;
    }

    public Agent getBasePolicy(){
        return this.agent;
    }
}
