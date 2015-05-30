package edu.oregonstate.eecs.mcplan.domains.havannah;

import edu.oregonstate.eecs.mcplan.State;

/**
 * 
 */
public final class HavannahState implements State {
    /** Length of one side of the six sided board. */
    private static final int BASE = 5;

    private static final int SIZE = 2 * BASE - 1;

    /**
     * 0 is empty or not playable 1 is player 1 2 is player 2
     */
    private byte[][] locations_;

    private int agentTurn_;

    public HavannahState(byte[][] locations, int agentTurn) {
        locations_ = locations;
        agentTurn_ = agentTurn;
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                locations[i][j] = locations_[i][j];
        return locations;
    }

    public byte getLocation(int x, int y) {
        return locations_[x][y];
    }

    public static int getBase() {
        return BASE;
    }

    public static int getSize() {
        return SIZE;
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (byte[] row : locations_)
            for (byte location : row)
                code = 11 * code + location;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HavannahState))
            return false;
        HavannahState state = (HavannahState) object;
        byte[][] locations = state.getLocations();
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (locations[i][j] != locations_[i][j])
                    return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = SIZE - 1; i >= 0; i--) {
            for (int j = 0; j < BASE - i - 1 || j < i - BASE + 1; j++)
                output.append(" ");
            int xMin = 0;
            int xMax = HavannahState.getSize();
            if (i >= HavannahState.getBase())
                xMin = i - HavannahState.getBase() + 1;
            else
                xMax = HavannahState.getBase() + i;
            for (int j = xMin; j < xMax; j++) {
                if (locations_[j][i] == 1)
                    output.append("X ");
                else if (locations_[j][i] == 2)
                    output.append("O ");
                else
                    output.append("- ");
            }
            output.append("\n");
        }
        return output.toString();
    }
}
