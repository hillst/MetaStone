package net.demilich.metastone.game.utils;

import net.demilich.metastone.game.GameTag;

public class GameTagUtils {
	
	public static String getTagName(GameTag tag) {
		return tag.toString();
	}

	public static TagValueType getTagValueType(GameTag tag) {
		switch (tag) {
		case ARMOR:
		case TEMPORARY_ATTACK_BONUS:
		case AURA_ATTACK_BONUS:
		case AURA_HP_BONUS:
		case DURABILITY:
		case HP:
		case HP_BONUS:
		case COMBO:
		case DIED_ON_TURN:
		case FATIGUE:
		case MAX_HP:
		case NUMBER_OF_ATTACKS:
		case OVERLOAD:
		case SPELL_AMPLIFY_MULTIPLIER:
		case SPELL_POWER:
		case WEAPON_DAMAGE:
		case ATTACK:
		case ATTACK_BONUS:
			return TagValueType.INTEGER;
		case ATTACK_EQUALS_HP:
		case CANNOT_ATTACK:
		case CANNOT_REDUCE_HP_BELOW_1:
		case CHARGE:
		case CONSUME_DAMAGE_INSTEAD_OF_DURABILITY_ON_MINIONS:
		case COUNTERED:
		case DIVINE_SHIELD:
		case DOUBLE_DEATHRATTLES:
		case ENRAGED:
		case FROZEN:
		case IMMUNE:
		case IMMUNE_WHILE_ATTACKING:
		case INVERT_HEALING:
		case SECRET:
		case SILENCED:
		case STEALTHED:
		case SUMMONING_SICKNESS:
		case TAUNT:
		case UNTARGETABLE_BY_SPELLS:
		case WINDFURY:
			return TagValueType.BOOLEAN;
		case BASE_ATTACK:
		case BASE_HP:
		case BATTLECRY:
		case DEATHRATTLES:
		case DEBUG:
		case MANA_COST_MODIFIER:
		case RACE:
		case UNIQUE_ENTITY:
			return TagValueType.OTHER;
		default:
			break;
		}
		return TagValueType.OTHER;
	}
	
	private GameTagUtils() {
		
	}

}
