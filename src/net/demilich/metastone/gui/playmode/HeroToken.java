package net.demilich.metastone.gui.playmode;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import net.demilich.metastone.game.GameTag;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.heroes.Hero;
import net.demilich.metastone.game.entities.weapons.Weapon;
import net.demilich.metastone.gui.DigitFactory;
import net.demilich.metastone.gui.IconFactory;

public class HeroToken extends GameToken {

	@FXML
	private Group attackAnchor;
	@FXML
	private Group hpAnchor;
	@FXML
	private Label manaLabel;

	@FXML
	private Group armorAnchor;
	@FXML
	private ImageView armorIcon;

	@FXML
	private Pane weaponPane;
	@FXML
	private Label weaponNameLabel;
	@FXML
	private Group weaponAttackAnchor;
	@FXML
	private Group weaponDurabilityAnchor;

	@FXML
	private ImageView portrait;

	@FXML
	private ImageView heroPowerIcon;

	@FXML
	private Pane secretsAnchor;
	
	@FXML
	private Shape frozen;

	public HeroToken() {
		super("HeroToken.fxml");
		frozen.getStrokeDashArray().add(16.0);
	}

	public void highlight(boolean highlight) {
		String cssBorder = null;
		if (highlight) {
			cssBorder = "-fx-border-color:seagreen; \n" + "-fx-border-radius:7;\n" + "-fx-border-width:5.0;";
		} else {
			cssBorder = "-fx-border-color:transparent; \n" + "-fx-border-radius:7;\n" + "-fx-border-width:5.0;";
		}

		target.setStyle(cssBorder);
	}

	public void setHero(Player player) {
		Hero hero = player.getHero();
		DigitFactory.showPreRenderedDigits(attackAnchor, hero.getAttack());
		Image portraitImage = new Image(IconFactory.getHeroIconUrl(hero.getHeroClass()));
		portrait.setImage(portraitImage);
		Image heroPowerImage = new Image(IconFactory.getHeroPowerIconUrl(hero.getHeroPower()));
		heroPowerIcon.setImage(heroPowerImage);
		DigitFactory.showPreRenderedDigits(hpAnchor, hero.getHp());
		manaLabel.setText("Mana: " + player.getMana() + "/" + player.getMaxMana());
		updateArmor(hero.getArmor());
		updateWeapon(hero.getWeapon());
		updateSecrets(player);
		updateStatus(hero);
	}

	private void updateArmor(int armor) {
		DigitFactory.showPreRenderedDigits(armorAnchor, armor);
		boolean visible = armor > 0;
		armorIcon.setVisible(visible);
		armorAnchor.setVisible(visible);
	}
	
	private void updateSecrets(Player player) {
		secretsAnchor.getChildren().clear();
		for (int i = 0; i < player.getSecrets().size(); i++) {
			ImageView secretIcon = new ImageView(IconFactory.getImageUrl("common/secret.png"));
			secretsAnchor.getChildren().add(secretIcon);
		}
	}

	private void updateStatus(Hero hero) {
		frozen.setVisible(hero.hasStatus(GameTag.FROZEN));
	}

	private void updateWeapon(Weapon weapon) {
		boolean hasWeapon = weapon != null;
		weaponPane.setVisible(hasWeapon);
		if (hasWeapon) {
			weaponNameLabel.setText(weapon.getName());
			DigitFactory.showPreRenderedDigits(weaponAttackAnchor, weapon.getWeaponDamage());
			DigitFactory.showPreRenderedDigits(weaponDurabilityAnchor, weapon.getDurability());
		}
	}

}