package org.queens.app.imagesearchengine;

import java.awt.image.BufferedImage;

import org.queens.app.imagesearchengine.EnumCategory.Category;
import org.queens.app.imagesearchengine.colourautocorrelogram.ColourAutoCorrelogram;
import org.queens.app.imagesearchengine.colourhistogram.ColourHistogram;
import org.queens.app.imagesearchengine.cooccurrencematrix.CooccurrenceMatrix;
import org.queens.app.imagesearchengine.edgehistogram.EdgeHistogram;

public class GalleryImage implements Comparable<GalleryImage> {
	private BufferedImage imageData;
	
	private double finalDistance;
	
	private double colorHistogramDistance;
	private double edgeHistogramDistance;
	private double coOccurrenceMatrixDistance;
	private double colourCorrelogramDistance;
	
	private double[] coOccurrenceFeatureDistances;
	
	private ColourHistogram colourHistogram;
	private EdgeHistogram edgeHistogram;
	private CooccurrenceMatrix cooccurrenceMatrix;
	private ColourAutoCorrelogram colourCorrelogram;
	
	// Used for precision/recall test
	private Category category;
	public Category getCategory() {
		return category;
	}
	public GalleryImage(BufferedImage imageData, String fileName) {
		category = EnumCategory.getCategoryFromString(fileName);
		this.imageData = imageData;
	}
	// Used for precision/recall test
	

	public GalleryImage(BufferedImage imageData) {
		this.imageData = imageData;
	}

	public BufferedImage getImageData() {
		return imageData;
	}

	public void setImageData(BufferedImage imageData) {
		this.imageData = imageData;
	}
	
	public double[] getCoOccurrenceFeatureDistances() {
		return coOccurrenceFeatureDistances;
	}

	public void setTextureVectorDistances(double[] textureVectorDistances) {
		this.coOccurrenceFeatureDistances = textureVectorDistances;
	}

	public double getTextureDistance() {
		return coOccurrenceMatrixDistance;
	}

	public void setTextureDistance(double textureDistance) {
		this.coOccurrenceMatrixDistance = textureDistance;
	}


	public double getDistance() {
		return finalDistance;
	}

	public void setDistance(double distance) {
		this.finalDistance = distance;
	}

	public ColourHistogram getColourHistogram() {
		return colourHistogram;
	}

	public void setColourHistogram(ColourHistogram colourHistogram) {
		this.colourHistogram = colourHistogram;
	}

	public EdgeHistogram getEdgeHistogram() {
		return edgeHistogram;
	}

	public void setEdgeHistogram(EdgeHistogram edgeHistogram) {
		this.edgeHistogram = edgeHistogram;
	}

	public CooccurrenceMatrix getCooccurrenceMatrix() {
		return cooccurrenceMatrix;
	}

	public void setCooccurrenceMatrix(CooccurrenceMatrix cooccurrenceMatrix) {
		this.cooccurrenceMatrix = cooccurrenceMatrix;
	}

	public double getColorDistance() {
		return colorHistogramDistance;
	}

	public void setColorDistance(double colorDistance) {
		this.colorHistogramDistance = colorDistance;
	}

	public double getShapeDistance() {
		return edgeHistogramDistance;
	}

	public void setShapeDistance(double shapeDistance) {
		this.edgeHistogramDistance = shapeDistance;
	}
	
	public double getColourCorrelogramDistance() {
		return colourCorrelogramDistance;
	}
	public void setColourCorrelogramDistance(double correlogramDistance) {
		this.colourCorrelogramDistance = correlogramDistance;
	}
	public ColourAutoCorrelogram getColourCorrelogram() {
		return colourCorrelogram;
	}
	public void setColourCorrelogram(ColourAutoCorrelogram colourCorrelogram) {
		this.colourCorrelogram = colourCorrelogram;
	}
	@Override
	public int compareTo(GalleryImage o) {
		double compareDistance = ((GalleryImage)o).getDistance();
		
		return Double.compare(finalDistance, compareDistance);
	}
	
	

}
