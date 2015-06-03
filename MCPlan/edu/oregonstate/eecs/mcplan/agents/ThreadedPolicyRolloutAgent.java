package edu.oregonstate.eecs.mcplan.agents;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Hill on 3/27/15.
 */
public class ThreadedPolicyRolloutAgent extends Agent {

    /**
     * Use the base policy basePi_ when sampling,
     * the depth and height of which are controlled by parameters.
     **/
        /* The base policy. */
    private Agent basePi_;

    private int width_;

    /*if height is -1, simulate until the end of the game. */
    private int height_;
    private int threads_;

    private ActionFilter filter_ = null;
    private Heuristic heuristic_ = null;
    private ExecutorService executor;

    public ThreadedPolicyRolloutAgent(Agent basePolicy, int width, int height, ExecutorService executor){
        name_ = "PolicyRollout of " + basePolicy.getName();
        basePi_ = basePolicy;
        width_ = width;
        height_ = height;
        this.executor = executor;
    }

    public ThreadedPolicyRolloutAgent(Agent basePolicy, int width, int height, ExecutorService executor, ActionFilter filter, Heuristic heuristic) {
        this(basePolicy, width, height, executor);
        this.filter_ = filter;
        this.heuristic_ = heuristic;
        this.executor = executor;

    }

    @Override
    public <S extends State, A> A selectAction(S state,
                                               Simulator<S, A> iSimulator) {
        iSimulator.setState(state);
        List<A> actions = iSimulator.getLegalActions();
        if (filter_ != null) {
            actions = filter_.filter(actions, state);
        }

        if(actions.size() == 1)
            return actions.get(0);
        double[] qValues = this.qEstimates(state, iSimulator);
        int best = 0;
        for (int i = 1; i < qValues.length; i++){
            if(qValues[i] > qValues[best])
                best = i;
        }
        return actions.get(best);
    }

    /**
     * Compute the Q values of all legal actions.
     **/
    public <S extends State, A> double[] qEstimates(S state, Simulator<S, A> iSimulator) {
        iSimulator.setState(state);
        List<A> actions = iSimulator.getLegalActions();
        if (filter_ != null) {
            actions = filter_.filter(actions, state);
        }
        double[] qValues = new double[actions.size()];

        List<Callable<Object>> todo = new ArrayList<Callable<Object>>(actions.size());
        for(int i = 0; i < actions.size(); i++){
            qValues[i] = 0;

            for(int j = 0; j < width_; j++){
                todo.add(Executors.callable(new QEstimateWorker(i, height_, iSimulator.copy(), actions, qValues)));
            }
        }
        try {
            executor.invokeAll(todo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < actions.size(); i++){
            qValues[i] /= width_;
        }
        return qValues;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(super.toString());
        output.append("\n  Width:     " + width_);
        output.append("\n  Height:     " + height_);
        return output.toString();
    }

    private class QEstimateWorker<A> implements Runnable{
        private int actionId;
        private Simulator iSimulator;
        private int height;
        private List<A> availableActions;
        private double results[];

        public QEstimateWorker(int actionId, int height, Simulator simulator,
                               List<A> availableActions, double[] results){
            this.actionId = actionId;
            this.iSimulator = simulator;
            this.height = height;
            this.results = results;
            this.availableActions = simulator.getLegalActions();

        }

        @Override
        public void run() {
            double result = this.qEstimate(this.availableActions.get(actionId));
            this.results[actionId] += result; // we do this width times so just add it

        }

        public double qEstimate(A action_) {
            Simulator simulator = iSimulator;
            int policyID = simulator.getState().getAgentTurn();
            System.out.println("rolling out with action: " + action_);
            simulator.takeAction(action_);
            int reward = simulator.getReward(policyID);

            int k = 0;
            while (!simulator.isTerminalState()) {
                if (height_ == -1) // make sure that k < height_ if height == -1
                    k = -2;
                if (k >= height_)
                    break;
                // whoa this could be the problem
                Object action = basePi_.selectAction(simulator.getState(), simulator.copy());
                simulator.takeAction(action);
                reward += simulator.getReward(policyID);
                k++;
            }

            double h = 0;
            if (heuristic_ != null) {
                h = heuristic_.heuristic(simulator.getState());

            }
            return (reward + h);

        }
    }


}

