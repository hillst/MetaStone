package net.demilich.metastone.game.cards.concrete.warlock;

import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.SpellCard;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.custom.SenseDemonsSpell;
import net.demilich.metastone.game.targeting.TargetSelection;

public class SenseDemons extends SpellCard {

	public SenseDemons() {
		super("Sense Demons", Rarity.COMMON, HeroClass.WARLOCK, 3);
		setDescription("Put 2 random Demons from your deck into your hand.");
		setSpell(SenseDemonsSpell.create());
		setTargetRequirement(TargetSelection.NONE);
	}

	@Override
	public int getTypeId() {
		return 349;
	}

}
