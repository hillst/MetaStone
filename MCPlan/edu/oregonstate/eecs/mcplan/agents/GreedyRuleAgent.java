package edu.oregonstate.eecs.mcplan.agents;

import edu.oregonstate.ai.hearthstone.HearthstoneSimulator;
import edu.oregonstate.ai.hearthstone.HearthstoneState;
import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.GreedyOptimizeMove;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;

import java.util.List;

/**
 * Created by Hill on 6/5/15.
 */
public class GreedyRuleAgent extends Agent {
    private Behaviour policy;
    private IGameStateHeuristic heurisic;
    public GreedyRuleAgent(IGameStateHeuristic h){
        this.policy  = new GreedyOptimizeMove(h);
    }

    @Override
    public <S extends State, A> A selectAction(S state, Simulator<S, A> iSimulator) {
        GameContext context = ((HearthstoneState) state).getContext();
        Player player =  ((HearthstoneState) state).getContext().getActivePlayer();
        List<GameAction> actions = ((HearthstoneState) state).getLegalActions();
        return (A) this.policy.requestAction(context,player, actions);
    }


}
