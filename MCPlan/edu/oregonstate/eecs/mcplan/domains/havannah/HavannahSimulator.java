package edu.oregonstate.eecs.mcplan.domains.havannah;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Simulator;

public class HavannahSimulator extends Simulator<HavannahState, HavannahAction> {
    private static final int NUMBER_OF_AGENTS = 2;

    private static final int[][] CORNERS = new int[][] { { 0, 0 },
            { 0, HavannahState.getBase() - 1 },
            { HavannahState.getBase() - 1, 0 },
            { HavannahState.getBase() - 1, HavannahState.getSize() - 1 },
            { HavannahState.getSize() - 1, HavannahState.getBase() - 1 },
            { HavannahState.getSize() - 1, HavannahState.getSize() - 1 } };

    private static final int[][][] SIDES = getSides();

    private HavannahAction prevAction_ = null;

    public HavannahSimulator() {
        setInitialState();
    }

    private HavannahSimulator(HavannahState state,
            List<HavannahAction> legalActions, int[] rewards,
            HavannahAction prevAction) {
        state_ = state;
        legalActions_ = new ArrayList<HavannahAction>();
        for (HavannahAction action : legalActions)
            legalActions_.add(action);
        rewards_ = new int[NUMBER_OF_AGENTS];
        for (int i = 0; i < NUMBER_OF_AGENTS; i++)
            rewards_[i] = rewards[i];
        prevAction_ = prevAction;
    }

    @Override
    public Simulator<HavannahState, HavannahAction> copy() {
        return new HavannahSimulator(state_, legalActions_, rewards_,
                prevAction_);
    }

    public void setInitialState() {
        state_ = new HavannahState(
                new byte[HavannahState.getSize()][HavannahState.getSize()], 0);
        prevAction_ = null;
        rewards_ = new int[NUMBER_OF_AGENTS];
        computeLegalActions();
    }

    @Override
    public void setState(HavannahState state) {
        state_ = state;
        prevAction_ = null;
        computeRewards();
        computeLegalActions();
    }

    @Override
    public void setState(HavannahState state, List<HavannahAction> legalActions) {
        state_ = state;
        prevAction_ = null;
        legalActions_ = legalActions;
        if (legalActions_.size() == 0)
            computeRewards();
        else
            rewards_ = new int[] { 0, 0 };
    }

    private static int[][][] getSides() {
        int[][][] sides = new int[6][HavannahState.getBase() - 2][2];
        for (int i = 0; i < HavannahState.getBase() - 2; i++) {
            sides[0][i][0] = 0;
            sides[0][i][1] = i + 1;
            sides[1][i][0] = i + 1;
            sides[1][i][1] = 0;
            sides[2][i][0] = i + 1;
            sides[2][i][1] = HavannahState.getBase() + i;
            sides[3][i][0] = HavannahState.getBase() + i;
            sides[3][i][1] = HavannahState.getSize() - 1;
            sides[4][i][0] = HavannahState.getSize() - 1;
            sides[4][i][1] = HavannahState.getBase() + i;
            sides[5][i][0] = HavannahState.getBase() + i;
            sides[5][i][1] = i + 1;
        }
        return sides;
    }

    @Override
    public void takeAction(HavannahAction action) {
        if (!legalActions_.contains(action))
            throw new IllegalArgumentException("Action " + action
                    + " not possible from current state.");
        byte[][] locations = state_.getLocations();
        locations[action.getX()][action.getY()] = (byte) (state_.getAgentTurn() + 1);
        state_ = new HavannahState(locations, getNextAgentTurn(state_
                .getAgentTurn()));
        prevAction_ = action;
        computeRewards();
        computeLegalActions();
    }

    public void computeLegalActions() {
        legalActions_ = new ArrayList<HavannahAction>();
        if (rewards_[0] == 0) {
            for (int y = 0; y < HavannahState.getSize(); y++) {
                int xMin = 0;
                int xMax = HavannahState.getSize();
                if (y >= HavannahState.getBase())
                    xMin = y - HavannahState.getBase() + 1;
                else
                    xMax = HavannahState.getBase() + y;
                for (int x = xMin; x < xMax; x++)
                    if (state_.getLocation(x, y) == 0)
                        legalActions_.add(HavannahAction.valueOf(x, y));
            }
        }
    }

