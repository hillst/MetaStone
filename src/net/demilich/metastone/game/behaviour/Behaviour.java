package net.demilich.metastone.game.behaviour;

import edu.oregonstate.eecs.mcplan.Agent;
import net.demilich.metastone.game.GameContext;

public abstract class Behaviour implements IBehaviour {

	public IBehaviour clone() {
		try {
			return (IBehaviour)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onGameOver(GameContext context, int playerId, int winningPlayerId) {
	}

	@Override
	public Agent getBasePolicy(){
		return null;
	}

}
