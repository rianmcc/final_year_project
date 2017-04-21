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

public class Retriever {

	List<LibraryImage> library;

	public Retriever(File libraryDirectory) {
		library = loadLibrary(libraryDirectory);
	}

	public List<LibraryImage> getLibrary() {
		return library;
	}

	/*
	 * Loads the images at libraryDirectory into the library ArrayList
	 */
	public List<LibraryImage> loadLibrary(File libraryDirectory) {
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

	/*
	 * Submit a QueryImage for comparison to the library images. All distances
	 * are calculated and then the library is sorted in descending order of
	 * distance.
	 */
	public void submitQuery(QueryImage selectedQueryImage) {

		for (LibraryImage img : library) {
			img.setColorDistance(getColorDistance(selectedQueryImage, img));
//			img.setShapeDistance(getShapeDistance(selectedQueryImage, img));
//			img.setTextureVectorDistances(getTextureDistance(
//					selectedQueryImage, img));
//			img.setColourCorrelogramDistance(getCorrelogramDistance(
//					selectedQueryImage, img));
		}

		ColourHistogram.normaliseLibraryDistances(library);
//		EdgeHistogram.normaliseLibraryDistances(library);
//		CooccurrenceMatrix.normaliseLibraryDistances(library);
//		ColourAutoCorrelogram.normaliseLibraryDistances(library);

		double finalDistance;
		for (LibraryImage img : library) {
			finalDistance = (img.getColorDistance()) / 1;
			img.setDistance(finalDistance);
		}

		Collections.sort(library);
	}

	/*
	 * Takes the library and computes the auto-correlograms for each image
	 */
	public void indexColourCorrelograms() {
		for (LibraryImage img : library) {
			img.setColourCorrelogram(new ColourAutoCorrelogram(img.getImageData()));
			img.getColourCorrelogram().extractFeature();
		}
	}
	public void indexColourHistograms() {
		for (LibraryImage img : library) {
			img.setColourHistogram(new ColourHistogram(img.getImageData()));
			img.getColourHistogram().extractFeature();
		}
	}
	public void indexEdgeHistograms() {
		for (LibraryImage img : library) {
			img.setEdgeHistogram(new EdgeHistogram(img.getImageData()));
			img.getEdgeHistogram().extractFeature();
		}
	}
	public void indexCooccurrenceMatrices() {
		for (LibraryImage img : library) {
			img.setCooccurrenceMatrix(new CooccurrenceMatrix(img.getImageData()));
			img.getCooccurrenceMatrix().extractFeature();
		}
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
			queryImage.setColourCorrelogram(new ColourAutoCorrelogram(
					queryImage.getImageData()));
			queryImage.getColourCorrelogram().extractFeature();
		}
		if (libraryImage.getColourCorrelogram() == null) {
			libraryImage.setColourCorrelogram(new ColourAutoCorrelogram(
					libraryImage.getImageData()));
			libraryImage.getColourCorrelogram().extractFeature();
		}

		double colourCorrelogramDistance = ColourAutoCorrelogram
				.calculateDistance(queryImage.getColourCorrelogram(),
						libraryImage.getColourCorrelogram());

		return colourCorrelogramDistance;
	}

}
