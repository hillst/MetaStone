package net.demilich.metastone.game.spells;

import java.util.Map;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

public class DrawCardAndDoSomethingSpell extends Spell {
	
	public static SpellDesc create(ICardProvider cardProvider, ICardPostProcessor cardProcessor) {
		return create(cardProvider, cardProcessor, 1);
	}

	public static SpellDesc create(ICardProvider cardProvider, ICardPostProcessor cardProcessor, int amount) {
		Map<SpellArg, Object> arguments = SpellDesc.build(DrawCardAndDoSomethingSpell.class);
		arguments.put(SpellArg.CARD_PROVIDER, cardProvider);
		arguments.put(SpellArg.CARD_PROCESSOR, cardProcessor);
		arguments.put(SpellArg.TARGET, EntityReference.NONE);
		arguments.put(SpellArg.VALUE, amount);
		return new SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		ICardProvider cardProvider = (ICardProvider) desc.get(SpellArg.CARD_PROVIDER);
		int amount = desc.getValue();
		for (int i = 0; i < amount; i++) {
			Card card = cardProvider.getCard(context, player);
			// card may be null (i.e. try to draw from deck, but already in fatigue)
			if (card == null) {
				return;
			}
			ICardPostProcessor cardPostProcessor = (ICardPostProcessor) desc.get(SpellArg.CARD_PROCESSOR);
			cardPostProcessor.processCard(context, player, card);	
		}
		
	}

}
