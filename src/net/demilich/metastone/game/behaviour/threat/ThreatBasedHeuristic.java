package net.demilich.metastone.game.behaviour.threat;

import java.util.ArrayList;
import java.util.List;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.concrete.druid.Naturalize;
import net.demilich.metastone.game.cards.concrete.goblinsvsgnomes.warrior.Crush;
import net.demilich.metastone.game.cards.concrete.hunter.DeadlyShot;
import net.demilich.metastone.game.cards.concrete.mage.Polymorph;
import net.demilich.metastone.game.cards.concrete.paladin.Equality;
import net.demilich.metastone.game.cards.concrete.paladin.Humility;
import net.demilich.metastone.game.cards.concrete.priest.ShadowWordDeath;
import net.demilich.metastone.game.cards.concrete.rogue.Assassinate;
import net.demilich.metastone.game.cards.concrete.rogue.Sap;
import net.demilich.metastone.game.cards.concrete.shaman.Hex;
import net.demilich.metastone.game.cards.concrete.warlock.SiphonSoul;
import net.demilich.metastone.game.cards.concrete.warrior.Execute;
import net.demilich.metastone.game.entities.heroes.Hero;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.minions.Minion;

public class ThreatBasedHeuristic implements IGameStateHeuristic {

	private static List<Integer> hardRemoval;

	static {
		hardRemoval = new ArrayList<Integer>();
		hardRemoval.add(new Polymorph().getTypeId());
		hardRemoval.add(new Execute().getTypeId());
		hardRemoval.add(new Crush().getTypeId());
		hardRemoval.add(new Assassinate().getTypeId());
		hardRemoval.add(new SiphonSoul().getTypeId());
		hardRemoval.add(new ShadowWordDeath().getTypeId());
		hardRemoval.add(new Execute().getTypeId());
		hardRemoval.add(new Naturalize().getTypeId());
		hardRemoval.add(new Hex().getTypeId());
		hardRemoval.add(new Humility().getTypeId());
		hardRemoval.add(new Equality().getTypeId());
		hardRemoval.add(new DeadlyShot().getTypeId());
		hardRemoval.add(new Sap().getTypeId());
	}

	private static ThreatLevel calcuateThreatLevel(GameContext context, int playerId) {
		int damageOnBoard = 0;
		Player player = context.getPlayer(playerId);
		Player opponent = context.getOpponent(player);
		for (Minion minion : opponent.getMinions()) {
			damageOnBoard += minion.getAttack() * minion.getTagValue(GameTag.NUMBER_OF_ATTACKS);
		}
		damageOnBoard += getHeroDamage(opponent.getHero());

		int remainingHp = player.getHero().getEffectiveHp() - damageOnBoard;
		if (remainingHp < 1) {
			return ThreatLevel.RED;
		} else if (remainingHp < 15) {
			return ThreatLevel.YELLOW;
		}

		return ThreatLevel.GREEN;
	}

	private static int getHeroDamage(Hero hero) {
		int heroDamage = 0;
		if (hero.getHeroClass() == HeroClass.MAGE) {
			heroDamage += 1;
		} else if (hero.getHeroClass() == HeroClass.HUNTER) {
			heroDamage += 2;
		} else if (hero.getHeroClass() == HeroClass.DRUID) {
			heroDamage += 1;
		} else if (hero.getHeroClass() == HeroClass.ROGUE) {
			heroDamage += 1;
		}
		if (hero.getWeapon() != null) {
			heroDamage += hero.getWeapon().getWeaponDamage();
		}
		return heroDamage;
	}

	private static boolean isHardRemoval(Card card) {
		return hardRemoval.contains(card.getTypeId());
	}

	private final FeatureVector weights;

	public ThreatBasedHeuristic(FeatureVector vector) {
		this.weights = vector;
	}

	private double calculateMinionScore(Minion minion, ThreatLevel threatLevel) {
		if (minion.hasStatus(GameTag.MARKED_FOR_DEATH)) {
			return 0;
		}
		double minionScore = weights.get(WeightedFeature.MINION_INTRINSIC_VALUE);
		minionScore += weights.get(WeightedFeature.MINION_ATTACK_FACTOR)
				* (minion.getAttack() - minion.getTagValue(GameTag.TEMPORARY_ATTACK_BONUS));
		minionScore += weights.get(WeightedFeature.MINION_HP_FACTOR) * minion.getHp();

		if (minion.hasStatus(GameTag.TAUNT)) {
			switch (threatLevel) {
			case RED:
				minionScore += weights.get(WeightedFeature.MINION_RED_TAUNT_MODIFIER);
				break;
			case YELLOW:
				minionScore += weights.get(WeightedFeature.MINION_YELLOW_TAUNT_MODIFIER);
				break;
			default:
				minionScore += weights.get(WeightedFeature.MINION_DEFAULT_TAUNT_MODIFIER);
				break;
			}
		}
		if (minion.hasStatus(GameTag.WINDFURY)) {
			minionScore += weights.get(WeightedFeature.MINION_WINDFURY_MODIFIER);
		}
		if (minion.hasStatus(GameTag.DIVINE_SHIELD)) {
			minionScore += weights.get(WeightedFeature.MINION_DIVINE_SHIELD_MODIFIER);
		}
		if (minion.hasStatus(GameTag.SPELL_POWER)) {
			minionScore += minion.getTagValue(GameTag.SPELL_POWER) * weights.get(WeightedFeature.MINION_SPELL_POWER_MODIFIER);
		}

		if (minion.hasStatus(GameTag.STEALTHED)) {
			minionScore += weights.get(WeightedFeature.MINION_STEALTHED_MODIFIER);
		}
		if (minion.hasStatus(GameTag.UNTARGETABLE_BY_SPELLS)) {
			minionScore += weights.get(WeightedFeature.MINION_UNTARGETABLE_BY_SPELLS_MODIFIER);
		}

		return minionScore;
	}

	@Override
	public double getScore(GameContext context, int playerId) {
		Player player = context.getPlayer(playerId);
		Player opponent = context.getOpponent(player);
		if (player.getHero().isDead()) {
			return Float.NEGATIVE_INFINITY;
		}
		if (opponent.getHero().isDead()) {
			return Float.POSITIVE_INFINITY;
		}
		double score = 0;

		ThreatLevel threatLevel = calcuateThreatLevel(context, playerId);
		switch (threatLevel) {
		case RED:
			score += weights.get(WeightedFeature.RED_MODIFIER);
			break;
		case YELLOW:
			score += weights.get(WeightedFeature.YELLOW_MODIFIER);
			break;
		default:
			break;
		}
		score += player.getHero().getEffectiveHp() * weights.get(WeightedFeature.OWN_HP_FACTOR);
		score += opponent.getHero().getEffectiveHp() * weights.get(WeightedFeature.OPPONENT_HP_FACTOR);
		for (Card card : player.getHand()) {
			if (isHardRemoval(card)) {
				score += weights.get(WeightedFeature.HARD_REMOVAL_VALUE);
			}
		}

		score += player.getHand().getCount() * weights.get(WeightedFeature.OWN_CARD_COUNT);
		score += opponent.getHand().getCount() * weights.get(WeightedFeature.OPPONENT_CARD_COUNT);

		for (Minion minion : player.getMinions()) {
			score += calculateMinionScore(minion, threatLevel);
		}

		for (Minion minion : opponent.getMinions()) {
			score -= calculateMinionScore(minion, threatLevel);
		}

		return score;
	}

	@Override
	public void onActionSelected(GameContext context, int playerId) {

	}

}
