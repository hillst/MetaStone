package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;

import java.util.List;

/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneSimulator extends Simulator {
    private HearthstoneState state;

    /**
     * Ultimately we want the simulator to handle the work, default constructor will do that
     *
     */
    public HearthstoneSimulator(){
        this.setInitialState();
        this.rewards_ = new int[2];
    }

    /**
     * This is called by our copy function, since we are cloned do our policy swap
     *
     * Policy swap is such a hack.
     *
     *
     * @param state
     */
    public HearthstoneSimulator(HearthstoneState state, int[] rewards){
        GameContext context = state.getContext();
        setupRollout(context);
        this.state = state;
        this.rewards_ = new int[2];

        for (int i =0; i< rewards.length; i++)
            this.rewards_[i] = rewards[i];
    }
    /**
     * Operates with the assumption that player1 is the MCTS player and that the second player should be treated as
     * random for the rest of the simulation.
     *
     * Such a hack
     *
     * @param context
     */
    public HearthstoneSimulator(GameContext context){
        GameContext newContext = context.clone();
        setupRollout(newContext);
        this.state = new HearthstoneState(newContext);
        this.rewards_ = new int[2];
    }

    public HearthstoneSimulator(GameContext context, HearthstoneState state){
        GameContext newContext = context.clone();
        setupRollout(newContext);
        this.state = state;
        this.rewards_ = new int[2];
    }

    /**
     * We need to make sure the rollouts are happening correctly, since we are using the hearthstone simulator with
     * MCTS agent, we need to figure out which agent to swap and which agent to set to random (the opposition).
     *
     * -- What do we do if they are both UCT agents?
     * -- Try then test.
     * @param newContext
     */
    private void setupRollout(GameContext newContext){
        //MCTSAgent
        Player toChange;
        Player mcAgent;

        if (newContext.getPlayer1().getBehaviour().getName().equals("MCTSAgent") && newContext.getActivePlayerId() == 0){
            toChange = newContext.getPlayer2();
            mcAgent = newContext.getPlayer1();
        } else{
            toChange = newContext.getPlayer1();
            mcAgent = newContext.getPlayer2();
        }
        if (mcAgent.getBehaviour().getName() == "MCTSAgent"){
            ((MCTSAgent)mcAgent.getBehaviour()).policySwap();
        }

        if (!toChange.equals("Play Random")) {
            toChange.setBehaviour(new PlayRandomBehaviour());
        }
    }

    public HearthstoneSimulator(GameContext context, int[] rewards){
        this(context);
        this.rewards_ = new int[2];
        for (int i =0; i< rewards.length; i++)
            this.rewards_[i] = rewards[i];
    }

    /**
     * Probably should be in a different functino
     */
    private void computeRewards() {
        if (this.isTerminalState()){
            int winner = this.state.getWinningPlayerId();
            int loser;
            if (winner > -1) {
                if (winner == 0){
                    loser = 1;
                } else{
                    loser = 0;
                }
                rewards_[winner] = 1;
                rewards_[loser] = -1;
            }
        }
    }

    @Override
    public Simulator copy() {
        HearthstoneSimulator sim = new HearthstoneSimulator(state.copy(), this.rewards_);
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
        this.computeRewards();

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
    public void setState(State state, List legalActions) {
        this.state = (HearthstoneState) state;
        this.state.setLegalActions(legalActions);
        this.computeRewards();
    }

    @Override
    public void takeAction(Object o) {

        GameAction action = (GameAction) o;

        this.state.takeAction(action);
        this.state.setLegalActions(this.state.getContext().getValidActions());
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
