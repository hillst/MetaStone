package edu.oregonstate.eecs.mcplan.domains.galcon;

public class GalconNothingAction extends GalconAction {
	public GalconNothingAction(int playerID) {
		super(ActionType.Hold, playerID);
	}
	
	public String toString() {
		return "Nothing";
	}
	
	public boolean equals(Object o) {
		if (o instanceof GalconNothingAction) {
			GalconNothingAction a = (GalconNothingAction)o;
			return a.playerID == this.playerID;
		}
		return false;
	}
}
