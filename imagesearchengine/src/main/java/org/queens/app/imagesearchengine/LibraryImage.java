package org.queens.app.imagesearchengine;

import java.awt.image.BufferedImage;

import org.queens.app.imagesearchengine.colourhistogram.ColourHistogram;
import org.queens.app.imagesearchengine.cooccurrencematrix.CooccurrenceMatrix;
import org.queens.app.imagesearchengine.edgehistogram.EdgeHistogram;

public class LibraryImage implements Comparable<LibraryImage> {
	private BufferedImage imageData;
	private double distance;
	private ColourHistogram colourHistogram;
	private EdgeHistogram edgeHistogram;
	private CooccurrenceMatrix cooccurrenceMatrix;
	
	public LibraryImage(BufferedImage imageData) {
		this.imageData = imageData;
	}

	public BufferedImage getImageData() {
		return imageData;
	}

	public void setImageData(BufferedImage imageData) {
		this.imageData = imageData;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
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

	@Override
	public int compareTo(LibraryImage o) {
		double compareDistance = ((LibraryImage)o).getDistance();
		
		return Double.compare(distance, compareDistance);
	}
	
	

}
