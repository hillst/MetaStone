package net.demilich.metastone.game.cards.costmodifier;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.logic.CustomCloneable;
import net.demilich.metastone.game.spells.TargetPlayer;
import net.demilich.metastone.game.spells.trigger.IGameEventListener;
import net.demilich.metastone.game.spells.trigger.TriggerLayer;
import net.demilich.metastone.game.targeting.EntityReference;

public abstract class CardCostModifier extends CustomCloneable implements IGameEventListener {

	private boolean expired;
	private int owner;
	private EntityReference hostReference;
	private int manaModifier;
	private int minValue;
	private CardType cardType;
	private GameTag requiredTag;
	private TargetPlayer targetPlayer = TargetPlayer.SELF;

	public CardCostModifier(CardType cardType, int manaModifier) {
		this.cardType = cardType;
		this.manaModifier = manaModifier;
	}

	protected boolean appliesTo(Card card) {
		if (getRequiredTag() != null && !card.hasTag(getRequiredTag())) {
			return false;
		}
		switch (getTargetPlayer()) {
		case BOTH:
			break;
		case OPPONENT:
			if (card.getOwner() == getOwner()) {
				return false;
			}
			break;
		case SELF:
			if (card.getOwner() != getOwner()) {
				return false;
			}
			break;
		default:
			break;

		}
		return card.getCardType() == cardType;
	}

	@Override
	public CardCostModifier clone() {
		CardCostModifier clone = (CardCostModifier) super.clone();
		return clone;
	}

	protected void expire() {
		expired = true;
	}

	@Override
	public EntityReference getHostReference() {
		return hostReference;
	}

	@Override
	public TriggerLayer getLayer() {
		return TriggerLayer.DEFAULT;
	}

	public int getMinValue() {
		return minValue;
	}

	@Override
	public int getOwner() {
		return owner;
	}

	public GameTag getRequiredTag() {
		return requiredTag;
	}

	public TargetPlayer getTargetPlayer() {
		return targetPlayer;
	}

	@Override
	public boolean isExpired() {
		return expired;
	}

	protected int modifyManaCost(Card card) {
		return manaModifier;
	}

	@Override
	public void onAdd(GameContext context) {
	}

	@Override
	public void onRemove(GameContext context) {
		expired = true;
	}

	public int process(Card card) {
		if (expired || !appliesTo(card)) {
			return 0;
		}

		return modifyManaCost(card);
	}

	@Override
	public void setHost(Entity host) {
		hostReference = host.getReference();
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	@Override
	public void setOwner(int playerIndex) {
		this.owner = playerIndex;
	}

	public void setRequiredTag(GameTag requiredTag) {
		this.requiredTag = requiredTag;
	}

	public void setTargetPlayer(TargetPlayer targetPlayer) {
		this.targetPlayer = targetPlayer;
	}

}
