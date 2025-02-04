package net.demilich.metastone.game.cards.concrete.paladin;

import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.cards.Rarity;
import net.demilich.metastone.game.cards.WeaponCard;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.EntityType;
import net.demilich.metastone.game.entities.heroes.Hero;
import net.demilich.metastone.game.entities.heroes.HeroClass;
import net.demilich.metastone.game.entities.weapons.Weapon;
import net.demilich.metastone.game.events.GameEvent;
import net.demilich.metastone.game.events.GameEventType;
import net.demilich.metastone.game.events.TargetAcquisitionEvent;
import net.demilich.metastone.game.spells.HealingSpell;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.trigger.SpellTrigger;
import net.demilich.metastone.game.spells.trigger.TargetAcquisitionTrigger;
import net.demilich.metastone.game.targeting.EntityReference;

public class TruesilverChampion extends WeaponCard {

	public TruesilverChampion() {
		super("Truesilver Champion", Rarity.FREE, HeroClass.PALADIN, 4);
		setDescription("Whenever your hero attacks, restore #2 Health to it.");
	}

	@Override
	public int getTypeId() {
		return 258;
	}

	@Override
	public Weapon getWeapon() {
		Weapon trueSilverChampion = createWeapon(4, 2);
		SpellDesc healHero = HealingSpell.create(EntityReference.FRIENDLY_HERO, 2);
		SpellTrigger trigger = new SpellTrigger(new TruesilverChampionWeaponTrigger(), healHero);
		trueSilverChampion.setSpellTrigger(trigger);
		return trueSilverChampion;
	}

	private class TruesilverChampionWeaponTrigger extends TargetAcquisitionTrigger {

		public TruesilverChampionWeaponTrigger() {
			super(ActionType.PHYSICAL_ATTACK, EntityType.HERO);
		}

		@Override
		public boolean fire(GameEvent event, Entity host) {
			if (!super.fire(event, host)) {
				return false;
			}
			TargetAcquisitionEvent targetAcquisitionEvent = (TargetAcquisitionEvent) event;
			Hero hero = (Hero) targetAcquisitionEvent.getSource();
			return hero.getWeapon() == host;
		}

		@Override
		public GameEventType interestedIn() {
			return GameEventType.TARGET_ACQUISITION;
		}

	}
}
