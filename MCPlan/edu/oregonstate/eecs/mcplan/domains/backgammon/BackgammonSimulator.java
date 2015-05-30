package edu.oregonstate.eecs.mcplan.domains.backgammon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Simulator;

/**
 * Backgammon has about 100 actions per game.
 * 
 */
public class BackgammonSimulator extends Simulator<BackgammonState, BackgammonAction> {
    private static final int NUMBER_OF_AGENTS = 2;

    public BackgammonSimulator() {
        setInitialState();
    }

    private BackgammonSimulator(BackgammonState state,
            List<BackgammonAction> legalActions, int[] rewards) {
        state_ = state;
        legalActions_ = new ArrayList<BackgammonAction>();
        for (BackgammonAction action : legalActions)
            legalActions_.add(action);
        rewards_ = new int[NUMBER_OF_AGENTS];
        for (int i = 0; i < NUMBER_OF_AGENTS; i++)
            rewards_[i] = rewards[i];
    }

    @Override
    public Simulator<BackgammonState, BackgammonAction> copy() {
        return new BackgammonSimulator(state_, legalActions_, rewards_);
    }

    /**
     * Set up initial board. Simulate each player rolling a single die until one
     * player has a larger value. That player goes first using the value of the
     * die they already rolled and rolling the other.
     */
    public void setInitialState() {
        byte[] locations = new byte[] { 0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0,
                5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2, 0 };
        byte[] dice = new byte[BackgammonState.getNumberOfDice()];
        int agentTurn;

        do {
            dice[0] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
            dice[1] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
        } while (dice[0] == dice[1]);

        if (dice[0] > dice[1]) {
            dice[1] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
            agentTurn = 0;
        } else {
            dice[0] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
            agentTurn = 1;
        }
        agentTurn = 0;
        dice = new byte[] { 1, 3 };
        state_ = new BackgammonState(locations, dice, agentTurn);
        computeRewards();
        computeLegalActions();
    }

    @Override
    public void setState(BackgammonState state) {
        state_ = state;
        computeRewards();
        computeLegalActions();
    }

    @Override
    public void setState(BackgammonState state,
            List<BackgammonAction> legalActions) {
        state_ = state;
        legalActions_ = legalActions;
        if (legalActions_.size() == 0)
            computeRewards();
        else
            rewards_ = new int[NUMBER_OF_AGENTS];
    }

    @Override
    public void takeAction(BackgammonAction action) {
        if (!legalActions_.contains(action))
            throw new IllegalArgumentException("Action " + action
                    + " not possible from current state.");

        byte[] locations = state_.getLocations();

        for (int i = 0; i < action.size(); i++) {
            int from = action.getMove(i).getFrom();
            int distance = action.getMove(i).getDistance();
            byte piece;

            if (locations[from] > 0)
                piece = 1;
            else
                piece = -1;
            int to = from + distance * piece;
            if (to > 0 && to < BackgammonState.getNumberOfLocations() - 1) {
                if (locations[to] * piece < 0) {
                    locations[to] = piece;
                    if (piece > 0)
                        locations[25] -= piece;
                    else
                        locations[0] -= piece;
                } else
                    locations[to] += piece;
            }
            locations[from] -= piece;
        }
        byte[] dice = new byte[] {
                (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1),
                (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1) };
        state_ = new BackgammonState(locations, dice, getNextAgentTurn(state_
                .getAgentTurn()));
        computeRewards();
        computeLegalActions();
    }

    private void computeLegalActions() {
        legalActions_ = new ArrayList<BackgammonAction>();
        byte[] locations = state_.getLocations();
        byte[] dice = state_.getDice();
        int piece;
        byte[] values;
        int depth;

        if (getRewards()[0] == 0) {
            if (state_.getAgentTurn() == 0)
                piece = 1;
            else
                piece = -1;
            if (dice[0] == dice[1])
                values = new byte[] { dice[0] };
            else
                values = dice;

            if (dice[0] == dice[1])
                depth = 4;
            else
                depth = 2;

            // Simplify the board
            for (int i = 0; i < BackgammonState.getNumberOfLocations(); i++)
                if (locations[i] * piece == -1)
                    locations[i] = 0;

            legalActions_ = dfs(locations, new LinkedList<BackgammonMove>(),
                    values, piece, depth);

            // Prune moves that are too small
            int max = 0;
            for (BackgammonAction legalAction : legalActions_)
                if (legalAction.size() > max)
                    max = legalAction.size();
            for (int i = 0; i < legalActions_.size(); i++)
                if (legalActions_.get(i).size() != max)
                    legalActions_.remove(i--);
        }
    }

