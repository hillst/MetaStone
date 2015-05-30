package edu.oregonstate.eecs.mcplan.domains.havannah;

import java.util.ArrayList;
import java.util.List;

public class HavannahAction {
    /** List of all possible Havannah actions. */
    private static List<List<HavannahAction>> havannahActions_ = generateActions();

    /** x coordinate. */
    private int x_;

    /** y coordinate. */
    private int y_;

    private HavannahAction(int x, int y) {
        x_ = x;
        y_ = y;
    }

    public static HavannahAction valueOf(int x, int y) {
        return havannahActions_.get(x).get(y);
    }

    /**
     * Contains all possible actions and a few impossible actions.
     * 
     * @return set of possible actions.
     */
    private static List<List<HavannahAction>> generateActions() {
        List<List<HavannahAction>> havannahActions = new ArrayList<List<HavannahAction>>();
        for (int i = 0; i < HavannahState.getSize(); i++) {
            havannahActions.add(new ArrayList<HavannahAction>());
            for (int j = 0; j < HavannahState.getSize(); j++)
                havannahActions.get(i).add(new HavannahAction(i, j));
        }
        return havannahActions;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    @Override
    public int hashCode() {
        return 11 * (7 + x_) + y_;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HavannahAction))
            return false;
        HavannahAction action = (HavannahAction) object;
        return x_ == action.getX() && y_ == action.getY();
    }

    @Override
    public String toString() {
        return "(" + x_ + "," + y_ + ")";
    }
}
