package net.demilich.metastone.game.cards.concrete.tokens.neutral;

import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.entities.minions.Race;

public class Murloc extends MinionCard {

	public Murloc() {
		super("Murloc", 1, 1, Rarity.FREE, HeroClass.ANY, 1);
		setRace(Race.MURLOC);

		setCollectible(false);
	}

	@Override
	public int getTypeId() {
		return 446;
	}

	@Override
	public Minion summon() {
		return createMinion();
	}
}
