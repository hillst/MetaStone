package edu.oregonstate.eecs.mcplan.domains.RCW;

public final class RCWkillAction extends RCWaction{
	/* ID of the purchased parcel. */
	private int parcel_;
	
	public RCWkillAction(int parcel){
		parcel_ = parcel;
	}	
	
	public int getParcel(){
		return parcel_;
	}
	
	@Override
    public boolean equals(Object object) {
		if (!(object instanceof RCWkillAction))
            return false;
        RCWkillAction action = (RCWkillAction) object;
        if (action.parcel_ != this.parcel_)
        	return false;
        return true;
	}
	
	@Override
	public String toString(){
		return "killAction-" + Integer.toString(parcel_);
	}
}
