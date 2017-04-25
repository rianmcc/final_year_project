package org.queens.app.imagesearchengine.precisionrecalltests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.EnumCategory;
import org.queens.app.imagesearchengine.EnumCategory.Category;
import org.queens.app.imagesearchengine.QueryImage;
import org.queens.app.imagesearchengine.Retriever;

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
		LOGGER.setLevel(Level.OFF);

		File testdataDirectory = new File("testdata/query_images");
		File[] query_images = testdataDirectory.listFiles();

		Retriever tester = new Retriever(new File("testdata/library"));
		Category queryCategory = null;

		int numOfQueryImages = query_images.length;
		
		int numOfRelevantImages = 48;
		int[] numReturns = new int[48];
		for (int i = 0; i < 48; i++) {
			numReturns[i] = (i+1)*4;
		}

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

			QueryImage queryImage = null;
			try {
				queryImage = new QueryImage(ImageIO.read(queryImageFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			tester.submitQuery(queryImage);

			int returnIndex = 0;
			for (int returnNumber : numReturns) {
				LOGGER.fine("Precision/Recall for " + returnNumber + " returns");
				relevantImagesReturned = 0;
				for (int i = 0; i != returnNumber; i++) {
					if (tester.getLibrary().get(i).getCategory() == queryCategory) {
						LOGGER.finer("Position: " + i + ". Category: "
								+ tester.getLibrary().get(i).getCategory() + ". Distance: "
								+ tester.getLibrary().get(i).getDistance() + ". Matches!");
						relevantImagesReturned++;
					} else {
						LOGGER.finer("Position: " + i + ". Category: "
								+ tester.getLibrary().get(i).getCategory() + ". Distance: "
								+ tester.getLibrary().get(i).getDistance()
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
				recallAverages[i] = recallAverages[i] / numOfQueryImages;
				fileWriter.append(Double.toString(recallAverages[i]));
				fileWriter.append(",");
				fileWriter.append(Double.toString(precisionAverages[i]));
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
