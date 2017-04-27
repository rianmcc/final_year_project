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

	List<GalleryImage> gallery;

	public Retriever(File galleryDirectory) {
		gallery = loadGallery(galleryDirectory);
	}

	public List<GalleryImage> getGallery() {
		return gallery;
	}

	/*
	 * Loads the images at galleryDirectory into the gallery ArrayList
	 */
	public List<GalleryImage> loadGallery(File galleryDirectory) {
		List<GalleryImage> gallery = new ArrayList<GalleryImage>();

		File[] listing = galleryDirectory.listFiles();
		if (listing != null) {
			for (File file : listing) {
				try {
					gallery.add(new GalleryImage(ImageIO.read(file), file
							.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return gallery;
	}

	/*
	 * Submit a QueryImage for comparison to the gallery images. All distances
	 * are calculated and then the gallery is sorted in descending order of
	 * distance.
	 */
	public void submitQuery(QueryImage selectedQueryImage) {

		for (GalleryImage img : gallery) {
			img.setColorDistance(getColorDistance(selectedQueryImage, img));
			img.setShapeDistance(getShapeDistance(selectedQueryImage, img));
			img.setTextureVectorDistances(getTextureDistance(
					selectedQueryImage, img));
			img.setColourCorrelogramDistance(getCorrelogramDistance(
					selectedQueryImage, img));
		}

		ColourHistogram.normaliseGalleryDistances(gallery);
		EdgeHistogram.normaliseGalleryDistances(gallery);
		CooccurrenceMatrix.normaliseGalleryDistances(gallery);
		ColourAutoCorrelogram.normaliseGalleryDistances(gallery);
		
		double finalDistance;
		double colourHistW = 0d;
		double shapeW = 0d;
		double textureW = 1d;
		double correlogramW = 0d;
		for (GalleryImage img : gallery) {
			finalDistance = 0;
			
			finalDistance += colourHistW * img.getColorDistance();
			finalDistance += shapeW * img.getShapeDistance();
			finalDistance += textureW * img.getTextureDistance();
			finalDistance += correlogramW * img.getColourCorrelogramDistance();
			finalDistance /= colourHistW + shapeW + textureW + correlogramW;
			
			img.setDistance(finalDistance);
		}

		Collections.sort(gallery);
	}

	/*
	 * Takes the gallery and computes the auto-correlograms for each image
	 */
	public void indexColourCorrelograms() {
		for (GalleryImage img : gallery) {
			img.setColourCorrelogram(new ColourAutoCorrelogram(img
					.getImageData()));
			img.getColourCorrelogram().extractFeature();
		}
	}

	public void indexColourHistograms() {
		for (GalleryImage img : gallery) {
			img.setColourHistogram(new ColourHistogram(img.getImageData()));
			img.getColourHistogram().extractFeature();
		}
	}

	public void indexEdgeHistograms() {
		for (GalleryImage img : gallery) {
			img.setEdgeHistogram(new EdgeHistogram(img.getImageData()));
			img.getEdgeHistogram().extractFeature();
		}
	}

	public void indexCooccurrenceMatrices() {
		for (GalleryImage img : gallery) {
			img.setCooccurrenceMatrix(new CooccurrenceMatrix(img.getImageData()));
			img.getCooccurrenceMatrix().extractFeature();
		}
	}

	public double getColorDistance(QueryImage queryImage,
			GalleryImage galleryImage) {
		if (queryImage.getColourHistogram() == null) {
			queryImage.setColourHistogram(new ColourHistogram(queryImage
					.getImageData()));
			queryImage.getColourHistogram().extractFeature();
		}
		if (galleryImage.getColourHistogram() == null) {
			galleryImage.setColourHistogram(new ColourHistogram(galleryImage
					.getImageData()));
			galleryImage.getColourHistogram().extractFeature();
		}

		double colourHistogramDistance = ColourHistogram.calculateDistance(
				queryImage.getColourHistogram(),
				galleryImage.getColourHistogram());

		return colourHistogramDistance;
	}

	public double getShapeDistance(QueryImage queryImage,
			GalleryImage galleryImage) {
		if (queryImage.getEdgeHistogram() == null) {
			queryImage.setEdgeHistogram(new EdgeHistogram(queryImage
					.getImageData()));
			queryImage.getEdgeHistogram().extractFeature();
		}
		if (galleryImage.getEdgeHistogram() == null) {
			galleryImage.setEdgeHistogram(new EdgeHistogram(galleryImage
					.getImageData()));
			galleryImage.getEdgeHistogram().extractFeature();
		}

		double edgeHistogramDistance = EdgeHistogram.calculateDistance(
				queryImage.getEdgeHistogram(), galleryImage.getEdgeHistogram());

		return edgeHistogramDistance;
	}

	public double[] getTextureDistance(QueryImage queryImage,
			GalleryImage galleryImage) {
		if (queryImage.getCooccurrenceMatrix() == null) {
			queryImage.setCooccurrenceMatrix(new CooccurrenceMatrix(queryImage
					.getImageData()));
			queryImage.getCooccurrenceMatrix().extractFeature();
		}
		if (galleryImage.getCooccurrenceMatrix() == null) {
			galleryImage.setCooccurrenceMatrix(new CooccurrenceMatrix(
					galleryImage.getImageData()));
			galleryImage.getCooccurrenceMatrix().extractFeature();
		}

		double[] cooccurrenceMatrixDistance = CooccurrenceMatrix
				.calculateDistance(queryImage.getCooccurrenceMatrix(),
						galleryImage.getCooccurrenceMatrix());

		return cooccurrenceMatrixDistance;
	}

	public double getCorrelogramDistance(QueryImage queryImage,
			GalleryImage galleryImage) {
		if (queryImage.getColourCorrelogram() == null) {
			queryImage.setColourCorrelogram(new ColourAutoCorrelogram(
					queryImage.getImageData()));
			queryImage.getColourCorrelogram().extractFeature();
		}
		if (galleryImage.getColourCorrelogram() == null) {
			galleryImage.setColourCorrelogram(new ColourAutoCorrelogram(
					galleryImage.getImageData()));
			galleryImage.getColourCorrelogram().extractFeature();
		}

		double colourCorrelogramDistance = ColourAutoCorrelogram
				.calculateDistance(queryImage.getColourCorrelogram(),
						galleryImage.getColourCorrelogram());

		return colourCorrelogramDistance;
	}

}
