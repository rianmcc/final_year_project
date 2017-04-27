package org.queens.app.imagesearchengine.gui;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class PreloaderGUI extends Preloader {

    private static final double WIDTH = 250;
    private static final double HEIGHT = 100;

    private Stage preloaderStage;
    private Scene scene;

    private ProgressBar bar;
    private Label description;



    @Override
    public void init() throws Exception {

        Platform.runLater(() -> {
            Label title = new Label("Indexing gallery!\nplease wait...");
            description = new Label(" ");
            description.setTextAlignment(TextAlignment.CENTER);
            title.setTextAlignment(TextAlignment.CENTER);
            bar = new ProgressBar();
            bar.setProgress(0);
            bar.setPrefWidth(200);

            VBox root = new VBox(title, bar, description);
            root.setAlignment(Pos.CENTER);

            scene = new Scene(root, WIDTH, HEIGHT);
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.preloaderStage = primaryStage;

        // Set preloader scene and show stage.
        preloaderStage.setScene(scene);
        preloaderStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            bar.setProgress(((ProgressNotification) info).getProgress());
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // Handle state change notifications.
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_LOAD:
                // Called after ThePreloader#start is called.
                break;
            case BEFORE_INIT:
                // Called before Main#init is called.
                break;
            case BEFORE_START:
                // Called after Main#init and before Main#start is called.

                preloaderStage.hide();
                break;
        }
    }

}
