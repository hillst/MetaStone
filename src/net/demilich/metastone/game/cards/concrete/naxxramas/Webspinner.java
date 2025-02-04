package net.demilich.metastone.game.cards.concrete.naxxramas;

import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.entities.minions.Race;
import net.demilich.metastone.game.spells.ReceiveRandomRaceCardSpell;

public class Webspinner extends MinionCard {

	public Webspinner() {
		super("Webspinner", 1, 1, Rarity.COMMON, HeroClass.HUNTER, 1);
		setDescription("Deathrattle: Add a random Beast card to your hand.");
		setRace(Race.BEAST);
	}

	@Override
	public int getTypeId() {
		return 412;
	}

	@Override
	public Minion summon() {
		Minion webspinner = createMinion();
		webspinner.addDeathrattle(ReceiveRandomRaceCardSpell.create(Race.BEAST, 1));
		return webspinner;
	}

	
}
