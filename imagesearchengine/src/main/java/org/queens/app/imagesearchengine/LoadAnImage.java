package org.queens.app.imagesearchengine;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

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

public class LoadAnImage extends Application {

	BufferedImage queryImage;
	File libraryDirectory;

	@Override
	public void start(Stage stage) throws Exception {
		libraryDirectory = new File("testdata/library");

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
				5, 58));
		choiceBox.getSelectionModel().selectFirst();
		grid.add(choiceBox, 3, 1);

		loadImageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				fileChooser.setTitle("Select query image");
				fileChooser.setInitialDirectory(new File("testdata/query_images"));
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					imgView.setImage(new Image(file.toURI().toString()));
					try {
						queryImage = ImageIO.read(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				List<LibraryImage> library = loadLibrary(libraryDirectory);
				for (LibraryImage img : library) {
					img.setDistance(calculateDistance(queryImage, img));
				}

				Collections.sort(library);

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
					data.add(library.get(i));
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
		});

		Scene scene = new Scene(grid, 500, 350);
		stage.setTitle("Load An Image");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public List<LibraryImage> loadLibrary(File libraryDirectory) {
		List<LibraryImage> library = new ArrayList<LibraryImage>();

		File[] listing = libraryDirectory.listFiles();
		if (listing != null) {
			for (File file : listing) {
				try {
					library.add(new LibraryImage(ImageIO.read(file)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return library;
	}

	public double calculateDistanceOld(BufferedImage query,
			LibraryImage libraryImage) {
		Raster queryRas = query.getData();
		Raster libraryRas = libraryImage.getImageData().getData();

		double[] i = new double[3];
		double[] j = new double[3];

		double distance = 0;

		for (int x = 0; x != queryRas.getWidth(); x++) {
			for (int y = 0; y != queryRas.getHeight(); y++) {
				i = queryRas.getPixel(x, y, i);
				j = libraryRas.getPixel(x, y, j);
				distance += Math.pow(i[0] - j[0], 2);
			}
		}

		distance = Math.sqrt(distance);

		return distance;
	}

	public double calculateDistance(BufferedImage query,
			LibraryImage libraryImage) {
		Raster queryRas = query.getData();
		Raster libraryRas = libraryImage.getImageData().getData();

		int[][] queryHist = generateImageHistogram(queryRas);
		int[][] libraryHist = generateImageHistogram(libraryRas);

		double distance = 0;

		for (int i = 0; i != 3; i++)
			for (int j = 0; j != 4; j++)
				distance += Math.pow(queryHist[i][j] - libraryHist[i][j], 2);
		distance = Math.sqrt(distance);

		return distance;
	}

	public int[][] generateImageHistogram(Raster image) {
		// Multi-dimensional array representing a matrix for
		// colours in an image of different intensities
		// x: red, green, blue
		// y: 0-63, 64-127, 128-191, 192-255
		int[][] colourHistogram = new int[3][4];
		int[] pixelValues = new int[3];
		int index = 0;

		for (int x = 0; x != image.getWidth(); x++) {
			for (int y = 0; y != image.getHeight(); y++) {
				pixelValues = image.getPixel(x, y, pixelValues);
				index = 0;
				for (int i : pixelValues) {
					if (i >= 192)
						colourHistogram[index][3]++;
					else if (i >= 128)
						colourHistogram[index][2]++;
					else if (i >= 64)
						colourHistogram[index][1]++;
					else
						colourHistogram[index][0]++;
					index++;
				}
			}
		}
		return colourHistogram;
	}

}
