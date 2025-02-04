package net.demilich.metastone.game.spells.trigger;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.targeting.EntityReference;

public interface IGameEventListener {

	public IGameEventListener clone();

	public abstract EntityReference getHostReference();

	public abstract TriggerLayer getLayer();

	public abstract int getOwner();

	public abstract boolean interestedIn(GameEventType eventType);

	public abstract boolean isExpired();

	public abstract void onAdd(GameContext context);

	public abstract void onGameEvent(GameEvent event);

	public abstract void onRemove(GameContext context);

	public abstract void setHost(Entity host);
	
	public abstract void setOwner(int playerIndex);

}