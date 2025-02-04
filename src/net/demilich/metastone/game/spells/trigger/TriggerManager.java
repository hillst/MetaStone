package net.demilich.metastone.game.spells.trigger;

import java.util.ArrayList;
import java.util.List;

import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.targeting.EntityReference;
import net.demilich.metastone.utils.IDisposable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerManager implements Cloneable, IDisposable {
	
	public static Logger logger = LoggerFactory.getLogger(TriggerManager.class);
	
	private final List<IGameEventListener> triggers = new ArrayList<IGameEventListener>();;

	public TriggerManager() {
	}

	private TriggerManager(TriggerManager otherTriggerManager) {
		for (IGameEventListener gameEventListener : otherTriggerManager.triggers) {
			triggers.add(gameEventListener.clone());
		}
	}

	public void addTrigger(IGameEventListener trigger) {
		triggers.add(trigger);
		if (triggers.size() > 100) {
			logger.warn("Warning, many triggers: " + triggers.size() + " adding one of type: " + trigger);
		}
	}
	
	@Override
	public TriggerManager clone() {
		return new TriggerManager(this);
	}

	@Override
	public void dispose() {
		triggers.clear();
	}

	public void fireGameEvent(GameEvent event) {
		for (IGameEventListener trigger : getListSnapshot(triggers)) {
			if (trigger.getLayer() != event.getTriggerLayer()) {
				continue;
			}

			if (!trigger.interestedIn(event.getEventType())) {
				continue;
			}
			// we need to double check here if the trigger still exists;
			// after all, a previous trigger may have removed it (i.e. double
			// corruption)
			if (triggers.contains(trigger)) {
				trigger.onGameEvent(event);
			}

			if (trigger.isExpired()) {
				triggers.remove(trigger);
			}
		}
	}

	private List<IGameEventListener> getListSnapshot(List<IGameEventListener> triggerList) {
		return new ArrayList<IGameEventListener>(triggerList);
	}

	public List<IGameEventListener> getTriggersAssociatedWith(EntityReference entityReference) {
		List<IGameEventListener> relevantTriggers = new ArrayList<>();
		for (IGameEventListener trigger : triggers) {
			if (trigger.getHostReference().equals(entityReference)) {
				relevantTriggers.add(trigger);
			}
		}
		return relevantTriggers;
	}

	public void printCurrentTriggers() {
		for (IGameEventListener trigger : triggers) {
			System.out.println();
			System.out.println(trigger.toString());
			System.out.println();
		}
	}

	public void removeTrigger(IGameEventListener trigger) {
		if (!triggers.remove(trigger)) {
			System.out.println("Failed to remove trigger " + trigger);
		}
	}
	
	public void removeTriggersAssociatedWith(EntityReference entityReference) {
		for (IGameEventListener trigger : getListSnapshot(triggers)) {
			if (trigger.getHostReference().equals(entityReference)) {
				triggers.remove(trigger);
			}
		}
	}

}
