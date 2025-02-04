package net.demilich.metastone.game.cards.concrete.mage;

import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.SecretCard;
import net.demilich.metastone.game.cards.concrete.tokens.mage.SpellbenderToken;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.SummonNewAttackTargetSpell;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.trigger.SpellbenderTrigger;

public class Spellbender extends SecretCard {

	public Spellbender() {
		super("Spellbender", Rarity.EPIC, HeroClass.MAGE, 3);
		setDescription("Secret: When an enemy casts a spell on a minion, summon a 1/3 as the new target.");

		SpellDesc spellbenderSpell = SummonNewAttackTargetSpell.create(new SpellbenderToken());
		setTriggerAndEffect(new SpellbenderTrigger(), spellbenderSpell);
	}

	@Override
	public int getTypeId() {
		return 73;
	}
}
