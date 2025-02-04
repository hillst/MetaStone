package net.demilich.metastone.game.spells;

import java.util.Map;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporaryAttackSpell extends Spell {
	
	private static Logger logger = LoggerFactory.getLogger(TemporaryAttackSpell.class);

	public static SpellDesc create(EntityReference target, int attackBonus) {
		Map<SpellArg, Object> arguments = SpellDesc.build(TemporaryAttackSpell.class);
		arguments.put(SpellArg.VALUE, attackBonus);
		arguments.put(SpellArg.TARGET, target);
		return new SpellDesc(arguments);
	}

	public static SpellDesc create(int attackBonus) {
		return create(null, attackBonus);
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		int attackBonus = desc.getValue();

		IValueProvider attackValueProvider = (IValueProvider) desc.get(SpellArg.VALUE_PROVIDER);

		if (attackValueProvider != null) {
			attackBonus = attackValueProvider.provideValue(context, player, target);
		}

		logger.debug("{} gains {} attack", target, attackBonus);

		Actor targetActor = (Actor) target;

		if (attackBonus != 0) {
			targetActor.modifyTag(GameTag.TEMPORARY_ATTACK_BONUS, +attackBonus);
		}
	}

}
