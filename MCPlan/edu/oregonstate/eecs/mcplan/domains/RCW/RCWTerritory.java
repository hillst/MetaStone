package edu.oregonstate.eecs.mcplan.domains.RCW;

public class RCWTerritory {

	/**
	 * @param args
	 */
	private int terID_;
	private boolean reservation_;
	private boolean occupied_;
	private int treatNum_;
	private boolean predatorExist_;
	
	public RCWTerritory(int id, boolean reservation, boolean occ, int treatNum, boolean predator){
		terID_ = id;
		reservation_ = reservation;
		occupied_ = occ;
		treatNum_ = treatNum;
		predatorExist_ = predator;
	}
	public RCWTerritory copy(){
		return new RCWTerritory(terID_, reservation_, occupied_, treatNum_, predatorExist_);
	}
	
	public void setReservation(boolean revervation){
		reservation_ = revervation;
	}
	public void setOccupancy(boolean occ){
		occupied_ = occ;
	}
	public void beTreated(){
		treatNum_++;
	}
	public void killPredator(){
		predatorExist_ = false;
	}
	public void beInvaded(){
		predatorExist_ = true;
	}
	public int getID(){
		return terID_;
	}
	public boolean isReserved(){
		return reservation_;
	}
	public boolean isOccupied(){
		return occupied_;
	}
	public int getTreatNum(){
		return treatNum_;
	}
	public boolean isInvaded(){
		return predatorExist_;
	}
	
	@Override
	public boolean equals(Object object){
		if (!(object instanceof RCWTerritory))
            return false;
		RCWTerritory territory = (RCWTerritory) object;
		if (territory.getID() == this.terID_ && territory.isReserved() == this.reservation_
				&& territory.isOccupied() == this.occupied_ && territory.getTreatNum() == this.treatNum_
				&& territory.isInvaded() == this.predatorExist_)
			return true;
		return false;
	}
	@Override
	public String toString(){
		StringBuilder output = new StringBuilder();
		output.append(this.terID_ + "_");
		output.append(this.reservation_ + "_");
		output.append(this.occupied_ + "_");
		output.append(this.treatNum_ + "_" + this.isInvaded());
		return output.toString();
	}
}
