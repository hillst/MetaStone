package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;

import java.util.List;

/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneSimulator extends Simulator {
    private HearthstoneState state;

    /**
     * Ultimately we want the simulator to handle the work, default constructor will do that
     */
    public HearthstoneSimulator(){
        this.setInitialState();
        this.rewards_ = new int[2];
    }
    /**
     * Given a state we already have setup the simulator
     * @param context
     */
    public HearthstoneSimulator(GameContext context){

        GameContext newContext = context.clone();
        if (!newContext.getPlayer2().getBehaviour().getName().equals("Play Random")){
            newContext.getPlayer2().setBehaviour(new PlayRandomBehaviour());
        }
        this.state = new HearthstoneState(newContext);
        this.state = new HearthstoneState(context.clone());
        this.rewards_ = new int[2];
    }

    public HearthstoneSimulator(GameContext context, int[] rewards){
        GameContext newContext = context.clone();
        if (!newContext.getPlayer2().getBehaviour().getName().equals("Play Random")){
            newContext.getPlayer2().setBehaviour(new PlayRandomBehaviour());
        }
        this.state = new HearthstoneState(newContext);

        for (int i =0; i< rewards.length; i++)
            this.rewards_[i] = rewards[i];
    }

    private void computeRewards() {
        if (this.isTerminalState()){
            int winner = this.state.getWinningPlayerId();
            if (winner > -1)
                rewards_[winner]++;
        }
    }

    @Override
    public Simulator copy() {
        HearthstoneSimulator sim = new HearthstoneSimulator();
        sim.setState(this.state.copy());
        return sim;
    }

    @Override
    public void setInitialState() {
        //this is weird because it doesn't ask for anything and our state depends on a lot
        //ultimately this should initialize our game though, act as sort of a base "setup" type thing.
        // we don't really need it though for now. We can do it manually and pass in a gamecontext.
    }

    @Override
    public void setState(State state) {
        this.state = (HearthstoneState) state;

    }
    @Override
    public State getState(){
        return this.state;
    }

    @Override
    public boolean isTerminalState(){
        return this.state.getTerminal();
    }

    @Override
    public List<GameAction> getLegalActions(){
        return this.state.getLegalActions();
    }

    @Override
    public void setState(State state, List list) {
        this.state = (HearthstoneState) state;
    }

    @Override
    public void takeAction(Object o) {
        GameAction action = (GameAction) o;
        this.state.takeAction(action);
        this.computeRewards();
    }

    @Override
    public int getNumberOfAgents() {
        return 2;
    }

    @Override
    public double[] getFeatureVector(Object o) {
        return new double[0];
    }
}
