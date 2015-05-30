package edu.oregonstate.eecs.mcplan.domains.RCW;

/*	The spreading action for one year. */
public final class RCWnoopAction extends RCWaction{

	public RCWnoopAction(){
		
	}
	
	@Override
    public boolean equals(Object object) {
		if (!(object instanceof RCWnoopAction))
            return false;
        return true;
	}
	
	@Override
	public String toString(){
		return "NOOPAction";
	}

}