    public void computeRewards() {
        byte[][] locations = state_.getLocations();
        boolean[][] visited = new boolean[HavannahState.getSize()][HavannahState
                .getSize()];
        int yMin = 0;
        int xMin = 0;
        int yMax = HavannahState.getSize();
        int xMax = HavannahState.getSize();
        if (prevAction_ != null) {
            xMin = prevAction_.getX();
            yMin = prevAction_.getY();
            xMax = xMin + 1;
            yMax = yMin + 1;
        }
        for (int y = yMin; y < yMax; y++) {
            for (int x = xMin; x < xMax; x++) {
                // Checks: non empty location - hasn't been visited
                if (locations[x][y] != 0 && visited[x][y] == false) {
                    int result = dfsCornersSides(x, y, locations, visited);
                    // count corners
                    int corners = 0;
                    for (int k = 0; k < 6; k++) {
                        if (result % 2 == 1)
                            corners += 1;
                        result >>= 1;
                    }
                    // count sides
                    int sides = 0;
                    for (int k = 0; k < 6; k++) {
                        if (result % 2 == 1)
                            sides += 1;
                        result >>= 1;
                    }
                    if (corners >= 2 || sides >= 3) {
                        if (locations[x][y] == 1) {
                            rewards_ = new int[] { 1, -1 };
                            return;
                        } else {
                            rewards_ = new int[] { -1, 1 };
                            return;
                        }
                    }
                }
            }
        }

        locations = state_.getLocations();
        visited = new boolean[HavannahState.getSize()][HavannahState.getSize()];
        for (int y = 0; y < locations.length; y++) {
            xMin = 0;
            xMax = HavannahState.getSize();
            if (y >= HavannahState.getBase())
                xMin = y - HavannahState.getBase() + 1;
            else
                xMax = HavannahState.getBase() + y;
            for (int x = xMin; x < xMax; x++) {
                if (locations[x][y] == 0
                        || locations[x][y] == state_.getAgentTurn() + 1)
                    locations[x][y] = 1;
                else
                    locations[x][y] = 0;
            }
        }

        yMin = 0;
        xMin = 0;
        yMax = HavannahState.getSize();
        xMax = HavannahState.getSize();
        if (prevAction_ != null) {
            xMin = Math.max(prevAction_.getX() - 1, 0);
            yMin = Math.max(prevAction_.getY() - 1, 0);
            xMax = Math.min(prevAction_.getX() + 2, HavannahState.getSize());
            yMax = Math.min(prevAction_.getY() + 2, HavannahState.getSize());
        }

        for (int y = yMin; y < yMax; y++) {
            for (int x = xMin; x < xMax; x++) {
                if (locations[x][y] != 0 && visited[x][y] == false) {
                    if (dfsCornersSides(x, y, locations, visited) == 0) {
                        if (state_.getAgentTurn() == 0) {
                            rewards_ = new int[] { -1, 1 };
                            return;
                        } else {
                            rewards_ = new int[] { 1, -1 };
                            return;
                        }
                    }
                }
            }
        }
        rewards_ = new int[NUMBER_OF_AGENTS];
    }

    private int dfsCornersSides(int x, int y, byte[][] locations,
            boolean[][] visited) {
        int value = getCornerMask(x, y) | getSideMask(x, y);
        visited[x][y] = true;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i + j != 0
                        && x + i >= 0
                        && y + j >= 0
                        && x + i < HavannahState.getSize()
                        && y + j < HavannahState.getSize()
                        && (y + j < HavannahState.getBase()
                                && x + i < HavannahState.getBase() + y + j || y
                                + j >= HavannahState.getBase()
                                && x + i > y + j - HavannahState.getBase())) {
                    int nextX = x + i;
                    int nextY = y + j;
                    if (visited[nextX][nextY] == false
                            && locations[nextX][nextY] == locations[x][y])
                        value |= dfsCornersSides(nextX, nextY, locations,
                                visited);
                }
            }
        }
        return value;
    }

    private int getCornerMask(int x, int y) {
        for (int i = 0; i < CORNERS.length; i++)
            if (CORNERS[i][0] == x && CORNERS[i][1] == y)
                return 1 << i;
        return 0;
    }

    private int getSideMask(int x, int y) {
        for (int i = 0; i < SIDES.length; i++)
            for (int j = 0; j < SIDES[i].length; j++)
                if (SIDES[i][j][0] == x && SIDES[i][j][1] == y)
                    return 1 << (i + 6);
        return 0;
    }

    @Override
    public int getNumberOfAgents() {
        return NUMBER_OF_AGENTS;
    }

    @Override
    public double[] getFeatureVector(HavannahAction action) {
        // TODO Auto-generated method stub
        return null;
    }
}
