package edu.oregonstate.eecs.mcplan.agents;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Simulator;
import edu.oregonstate.eecs.mcplan.State;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconLaunchAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconNothingAction;
import edu.oregonstate.eecs.mcplan.domains.galcon.GalconState;

public class GalconFilterAgent extends Agent {
	private PolicyRollout pr;
	private UctAgent uct;
	
	private class Filter implements ActionFilter {
		public <A> List<A> filter(List<A> actionList_, State state_) {
			GalconState state = (GalconState)state_;
			List<GalconAction> actionList = (List<GalconAction>)actionList_;
			List<GalconAction> result = new ArrayList<GalconAction>();
			
			for (GalconAction a : actionList) {
				if (a instanceof GalconNothingAction) {
					result.add(a);
				} else if (a instanceof GalconLaunchAction) {
					GalconLaunchAction la = (GalconLaunchAction)a;
					if (la.getDestID() % 2 == 0) {
						result.add(a);
					}
				}
			}
			
			return (List<A>)result;
		}
	}
	
    public GalconFilterAgent() {
        name_ = "Nothing Galcon Agent";
        pr = new PolicyRollout(new RandomAgent(), 1, -1, new Filter(), null);
        uct = new UctAgent(500, 1, 10, 1, "ROOT_PARALLELIZATION", new RandomAgent(), new Filter());
    }

    @Override
    public <S extends State, A> A selectAction(S state_, Simulator<S, A> iSimulator) {
    	return uct.selectAction(state_, iSimulator);
    }
}
