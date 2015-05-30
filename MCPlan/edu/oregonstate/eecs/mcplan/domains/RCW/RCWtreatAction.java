package edu.oregonstate.eecs.mcplan.domains.RCW;

public final class RCWtreatAction extends RCWaction {
	/* ID of the purchased parcel. */
	private int parcel_;
	
	public RCWtreatAction(int parcel){
		parcel_ = parcel;
	}	
	
	public int getParcel(){
		return parcel_;
	}
	
	@Override
    public boolean equals(Object object) {
		if (!(object instanceof RCWtreatAction))
            return false;
        RCWtreatAction action = (RCWtreatAction) object;
        if (action.parcel_ != this.parcel_)
        	return false;
        return true;
	}
	
	@Override
	public String toString(){
		return "TreatAction-" + Integer.toString(parcel_);
	}
}
