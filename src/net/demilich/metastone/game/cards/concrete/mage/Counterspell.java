package net.demilich.metastone.game.cards.concrete.mage;

import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.SecretCard;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.spells.ApplyTagSpell;
import net.demilich.metastone.game.spells.TargetPlayer;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.trigger.SpellCastedTrigger;
import net.demilich.metastone.game.targeting.EntityReference;

public class Counterspell extends SecretCard {

	public Counterspell() {
		super("Counterspell", Rarity.RARE, HeroClass.MAGE, 3);
		setDescription("Secret: When your opponent casts a spell, Counter it.");

		SpellDesc counterSpell = ApplyTagSpell.create(EntityReference.PENDING_CARD, GameTag.COUNTERED);
		setTriggerAndEffect(new SpellCastedTrigger(TargetPlayer.OPPONENT), counterSpell);
	}

	@Override
	public int getTypeId() {
		return 57;
	}
}
