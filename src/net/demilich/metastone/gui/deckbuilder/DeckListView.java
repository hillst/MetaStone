package net.demilich.metastone.gui.deckbuilder;

import java.io.IOException;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.demilich.metastone.ApplicationFacade;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.decks.Deck;

public class DeckListView extends VBox implements EventHandler<MouseEvent> {

	@FXML
	private Button newDeckButton;

	public DeckListView() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DeckListView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		newDeckButton.setOnAction(actionEvent -> ApplicationFacade.getInstance().sendNotification(GameNotification.CREATE_NEW_DECK));
		setCache(true);
	}
	
	private void clearChildren() {
		for (Node child : getChildren()) {
			child.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
		}
		getChildren().clear();
	}
	
	public void displayDecks(List<Deck> decks) {
		clearChildren();
		getChildren().add(newDeckButton);
		for (Deck deck : decks) {
			DeckEntry deckEntry = new DeckEntry();
			deckEntry.setDeck(deck);
			deckEntry.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			getChildren().add(deckEntry);
		}
	}

	@Override
	public void handle(MouseEvent event) {
		DeckEntry deckEntry = (DeckEntry) event.getSource();
		ApplicationFacade.getInstance().sendNotification(GameNotification.SET_ACTIVE_DECK, deckEntry.getDeck());
	}

}
