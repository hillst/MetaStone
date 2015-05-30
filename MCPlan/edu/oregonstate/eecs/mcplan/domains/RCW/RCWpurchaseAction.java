package edu.oregonstate.eecs.mcplan.domains.RCW;

/**
 * Represent a Cascades action.
 * We assume that only one parcel is purchased by an action. 
 */
public final class RCWpurchaseAction extends RCWaction{
	/* ID of the purchased parcel. */
	private int parcel_;
	
	public RCWpurchaseAction(int parcel){
		parcel_ = parcel;
	}
	
	public int getParcel(){
		return parcel_;
	}

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RCWpurchaseAction))
            return false;
        RCWpurchaseAction action = (RCWpurchaseAction) object;
        if (action.parcel_ != this.parcel_)
        	return false;
        return true;
    }

    @Override
    public String toString() {
        return "PurchaseAction-" + Integer.toString(parcel_);
    }
}
