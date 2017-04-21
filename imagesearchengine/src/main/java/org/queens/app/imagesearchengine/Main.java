package org.queens.app.imagesearchengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.queens.app.imagesearchengine.colourautocorrelogram.ColourAutoCorrelogram;
import org.queens.app.imagesearchengine.colourhistogram.ColourHistogram;
import org.queens.app.imagesearchengine.cooccurrencematrix.CooccurrenceMatrix;
import org.queens.app.imagesearchengine.edgehistogram.EdgeHistogram;

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

public class Main extends Application {

	QueryImage selectedQueryImage;
	File libraryDirectory = new File("testdata/library");
	List<LibraryImage> library;

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

				submitQuery(selectedQueryImage);

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

	public List<LibraryImage> loadLibrary() {
		List<LibraryImage> library = new ArrayList<LibraryImage>();

		File[] listing = libraryDirectory.listFiles();
		if (listing != null) {
			for (File file : listing) {
				try {
					library.add(new LibraryImage(ImageIO.read(file), file
							.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return library;
	}

	public double getColorDistance(QueryImage queryImage,
			LibraryImage libraryImage) {
		if (queryImage.getColourHistogram() == null) {
			queryImage.setColourHistogram(new ColourHistogram(queryImage
					.getImageData()));
			queryImage.getColourHistogram().extractFeature();
		}
		if (libraryImage.getColourHistogram() == null) {
			libraryImage.setColourHistogram(new ColourHistogram(libraryImage
					.getImageData()));
			libraryImage.getColourHistogram().extractFeature();
		}

		double colourHistogramDistance = ColourHistogram.calculateDistance(
				queryImage.getColourHistogram(),
				libraryImage.getColourHistogram());

		return colourHistogramDistance;
	}

	public double getShapeDistance(QueryImage queryImage,
			LibraryImage libraryImage) {
		if (queryImage.getEdgeHistogram() == null) {
			queryImage.setEdgeHistogram(new EdgeHistogram(queryImage
					.getImageData()));
			queryImage.getEdgeHistogram().extractFeature();
		}
		if (libraryImage.getEdgeHistogram() == null) {
			libraryImage.setEdgeHistogram(new EdgeHistogram(libraryImage
					.getImageData()));
			libraryImage.getEdgeHistogram().extractFeature();
		}

		double edgeHistogramDistance = EdgeHistogram.calculateDistance(
				queryImage.getEdgeHistogram(), libraryImage.getEdgeHistogram());

		return edgeHistogramDistance;
	}

	public double[] getTextureDistance(QueryImage queryImage,
			LibraryImage libraryImage) {
		if (queryImage.getCooccurrenceMatrix() == null) {
			queryImage.setCooccurrenceMatrix(new CooccurrenceMatrix(queryImage
					.getImageData()));
			queryImage.getCooccurrenceMatrix().extractFeature();
		}
		if (libraryImage.getCooccurrenceMatrix() == null) {
			libraryImage.setCooccurrenceMatrix(new CooccurrenceMatrix(
					libraryImage.getImageData()));
			libraryImage.getCooccurrenceMatrix().extractFeature();
		}

		double[] cooccurrenceMatrixDistance = CooccurrenceMatrix
				.calculateDistance(queryImage.getCooccurrenceMatrix(),
						libraryImage.getCooccurrenceMatrix());

		return cooccurrenceMatrixDistance;
	}

	public double getCorrelogramDistance(QueryImage queryImage,
			LibraryImage libraryImage) {
		if (queryImage.getColourCorrelogram() == null) {
			queryImage.setColourCorrelogram(new ColourAutoCorrelogram(queryImage
					.getImageData()));
			queryImage.getColourCorrelogram().extractFeature();
		}
		if (libraryImage.getColourCorrelogram() == null) {
			libraryImage.setColourCorrelogram(new ColourAutoCorrelogram(
					libraryImage.getImageData()));
			libraryImage.getColourCorrelogram().extractFeature();
		}

		double colourCorrelogramDistance = ColourAutoCorrelogram.calculateDistance(
				queryImage.getColourCorrelogram(),
				libraryImage.getColourCorrelogram());

		return colourCorrelogramDistance;
	}

	public void submitQuery(QueryImage selectedQueryImage) {
		if (library == null) {
			library = loadLibrary();
		}

		for (LibraryImage img : library) {
			img.setColorDistance(getColorDistance(selectedQueryImage, img));
//			img.setShapeDistance(getShapeDistance(selectedQueryImage, img));
//			img.setTextureVectorDistances(getTextureDistance(
//					selectedQueryImage, img));
			img.setColourCorrelogramDistance(getCorrelogramDistance(
					selectedQueryImage, img));
		}

		ColourHistogram.normaliseLibraryDistances(library);
//		EdgeHistogram.normaliseLibraryDistances(library);
//		CooccurrenceMatrix.normaliseLibraryDistances(library);
		ColourAutoCorrelogram.normaliseLibraryDistances(library);

		double finalDistance;
		for (LibraryImage img : library) {
			finalDistance = (img.getColourCorrelogramDistance()) / 1;
			img.setDistance(finalDistance);
		}

		Collections.sort(library);
	}

	public List<LibraryImage> getLibrary() {
		return library;
	}

}