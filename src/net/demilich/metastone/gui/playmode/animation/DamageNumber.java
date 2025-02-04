package net.demilich.metastone.gui.playmode.animation;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import net.demilich.metastone.ApplicationFacade;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.gui.IconFactory;
import net.demilich.metastone.gui.playmode.GameToken;

public class DamageNumber extends StackPane {
	
	private final GameToken parent;

	public DamageNumber(String text, GameToken parent) {
		this.parent = parent;
		this.setAlignment(Pos.CENTER);
		
		ImageView image = new ImageView(IconFactory.getImageUrl("common/splash.png"));
		image.setFitWidth(96);
		image.setFitHeight(96);
		
		Text textShape = new Text(text);
		textShape.setFill(Color.WHITE);
		textShape.setStyle("-fx-font-size: 22pt; -fx-font-family: \"System\";-fx-font-weight: 900;-fx-stroke: black;-fx-stroke-width: 2;");
		
		setCache(true);
		setCacheHint(CacheHint.SPEED);
		
		getChildren().add(image);
		getChildren().add(textShape);
		parent.getAnchor().getChildren().add(this);
		
		ApplicationFacade.getInstance().sendNotification(GameNotification.ANIMATION_STARTED);
		
		PauseTransition animation = new PauseTransition(Duration.seconds(1.2));
		animation.setOnFinished(this::onComplete);
		animation.play();
	}
	
	private void onComplete(ActionEvent event) {
		parent.getAnchor().getChildren().remove(this);
		ApplicationFacade.getInstance().sendNotification(GameNotification.ANIMATION_COMPLETED);
	}
}
