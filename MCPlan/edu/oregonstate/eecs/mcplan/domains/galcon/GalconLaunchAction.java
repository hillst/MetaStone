package edu.oregonstate.eecs.mcplan.domains.galcon;

public class GalconLaunchAction extends GalconAction {

	private int launchSiteID = -1;
	private int destID = -1;
	private LaunchSize size;
	
	public enum LaunchSize {
		SMALL,
		HALF,
		LARGE
	}

	public GalconLaunchAction(int playerID, int launchSiteID, int destID,
			LaunchSize size) {
		super(ActionType.Launch, playerID);
		this.launchSiteID = launchSiteID;
		this.destID = destID;
		this.size = size;
	}

	public LaunchSize getSize() {
		return size;
	}

	public int getLaunchSiteID() {
		return launchSiteID;
	}

	public int getDestID() {
		return destID;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Launch " + size.toString() + " from " + launchSiteID + " to "
				+ destID);
		return buffer.toString();
	}
	
	public boolean equals(Object o) {
		if (o instanceof GalconLaunchAction) {
			GalconLaunchAction a = (GalconLaunchAction)o;
			return a.getLaunchSiteID() == launchSiteID &&
					a.getDestID() == destID &&
					a.getSize() == size &&
					a.getPlayerID() == playerID;
		}
		return false;
	}
}
