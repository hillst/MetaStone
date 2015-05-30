package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.Serializable;

public abstract class GalconAction implements Serializable {

	public enum ActionType {
		Launch, Hold
	}

	protected ActionType actionType = null;

	protected int playerID = -1;

	public GalconAction(ActionType actionType, int playerID) {
		this.actionType = actionType;
		this.playerID = playerID;
	}

	public int getPlayerID() {
		return playerID;
	}
	
	public abstract boolean equals(Object o);
}
