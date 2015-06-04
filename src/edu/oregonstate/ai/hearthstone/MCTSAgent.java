package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.agents.RandomAgent;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.cards.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hill on 5/27/15.
 */
public class MCTSAgent extends Behaviour {
    private Agent policy;
    private Agent basePolicy;
    private String name = "MCTSAgent";

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
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        //not smart enough to try mulligan
        return new ArrayList<Card>();
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        HearthstoneState triggerState = new HearthstoneState(context, validActions);

        return (GameAction) this.policy.selectAction(triggerState, new HearthstoneSimulator(context, triggerState));
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