    private List<BackgammonAction> dfs(byte[] locations,
            LinkedList<BackgammonMove> moves, byte[] values, int piece,
            int depth) {
        List<BackgammonAction> legalActions = new ArrayList<BackgammonAction>();
        int limit = BackgammonState.getNumberOfLocations();
        int start = 0;

        if (piece > 0 && locations[0] > 0)
            limit = 1;
        else if (piece < 0 && locations[25] < 0)
            start = 25;

        boolean moveOff = canMoveOff(locations, piece);
        for (int i = start; i < limit; i++) {
            if (locations[i] * piece >= 1) {
                for (int j = 0; j < values.length; j++) {
                    if (canMove(i, values[j], moveOff)) {
                        BackgammonMove move = BackgammonMove.valueOf(i,
                                values[j]);
                        if (moves.isEmpty()
                                || move.compareTo(moves.getLast()) * piece >= 0) {
                            moves.addLast(move);
                            if (depth > 1) {
                                locations[i] -= piece;
                                int next = i + values[j] * piece;
                                if (next > 0
                                        && next < BackgammonState
                                                .getNumberOfLocations() - 1)
                                    locations[next] += piece;
                                int k = 0;
                                if (values.length == 2) {
                                    if (j == 0)
                                        k = 1;
                                    else
                                        k = 0;
                                }
                                legalActions.addAll(dfs(locations, moves,
                                        new byte[] { values[k] }, piece,
                                        depth - 1));
                                if (next > 0
                                        && next < BackgammonState
                                                .getNumberOfLocations() - 1)
                                    locations[next] -= piece;
                                locations[i] += piece;
                            } else
                                legalActions.add(new BackgammonAction(moves));
                            moves.removeLast();
                        }
                    }
                }
            }
        }
        if (legalActions.size() == 0)
            legalActions.add(new BackgammonAction(moves));
        return legalActions;
    }

    private boolean canMove(int location, int distance, boolean moveOff) {
        if (state_.getAgentTurn() == 0) {
            int next = location + distance;
            return (next < BackgammonState.getNumberOfLocations() - 1 && state_
                    .getLocation(next) >= -1)
                    || (moveOff && next >= BackgammonState
                            .getNumberOfLocations() - 1);
        } else {
            int next = location - distance;
            return (next > 0 && state_.getLocation(next) <= 1)
                    || (moveOff && next <= 0);
        }
    }

    /**
     * Checks if a player can start moving pieces off of the board.
     * 
     * @param locations
     * @param piece
     * @return true if legal to move off board.
     */
    private boolean canMoveOff(byte[] locations, int piece) {
        if (piece > 0) {
            for (int i = 0; i < 19; i++)
                if (locations[i] > 0)
                    return false;
        } else {
            for (int i = 7; i < BackgammonState.getNumberOfLocations(); i++)
                if (locations[i] < 0)
                    return false;
        }
        return true;
    }

    // private int moveOffDistance(byte[] locations, int piece) {
    // int distance = 0;
    // if (piece > 0) {
    // for (int i = 0; i < 19; i++)
    // if (locations[i] > 0)
    // distance += 1;
    // } else {
    // for (int i = 7; i < BackgammonState.getNumberOfLocations(); i++)
    // if (locations[i] < 0)
    // distance += 1;
    // }
    // return distance;
    // }

    /**
     * @return {-1,1} for loss and {1,-1} for win at terminal state otherwise
     *         returns {0,0}
     */
    public void computeRewards() {
        boolean pos = false, neg = false;
        for (int i = 0; i < BackgammonState.getNumberOfLocations(); i++) {
            if (state_.getLocation(i) > 0)
                pos = true;
            else if (state_.getLocation(i) < 0)
                neg = true;
        }
        if (!pos)
            rewards_ = new int[] { 1, -1 };
        else if (!neg)
            rewards_ = new int[] { -1, 1 };
        else
            rewards_ = new int[] { 0, 0 };
    }

    @Override
    public int getNumberOfAgents() {
        return NUMBER_OF_AGENTS;
    }

    @Override
    public double[] getFeatureVector(BackgammonAction action) {
        throw new IllegalStateException("Unimplemented");
    }
}
