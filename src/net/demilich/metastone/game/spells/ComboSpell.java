package net.demilich.metastone.game.spells;

import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.spells.desc.SpellDesc;

public class ComboSpell extends EitherOrSpell {
	
	public static SpellDesc create(SpellDesc noCombo, SpellDesc combo) {
		return EitherOrSpell.create(combo, noCombo, (context, player, target) -> player.getHero().hasStatus(GameTag.COMBO));
	}

}
