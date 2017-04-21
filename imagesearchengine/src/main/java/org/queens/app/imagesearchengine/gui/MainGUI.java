package org.queens.app.imagesearchengine.gui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.queens.app.imagesearchengine.LibraryImage;
import org.queens.app.imagesearchengine.QueryImage;
import org.queens.app.imagesearchengine.Retriever;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainGUI extends Application {

	QueryImage selectedQueryImage;
	Retriever retriever;

	@Override
	public void start(Stage stage) throws Exception {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);

		final FileChooser fileChooser = new FileChooser();

		Image imageIcon = new Image("load_image_icon.png");

		ImageView imgView = new ImageView(imageIcon);
		imgView.setFitWidth(250);
		imgView.setPreserveRatio(true);
		grid.add(imgView, 0, 0, 4, 1);

		Button loadImageBtn = new Button("Load Query Image");
		grid.add(loadImageBtn, 0, 1);
		Button startButton = new Button("Run Algorithm");
		grid.add(startButton, 1, 1);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(
				5, 192));
		choiceBox.getSelectionModel().selectFirst();
		grid.add(choiceBox, 3, 1);
		
		Scene scene = new Scene(grid, 500, 350);
		stage.setTitle("Load An Image");
		stage.setScene(scene);
		stage.show();

		loadImageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				fileChooser.setTitle("Select query image");
				fileChooser.setInitialDirectory(new File(
						"testdata/query_images"));
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					imgView.setImage(new Image(file.toURI().toString()));
					try {
						selectedQueryImage = new QueryImage(ImageIO.read(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (selectedQueryImage == null) {
					System.out.println("No query selected");
				} else {
					retriever.submitQuery(selectedQueryImage);

					TableView<LibraryImage> table = new TableView<LibraryImage>();
					table.setEditable(false);

					TableColumn<LibraryImage, ImageView> imageColumn = new TableColumn<>(
							"Image");
					TableColumn<LibraryImage, Double> distanceColumn = new TableColumn<>(
							"Distance");

					final ObservableList<LibraryImage> data = FXCollections
							.observableArrayList();
					for (int i = 0; i != (int) choiceBox.getSelectionModel()
							.getSelectedItem(); i++) {
						data.add(retriever.getLibrary().get(i));
					}

					imageColumn
							.setCellValueFactory(c -> new SimpleObjectProperty<ImageView>(
									new ImageView(SwingFXUtils.toFXImage(c
											.getValue().getImageData(), null))));
					distanceColumn
							.setCellValueFactory(new PropertyValueFactory<LibraryImage, Double>(
									"Distance"));

					table.setItems(data);

					table.getColumns().add(imageColumn);
					table.getColumns().add(distanceColumn);

					VBox vbox = new VBox();
					vbox.getChildren().add(table);
					VBox.setVgrow(table, Priority.ALWAYS);

					Stage results = new Stage();
					Scene scene2 = new Scene(vbox, 1000, 700);
					results.setTitle("Results");
					results.setScene(scene2);
					results.show();
				}
			}
		});

	}

	public static void main(String[] args) {
        LauncherImpl.launchApplication(MainGUI.class, PreloaderGUI.class, args);
	}
	
    @Override
    public void init() throws Exception {
    	retriever = new Retriever(new File("testdata/library"));
        LauncherImpl.notifyPreloader(this, new PreloaderGUI.ProgressNotification(.10));
        
    	retriever.indexEdgeHistograms();
        LauncherImpl.notifyPreloader(this, new PreloaderGUI.ProgressNotification(.25));
        retriever.indexColourCorrelograms();
        LauncherImpl.notifyPreloader(this, new PreloaderGUI.ProgressNotification(.5));
        retriever.indexColourHistograms();
        LauncherImpl.notifyPreloader(this, new PreloaderGUI.ProgressNotification(.75));
        retriever.indexCooccurrenceMatrices();
        LauncherImpl.notifyPreloader(this, new PreloaderGUI.ProgressNotification(1));
        
        
        }
    }


