package edu.oregonstate.eecs.mcplan.domains.backgammon;

import edu.oregonstate.eecs.mcplan.State;

/**
 * Represents a backgammon state as an array of byte locations. Each location is
 * 0 if no pieces are at that location and positive if player 1 has pieces there
 * and negative for the number of pieces player 2 has there.
 */
public class BackgammonState implements State {
    private static final int NUMBER_OF_DICE = 2;

    private static final int NUMBER_OF_DIE_FACES = 6;

    private static final int NUMBER_OF_LOCATIONS = 26;

    private byte[] locations_;

    private byte[] dice_;

    private int agentTurn_;

    public BackgammonState(byte[] locations, byte[] dice, int agentTurn) {
        locations_ = locations;
        dice_ = dice;
        agentTurn_ = agentTurn;
    }

    public byte[] getLocations() {
        byte[] locations = new byte[NUMBER_OF_LOCATIONS];
        for (int i = 0; i < NUMBER_OF_LOCATIONS; i++)
            locations[i] = locations_[i];
        return locations;
    }

    public byte getLocation(int index) {
        return locations_[index];
    }

    public byte[] getDice() {
        byte[] dice = new byte[NUMBER_OF_DICE];
        for (int i = 0; i < NUMBER_OF_DICE; i++)
            dice[i] = dice_[i];
        return dice;
    }

    public byte getDie(int number) {
        return dice_[number];
    }

    public static int getNumberOfDice() {
        return NUMBER_OF_DICE;
    }

    public static int getNumberOfDieFaces() {
        return NUMBER_OF_DIE_FACES;
    }

    public static int getNumberOfLocations() {
        return NUMBER_OF_LOCATIONS;
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    @Override
    public int hashCode() {
        int code = 11 * (7 + dice_[0]) + dice_[1];
        for (int i = 0; i < NUMBER_OF_LOCATIONS; i++)
            code = 11 * code + locations_[i];
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BackgammonState))
            return false;
        BackgammonState state = (BackgammonState) object;
        for (int i = 0; i < NUMBER_OF_LOCATIONS; i++)
            if (locations_[i] != state.getLocation(i))
                return false;
        return dice_[0] == state.getDie(0) && dice_[1] == state.getDie(1);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[" + dice_[0] + "][" + dice_[1] + "]\n");
        for (int i = 12; i > 6; i--) {
            if (locations_[i] >= 0)
                output.append(" ");
            output.append(locations_[i]);
        }
        output.append("|");
        for (int i = 6; i > 0; i--) {
            if (locations_[i] >= 0)
                output.append(" ");
            output.append(locations_[i]);
        }
        output.append(" [" + locations_[0] + "]\n");
        output.append("------------|------------\n");
        for (int i = 13; i < 19; i++) {
            if (locations_[i] >= 0)
                output.append(" ");
            output.append(locations_[i]);
        }
        output.append("|");
        for (int i = 19; i < 25; i++) {
            if (locations_[i] >= 0)
                output.append(" ");
            output.append(locations_[i]);
        }
        output.append(" [" + locations_[25] + "]");
        return output.toString();
    }
}
