package net.demilich.metastone.game.cards.concrete.neutral;

import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.actions.BattlecryAction;
import net.demilich.metastone.game.cards.MinionCard;
import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.concrete.tokens.neutral.MurlocScout;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.entities.minions.Race;
import net.demilich.metastone.game.entities.minions.RelativeToSource;
import net.demilich.metastone.game.spells.SummonSpell;
import net.demilich.metastone.game.targeting.TargetSelection;

public class MurlocTidehunter extends MinionCard {

	public MurlocTidehunter() {
		super("Murloc Tidehunter", 2, 1, Rarity.FREE, HeroClass.ANY, 2);
		setDescription("Battlecry: Summon a 1/1 Murloc Scout.");
		setRace(Race.MURLOC);
		setTag(GameTag.BATTLECRY);
	}

	@Override
	public int getTypeId() {
		return 172;
	}

	@Override
	public Minion summon() {
		Minion murlocTidehunter = createMinion();
		BattlecryAction battlecry = BattlecryAction.createBattlecry(SummonSpell.create(RelativeToSource.RIGHT, new MurlocScout()), TargetSelection.NONE);
		battlecry.setResolvedLate(true);
		murlocTidehunter.setBattlecry(battlecry);
		return murlocTidehunter;
	}
}
