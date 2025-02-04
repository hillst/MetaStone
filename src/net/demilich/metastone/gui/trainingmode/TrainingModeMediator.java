package net.demilich.metastone.gui.trainingmode;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import net.demilich.metastone.GameNotification;
import net.demilich.metastone.game.decks.Deck;
import de.pferdimanzug.nittygrittymvc.Mediator;
import de.pferdimanzug.nittygrittymvc.interfaces.INotification;

public class TrainingModeMediator extends Mediator<GameNotification> {

	public static final String NAME = "TrainingModeMediator";

	private final TrainingConfigView configView;
	private final TrainingModeView view;

	public TrainingModeMediator() {
		super(NAME);
		configView = new TrainingConfigView();
		view = new TrainingModeView();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleNotification(final INotification<GameNotification> notification) {
		switch (notification.getId()) {
		case TRAINING_PROGRESS_UPDATE:
			TrainingProgressReport progress = (TrainingProgressReport) notification.getBody();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					view.showProgress(progress);
				}
			});
			break;
		case COMMIT_TRAININGMODE_CONFIG:
			getFacade().sendNotification(GameNotification.SHOW_VIEW, view);
			TrainingConfig trainingConfig = (TrainingConfig) notification.getBody();
			view.setDeckName(trainingConfig.getDeckToTrain().getName());
			view.startTraining();
			getFacade().sendNotification(GameNotification.START_TRAINING, trainingConfig);
			break;
		case REPLY_DECKS:
			configView.injectDecks((List<Deck>) notification.getBody());
			break;
		default:
			break;
		}
	}

	@Override
	public List<GameNotification> listNotificationInterests() {
		List<GameNotification> notificationInterests = new ArrayList<GameNotification>();
		notificationInterests.add(GameNotification.TRAINING_PROGRESS_UPDATE);
		notificationInterests.add(GameNotification.COMMIT_TRAININGMODE_CONFIG);
		notificationInterests.add(GameNotification.REPLY_DECKS);
		return notificationInterests;
	}

	@Override
	public void onRegister() {
		getFacade().sendNotification(GameNotification.SHOW_VIEW, configView);
		getFacade().sendNotification(GameNotification.REQUEST_DECKS);
	}

}
