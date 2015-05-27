package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.actions.GameAction;

import java.util.List;


/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneState implements State {
    private GameContext gameContext;


    public HearthstoneState(GameContext context) {
        this.gameContext = context;
    }

    public HearthstoneState copy() {
        return new HearthstoneState(this.gameContext.clone());
    }

    public int getWinningPlayerId(){
        return this.gameContext.getWinningPlayerId();
    }
    public void takeAction(GameAction a){
        this.gameContext.takeAction(a);
    }

    @Override
    public int getAgentTurn() {
        return this.gameContext.getActivePlayerId();
    }

    //nigga please
    @Override
    public boolean equals(Object object) {
        return false;
    }

    @Override
    public String toString() {
        return this.gameContext.toString();
    }

    public List<GameAction> getLegalActions(){
        return this.gameContext.getValidActions();
    }

    /**
     * TODO figure out if this means no more available actions or if it means the game is over
     * @return
     */
    public boolean getTerminal() {
        return this.gameContext.gameDecided();
    }
}


