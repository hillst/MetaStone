package net.demilich.metastone.game.events;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.entities.Entity;

public class SpellCastedEvent extends GameEvent {

	private final int playerId;
	private final Card sourceCard;

	public SpellCastedEvent(GameContext context, int playerId, Card sourceCard) {
		super(context);
		this.playerId = playerId;
		this.sourceCard = sourceCard;
	}

	@Override
	public Entity getEventTarget() {
		return getSourceCard();
	}

	@Override
	public GameEventType getEventType() {
		return GameEventType.SPELL_CASTED;
	}

	public int getPlayerId() {
		return playerId;
	}

	public Card getSourceCard() {
		return sourceCard;
	}

}
