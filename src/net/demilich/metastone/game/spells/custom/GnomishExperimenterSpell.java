package net.demilich.metastone.game.spells.custom;

import java.util.Map;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.cards.concrete.goblinsvsgnomes.tokens.Chicken;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

public class GnomishExperimenterSpell extends Spell {

	public static SpellDesc create() {
		Map<SpellArg, Object> arguments = SpellDesc.build(GnomishExperimenterSpell.class);
		arguments.put(SpellArg.TARGET, EntityReference.NONE);
		return new SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		Card card = context.getLogic().drawCard(player.getId());
		if (card == null) {
			return;
		}
		if (card.getCardType() == CardType.MINION) {
			player.getHand().remove(card);
			context.getLogic().receiveCard(player.getId(), new Chicken());
		}
	}

}
