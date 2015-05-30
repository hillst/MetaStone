package edu.oregonstate.eecs.mcplan.agents;

import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconNothingAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconState;

public class NothingGalconAgent extends Agent {
    public NothingGalconAgent() {
        name_ = "Nothing Galcon Agent";
    }

    @Override
    public <S extends State, A> A selectAction(S state_, Simulator<S, A> iSimulator) {
    	GalconState state = (GalconState)state_;
    	GalconAction a = AgentHelper.getMatchingNothingAction(state);
    	return (A)a;
    }
}
