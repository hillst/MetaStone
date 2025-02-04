package net.demilich.metastone.game.spells.trigger.secrets;

import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.trigger.SpellTrigger;
import net.demilich.metastone.game.spells.trigger.TriggerLayer;

public class Secret extends SpellTrigger {

	private Card source;

	public Secret(SecretTrigger trigger, SpellDesc spell, Card source) {
		super(trigger, spell, true);
		this.source = source;
		setLayer(TriggerLayer.SECRET);
	}

	public Card getSource() {
		return source;
	}

	@Override
	protected void onFire(int ownerId, SpellDesc spell, GameEvent event) {
		super.onFire(ownerId, spell, event);
		Player owner = event.getGameContext().getPlayer(ownerId);
		event.getGameContext().getLogic().secretTriggered(owner, this);
	}

}
