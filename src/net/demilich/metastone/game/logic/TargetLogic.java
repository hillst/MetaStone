package net.demilich.metastone.game.logic;

import java.util.ArrayList;
import java.util.List;

import net.demilich.metastone.game.Environment;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.targeting.EntityReference;
import net.demilich.metastone.game.targeting.TargetSelection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetLogic {

	private static Logger logger = LoggerFactory.getLogger(TargetLogic.class);

	private static List<Entity> singleTargetAsList(Entity target) {
		ArrayList<Entity> list = new ArrayList<>(1);
		list.add(target);
		return list;
	}

	private boolean containsTaunters(List<Minion> minions) {
		for (Entity entity : minions) {
			if (entity.hasStatus(GameTag.TAUNT) && !entity.hasStatus(GameTag.STEALTHED)) {
				return true;
			}
		}
		return false;
	}

	private List<Entity> filterTargets(GameContext context, Player player, GameAction action, List<Entity> potentialTargets) {
		List<Entity> validTargets = new ArrayList<>();
		for (Entity entity : potentialTargets) {
			// special case for 'SYSTEM' action, which are used in Sandbox Mode
			// we do not want to restrict those actions by STEALTH or
			// UNTARGETABLE_BY_SPELLS
			if (action.getActionType() == ActionType.SYSTEM && action.canBeExecutedOn(context, entity)) {
				validTargets.add(entity);
				continue;
			}
			if ((action.getActionType() == ActionType.SPELL || action.getActionType() == ActionType.HERO_POWER)
					&& entity.hasStatus(GameTag.UNTARGETABLE_BY_SPELLS)) {
				continue;
			}

			if (entity.getOwner() != player.getId() && entity.hasStatus(GameTag.STEALTHED)) {
				continue;
			}

			if (action.canBeExecutedOn(context, entity)) {
				validTargets.add(entity);
			}
		}
		return validTargets;
	}

	public Entity findEntity(GameContext context, EntityReference targetKey) {
		int targetId = targetKey.getId();
		Entity environmentResult = findInEnvironment(context, targetKey);
		if (environmentResult != null) {
			return environmentResult;
		}
		for (Player player : context.getPlayers()) {
			if (player.getHero().getId() == targetId) {
				return player.getHero();
			} else if (player.getHero().getWeapon() != null && player.getHero().getWeapon().getId() == targetId) {
				return player.getHero().getWeapon();
			}

			for (Actor minion : player.getMinions()) {
				if (minion.getId() == targetId) {
					return minion;
				}
			}

			for (Actor actor : player.getGraveyard()) {
				if (actor.getId() == targetId) {
					return actor;
				}
			}
		}

		Entity cardResult = findInCards(context.getPlayer1(), targetId);
		if (cardResult == null) {
			cardResult = findInCards(context.getPlayer2(), targetId);
		}
		if (cardResult != null) {
			return cardResult;
		}

		logger.error("Id " + targetId + " not found!");
		logger.error(context.toString());
		return null;
		//throw new RuntimeException("Target not found exception: " + targetKey);
	}

	private Entity findInCards(Player player, int targetId) {
		if (player.getHero().getHeroPower().getId() == targetId) {
			return player.getHero().getHeroPower();
		}
		for (Card card : player.getHand()) {
			if (card.getId() == targetId) {
				return card;
			}
		}
		for (Card card : player.getDeck()) {
			if (card.getId() == targetId) {
				return card;
			}
		}

		return null;
	}

	private Entity findInEnvironment(GameContext context, EntityReference targetKey) {
		int targetId = targetKey.getId();
		Card pendingCard = (Card) context.getEnvironment().get(Environment.PENDING_CARD);
		if (pendingCard != null && pendingCard.getReference().equals(targetKey)) {
			return pendingCard;
		}
		if (!context.getSummonStack().isEmpty()) {
			Minion summonedMinion = context.getSummonStack().peek();
			if (summonedMinion.getId() == targetId) {
				return summonedMinion;
			}
		}
		if (context.getEnvironment().containsKey(Environment.SUMMONED_WEAPON)) {
			Actor summonedWeapon = (Actor) context.getEnvironment().get(Environment.SUMMONED_WEAPON);
			if (summonedWeapon.getId() == targetId) {
				return summonedWeapon;
			}
		}
		return null;
	}

	private List<Entity> getEntities(GameContext context, Player player, TargetSelection targetRequirement) {
		Player opponent = context.getOpponent(player);
		List<Entity> entities = new ArrayList<>();
		if (targetRequirement == TargetSelection.ENEMY_HERO || targetRequirement == TargetSelection.ENEMY_CHARACTERS
				|| targetRequirement == TargetSelection.ANY || targetRequirement == TargetSelection.HEROES) {
			entities.add(opponent.getHero());
		}
		if (targetRequirement == TargetSelection.ENEMY_MINIONS || targetRequirement == TargetSelection.ENEMY_CHARACTERS
				|| targetRequirement == TargetSelection.MINIONS || targetRequirement == TargetSelection.ANY) {
			entities.addAll(opponent.getMinions());
		}
		if (targetRequirement == TargetSelection.FRIENDLY_HERO || targetRequirement == TargetSelection.FRIENDLY_CHARACTERS
				|| targetRequirement == TargetSelection.ANY || targetRequirement == TargetSelection.HEROES) {
			entities.add(player.getHero());
		}
		if (targetRequirement == TargetSelection.FRIENDLY_MINIONS || targetRequirement == TargetSelection.FRIENDLY_CHARACTERS
				|| targetRequirement == TargetSelection.MINIONS || targetRequirement == TargetSelection.ANY) {
			entities.addAll(player.getMinions());
		}
		return entities;
	}

	private List<Entity> getTaunters(List<Minion> entities) {
		List<Entity> taunters = new ArrayList<>();
		for (Actor entity : entities) {
			if (entity.hasStatus(GameTag.TAUNT) && !entity.hasStatus(GameTag.STEALTHED)) {
				taunters.add(entity);
			}
		}
		return taunters;
	}

	public List<Entity> getValidTargets(GameContext context, Player player, GameAction action) {
		TargetSelection targetRequirement = action.getTargetRequirement();
		ActionType actionType = action.getActionType();
		Player opponent = context.getOpponent(player);

		// if there is a minion with TAUNT and the action is of type physical
		// attack only allow corresponding minions as targets
		if (actionType == ActionType.PHYSICAL_ATTACK
				&& (targetRequirement == TargetSelection.ENEMY_CHARACTERS || targetRequirement == TargetSelection.ENEMY_MINIONS)
				&& containsTaunters(opponent.getMinions())) {
			return getTaunters(opponent.getMinions());
		}
		if (actionType == ActionType.SUMMON) {
			// you can summon next to any friendly minion or provide no target
			// (=null)
			// in which case the minion will appear to the very right of your
			// board
			List<Entity> summonTargets = getEntities(context, player, targetRequirement);
			summonTargets.add(null);
			return summonTargets;
		}
		List<Entity> potentialTargets = getEntities(context, player, targetRequirement);
		return filterTargets(context, player, action, potentialTargets);
	}

	public List<Entity> resolveTargetKey(GameContext context, Player player, Entity source, EntityReference targetKey) {
		if (targetKey == null) {
			return null;
		}
		if (targetKey == EntityReference.ALL_CHARACTERS) {
			return getEntities(context, player, TargetSelection.ANY);
		} else if (targetKey == EntityReference.ALL_MINIONS) {
			return getEntities(context, player, TargetSelection.MINIONS);
		} else if (targetKey == EntityReference.ENEMY_CHARACTERS) {
			return getEntities(context, player, TargetSelection.ENEMY_CHARACTERS);
		} else if (targetKey == EntityReference.ENEMY_HERO) {
			return getEntities(context, player, TargetSelection.ENEMY_HERO);
		} else if (targetKey == EntityReference.ENEMY_MINIONS) {
			return getEntities(context, player, TargetSelection.ENEMY_MINIONS);
		} else if (targetKey == EntityReference.FRIENDLY_CHARACTERS) {
			return getEntities(context, player, TargetSelection.FRIENDLY_CHARACTERS);
		} else if (targetKey == EntityReference.FRIENDLY_HERO) {
			return getEntities(context, player, TargetSelection.FRIENDLY_HERO);
		} else if (targetKey == EntityReference.FRIENDLY_MINIONS) {
			return getEntities(context, player, TargetSelection.FRIENDLY_MINIONS);
		} else if (targetKey == EntityReference.OTHER_FRIENDLY_MINIONS) {
			List<Entity> targets = getEntities(context, player, TargetSelection.FRIENDLY_MINIONS);
			targets.remove(source);
			return targets;
		} else if (targetKey == EntityReference.ALL_OTHER_CHARACTERS) {
			List<Entity> targets = getEntities(context, player, TargetSelection.ANY);
			targets.remove(source);
			return targets;
		} else if (targetKey == EntityReference.ADJACENT_MINIONS) {
			return new ArrayList<>(context.getAdjacentMinions(player, source.getReference()));
		} else if (targetKey == EntityReference.SELF) {
			return singleTargetAsList(source);
		} else if (targetKey == EntityReference.EVENT_TARGET) {
			return singleTargetAsList((Entity) context.getEnvironment().get(Environment.EVENT_TARGET));
		} else if (targetKey == EntityReference.KILLED_MINION) {
			return singleTargetAsList((Entity) context.getEnvironment().get(Environment.KILLED_MINION));
		} else if (targetKey == EntityReference.ATTACKER) {
			return singleTargetAsList((Entity) context.getEnvironment().get(Environment.ATTACKER));
		} else if (targetKey == EntityReference.PENDING_CARD) {
			return singleTargetAsList((Entity) context.getEnvironment().get(Environment.PENDING_CARD));
		} else if (targetKey == EntityReference.FRIENDLY_WEAPON) {
			if (player.getHero().getWeapon() != null) {
				return singleTargetAsList(player.getHero().getWeapon());
			} else {
				return new ArrayList<>();
			}
		} else if (targetKey == EntityReference.ENEMY_WEAPON) {
			Player opponent = context.getOpponent(player);
			if (opponent.getHero().getWeapon() != null) {
				return singleTargetAsList(opponent.getHero().getWeapon());
			} else {
				return new ArrayList<>();
			}
		} else if (targetKey == EntityReference.NONE) {
			return null;
		}

		return singleTargetAsList(findEntity(context, targetKey));
	}

}
