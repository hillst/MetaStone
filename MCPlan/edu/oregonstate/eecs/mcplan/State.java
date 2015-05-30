package edu.oregonstate.eecs.mcplan;
import java.io.Serializable;

/**
 * A state represents a state of some domain. A state should override
 * toString(), equals() and hashCode().
 */
public interface State extends Serializable {
    /**
     * @return id of the agent that is next to take an action.
     */
    public int getAgentTurn();
}