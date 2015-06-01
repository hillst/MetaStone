package edu.oregonstate.ai.hearthstone;

import edu.oregonstate.eecs.mcplan.State;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.actions.GameAction;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hill on 5/27/15.
 */
public class HearthstoneState implements State {
    private GameContext gameContext;
    private List<GameAction> legalActions;


    public HearthstoneState(GameContext context) {
        this.gameContext = context;
        //this.legalActions = context.getValidActions();
    }

    public HearthstoneState(GameContext context, List<GameAction> legalActions) {
        this.gameContext = context;
        this.legalActions = legalActions;
    }


    public HearthstoneState copy() {
        if (this.getLegalActions() != gameContext.getValidActions()){
            //System.out.println("");
            //System.out.println("What we think" + this.getLegalActions());
            //System.out.println("Before clone" + this.gameContext.getValidActions());
            //System.out.println("AFTER CLONE" + this.gameContext.clone().getValidActions());
            //return new HearthstoneState(this.gameContext.clone(), this.getLegalActions());
        } else{
            return new HearthstoneState(this.gameContext.clone());
        }
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

    public void setLegalActions(List<GameAction> legalActions){
        this.legalActions = legalActions;
    }

    public List<GameAction> getLegalActions(){
        if (this.legalActions != null){
            return this.legalActions;
        } else{
            return this.gameContext.getValidActions();
        }
    }

    /**
     * TODO figure out if this means no more available actions or if it means the game is over
     * @return
     */
    public boolean getTerminal() {
        return this.gameContext.gameDecided();
    }

    public GameContext getContext(){
        return this.gameContext;
    }
}


