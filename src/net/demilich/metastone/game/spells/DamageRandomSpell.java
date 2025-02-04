package net.demilich.metastone.game.spells;

import java.util.List;
import java.util.Map;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardType;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.EntityType;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

public class DamageRandomSpell extends DamageSpell {
	
	public static SpellDesc create(EntityReference target, int damage, int iterations) {
		Map<SpellArg, Object> arguments = SpellDesc.build(DamageRandomSpell.class);
		arguments.put(SpellArg.VALUE, damage);
		arguments.put(SpellArg.ITERATIONS, iterations);
		arguments.put(SpellArg.TARGET, target);
		return new SpellDesc(arguments);
	}
	
	public static SpellDesc create(int damage, int iterations) {
		return create(null, damage, iterations);
	}

	@Override
	public void cast(GameContext context, Player player, SpellDesc desc, Entity source, List<Entity> targets) {
		int missiles = desc.getInt(SpellArg.ITERATIONS);
		int damage = desc.getInt(SpellArg.VALUE);
		
		if (source.getEntityType() == EntityType.CARD && ((Card)source).getCardType() == CardType.SPELL) {
			missiles = context.getLogic().applySpellpower(player, missiles);
			missiles = context.getLogic().applyAmplify(player, missiles);
		}
		for (int i = 0; i < missiles; i++) {
			List<Actor> validTargets = SpellUtils.getValidRandomTargets(targets);
			Actor randomTarget = SpellUtils.getRandomTarget(validTargets);
			context.getLogic().damage(player, randomTarget, damage, source);
		}
	}


	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
	}

}
