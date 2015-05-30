package edu.oregonstate.eecs.mcplan.agents;

import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.actions.EndTurnAction;

public class RandomAgent extends Agent {
    public RandomAgent() {
        name_ = "Random";
    }


    @Override
    public <S extends State, A> A selectAction(S state, Simulator<S, A> iSimulator) {
    	iSimulator.setState(state);

        //I dont think our code needs to set the state since the state and simulator are tightly coupled.
        List<A> actions = iSimulator.getLegalActions();

        if (actions.size() == 0){

            System.out.println(state);
            System.out.println(actions);
            System.out.println("there are no legal actions here and this is a hack. RandomAgent.java");
            //return (A) new EndTurnAction();
        }

        return actions.get((int) (Math.random() * actions.size()));
    }
}
