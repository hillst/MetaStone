package net.demilich.metastone.game.spells.custom;

import java.util.Map;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.Spell;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

public class SwapAttackAndHpSpell extends Spell {
	
	public static SpellDesc create() {
		return create(null);
	}

	public static SpellDesc create(EntityReference target) {
		Map<SpellArg, Object> arguments = SpellDesc.build(SwapAttackAndHpSpell.class);
		arguments.put(SpellArg.TARGET, target);
		return new SpellDesc(arguments);
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		Minion minion = (Minion) target;
		int attack = minion.getAttack();
		int hp = minion.getHp();
		minion.setAttack(hp);
		context.getLogic().modifyMaxHp(minion, attack);
		minion.removeTag(GameTag.TEMPORARY_ATTACK_BONUS);
		minion.removeTag(GameTag.ATTACK_BONUS);
	}

}