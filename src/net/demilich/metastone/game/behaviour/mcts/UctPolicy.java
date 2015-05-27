package net.demilich.metastone.game.behaviour.mcts;

import java.util.Random;

class UctPolicy implements ITreePolicy {

	private double epsilon = 1e-5;
	private static final Random random = new Random();
	
	private double uctConstant = 1 / Math.sqrt(2);
	/*
	public UctPolicy(){
		super();
	}

	//TODO implement a base policy function so we can have some more sophisticated searches
	public UctPolicy(double epsilon){
		super();
		this.epsilon = epsilon;
	}

	public UctPolicy(double epsilon, double uctConstant){
		super();
		this.epsilon = epsilon;
		this.uctConstant = uctConstant;
	}
	*/

	@Override
	public Node select(Node parent) {
		Node selected = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		for (Node child : parent.getChildren()) {
			double uctValue = child.getVisits() == 0 ? 1000000 : child.getScore() / (double)child.getVisits() + this.uctConstant
					* Math.sqrt(Math.log(parent.getVisits()) 
							/ child.getVisits()) + random.nextDouble() * this.epsilon;
			
			// small random number to break ties randomly in unexpanded nodes
			if (uctValue > bestValue) {
				selected = child;
				bestValue = uctValue;
			} 
		}

		return selected;
	}

}
