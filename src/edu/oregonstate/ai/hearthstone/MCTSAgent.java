package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.agents.UctAgent;
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
    private UctAgent agent;
    private int nSimulations;
    private double uctConstant;

    public MCTSAgent(int nSimulations ,double uctConstant){
        this.nSimulations = nSimulations;
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
}
