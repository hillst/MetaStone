package net.demilich.metastone.game.cards.concrete.goblinsvsgnomes.druid;

import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.actions.BattlecryAction;
import net.demilich.metastone.game.cards.ChooseBattlecryCard;
import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.entities.minions.Race;
import net.demilich.metastone.game.spells.BuffSpell;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

public class AnodizedRoboCub extends ChooseBattlecryCard {

	public AnodizedRoboCub() {
		super("Anodized Robo Cub", 2, 2, Rarity.COMMON, HeroClass.DRUID, 2);
		setDescription("Taunt. Choose One - +1 Attack; or +1 Health.");
		setRace(Race.MECH);
	}

	@Override
	protected String getAction1Suffix() {
		return "+1 Attack";
	}

	@Override
	protected String getAction2Suffix() {
		return "+1 Health";
	}

	@Override
	protected BattlecryAction getBattlecry1() {
		SpellDesc spell = BuffSpell.create(EntityReference.SELF, +1, 0);
		return BattlecryAction.createBattlecry(spell);
	}

	@Override
	protected BattlecryAction getBattlecry2() {
		SpellDesc spell = BuffSpell.create(EntityReference.SELF, 0, +1);
		return BattlecryAction.createBattlecry(spell);
	}

	@Override
	public int getTypeId() {
		return 476;
	}



	@Override
	public Minion summon() {
		return createMinion(GameTag.TAUNT);
	}
}
