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
    private Agent policy;
    private Agent basePolicy;

    public MCTSAgent(Agent policy, Agent basePolicy){
        this.policy = policy;
        this.basePolicy = basePolicy;
    }

    @Override
    public MCTSAgent clone(){
        return new MCTSAgent(this.policy, this.basePolicy);
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
        return (GameAction) this.policy.selectAction(triggerState, new HearthstoneSimulator(context));
    }

    public Agent getPolicy(){
        return this.policy;
    }

    public void setPolicy(Agent policy){
        this.policy = policy;

    }

    /**
     * only allows for one level of agent layering but taht should be okay.
     */
    public void policySwap(){
        this.policy = this.basePolicy;
        this.basePolicy = new RandomAgent();
    }

    @Override
    public Agent getBasePolicy(){
        return this.basePolicy;
    }
}
