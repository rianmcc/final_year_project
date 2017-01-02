package org.queens.app.imagesearchengine.precisionrecalltests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;

import org.junit.Test;

import org.queens.app.imagesearchengine.LoadAnImage;
import org.queens.app.imagesearchengine.precisionrecalltests.EnumCategory.Category;

public class PrecisionRecallTest {

	private static final Logger LOGGER = Logger
			.getLogger(PrecisionRecallTest.class.getName());
	private static FileHandler fh = null;

	@Test
	public void precisionRecallTest() {
		try {
			fh = new FileHandler("precisionRecall.log", false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		fh.setFormatter(new SimpleFormatter());
		LOGGER.addHandler(fh);
		LOGGER.setLevel(Level.FINE);

		File testdataDirectory = new File("testdata/query_images");
		File[] query_images = testdataDirectory.listFiles();

		LoadAnImage tester = new LoadAnImage();
		List<LibraryImageTester> library = loadLibrary(new File(
				"testdata/library"));
		Category queryCategory = null;

		int numOfQueryImages = query_images.length;
		
		int numOfRelevantImages = 48;
		int[] numReturns = new int[192];
		for (int i = 0; i != 192; i++)
			numReturns[i] = i+1;

		int relevantImagesReturned;
		double precision;
		double recall;
		double[] precisionAverages = new double[numReturns.length];
		double[] recallAverages = new double[numReturns.length];

		for (File queryImageFile : query_images) {
			LOGGER.fine("Processing file: " + queryImageFile.getName());
			queryCategory = EnumCategory.getCategoryFromString(queryImageFile
					.getName());
			LOGGER.finer("File category is: " + queryCategory);

			BufferedImage queryImage = null;
			try {
				queryImage = ImageIO.read(queryImageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (LibraryImageTester img : library) {
				img.setDistance(tester.calculateDistance(queryImage, img));
			}

			Collections.sort(library);

			int returnIndex = 0;
			for (int returnNumber : numReturns) {
				LOGGER.fine("Precision/Recall for " + returnNumber + " returns");
				relevantImagesReturned = 0;
				for (int i = 0; i != returnNumber; i++) {
					if (library.get(i).getCategory() == queryCategory) {
						LOGGER.finer("Position: " + i + ". Category: "
								+ library.get(i).getCategory() + ". Distance: "
								+ library.get(i).getDistance() + ". Matches!");
						relevantImagesReturned++;
					} else {
						LOGGER.finer("Position: " + i + ". Category: "
								+ library.get(i).getCategory() + ". Distance: "
								+ library.get(i).getDistance()
								+ ". Doesn't Match!");
					}
				}
				precision = calculatePrecision(relevantImagesReturned,
						returnNumber);
				recall = calculateRecall(relevantImagesReturned,
						numOfRelevantImages);
				LOGGER.fine("Precision: " + precision + "\tRecall: " + recall);
				precisionAverages[returnIndex] += precision;
				recallAverages[returnIndex] += recall;
				returnIndex++;
			}

		}

		try {
			FileWriter fileWriter = new FileWriter("precisionRecallResults.csv");

			// Divide by number of queries to get average precision and recall
			for (int i = 0; i != numReturns.length; i++) {
				precisionAverages[i] = precisionAverages[i] / numOfQueryImages;
				fileWriter.append(Double.toString(precisionAverages[i]));
				recallAverages[i] = recallAverages[i] / numOfQueryImages;
				fileWriter.append(",");
				fileWriter.append(Double.toString(recallAverages[i]));
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<LibraryImageTester> loadLibrary(File libraryDirectory) {
		List<LibraryImageTester> library = new ArrayList<LibraryImageTester>();

		File[] listing = libraryDirectory.listFiles();
		if (listing != null) {
			for (File file : listing) {
				try {
					library.add(new LibraryImageTester(ImageIO.read(file), file
							.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return library;
	}

	private double calculatePrecision(int relevantImagesReturned,
			int returnedImages) {
		return (double) relevantImagesReturned / returnedImages;
	}

	private double calculateRecall(int relevantImagesReturned,
			int relevantImages) {
		return (double) relevantImagesReturned / relevantImages;
	}

}
