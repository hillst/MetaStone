package net.demilich.metastone.game.cards.concrete.priest;

import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.SpellCard;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.DamageSpell;
import net.demilich.metastone.game.spells.HealingSpell;
import net.demilich.metastone.game.spells.MetaSpell;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;
import net.demilich.metastone.game.targeting.TargetSelection;

public class HolyNova extends SpellCard {
	
	public HolyNova() {
		super("Holy Nova", Rarity.FREE, HeroClass.PRIEST, 5);
		setDescription("Deal $2 damage to all enemies. Restore #2 Health to all friendly characters.");
		SpellDesc damageComponent = DamageSpell.create(EntityReference.ENEMY_CHARACTERS, 2);
		SpellDesc healComponent = HealingSpell.create(EntityReference.FRIENDLY_CHARACTERS, +2);
		setSpell(MetaSpell.create(damageComponent, healComponent));
		setTargetRequirement(TargetSelection.NONE);
	}

	@Override
	public int getTypeId() {
		return 264;
	}
}
