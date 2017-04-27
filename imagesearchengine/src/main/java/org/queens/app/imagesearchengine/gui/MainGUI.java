package org.queens.app.imagesearchengine.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import org.queens.app.imagesearchengine.QueryImage;
import org.queens.app.imagesearchengine.Retriever;

import com.sun.javafx.application.LauncherImpl;

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
		
		Scene scene = new Scene(grid, 400, 350);
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
					
					ArrayList<Image> galleryImages = new ArrayList<Image>();
					for (int i = 0; i != retriever.getGallery().size(); i++) {
						galleryImages.add(SwingFXUtils.toFXImage(retriever.getGallery().get(i).getImageData(), null));
					}
					
					GridPane imageGrid = new GridPane();
					imageGrid.setHgap(5);
					imageGrid.setVgap(0);
					int imageCol = 0;
					int imageRow = 0;
					Label distance;
					// stackoverflow.com/questions/25374578/adding-images-to-a-gridpane-javafx
					for (int i = 0; i != galleryImages.size(); i++) {
						ImageView picture = new ImageView(galleryImages.get(i));
						picture.setFitHeight(156);
						picture.setFitWidth(156);
						
						distance = new Label(String.format("Distance: %.5f",retriever.getGallery().get(i).getDistance()));
						GridPane.setHalignment(distance, HPos.CENTER);
						
						imageGrid.add(picture, imageCol, imageRow);
						imageGrid.add(distance, imageCol, imageRow+1);
					    imageCol++;

					    if(imageCol > 5){
					      // Reset Column
					      imageCol=0;
					      // Next Row
					      imageRow += 2;
					    }
					}
					
					ScrollPane sp = new ScrollPane();
					sp.setContent(imageGrid);
					sp.setPadding(new Insets(5, 0, 0, 5));
					
					VBox ab = new VBox(10);
					ab.getChildren().addAll(sp);


					Stage results = new Stage();
					Scene scene2 = new Scene(ab, 1000, 700);
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
    	retriever = new Retriever(new File("testdata/gallery"));
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


