package org.queens.app.imagesearchengine;

import java.awt.image.BufferedImage;

import org.queens.app.imagesearchengine.colourautocorrelogram.ColourAutoCorrelogram;
import org.queens.app.imagesearchengine.colourhistogram.ColourHistogram;
import org.queens.app.imagesearchengine.cooccurrencematrix.CooccurrenceMatrix;
import org.queens.app.imagesearchengine.edgehistogram.EdgeHistogram;

public class QueryImage {
	private BufferedImage imageData;
	private ColourHistogram colourHistogram;
	private EdgeHistogram edgeHistogram;
	private CooccurrenceMatrix cooccurrenceMatrix;
	private ColourAutoCorrelogram colourCorrelogram;
	
	public QueryImage(BufferedImage imageData) {
		this.imageData = imageData;
	}

	public BufferedImage getImageData() {
		return imageData;
	}

	public void setImageData(BufferedImage imageData) {
		this.imageData = imageData;
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

	public ColourAutoCorrelogram getColourCorrelogram() {
		return colourCorrelogram;
	}

	public void setColourCorrelogram(ColourAutoCorrelogram colourCorrelogram) {
		this.colourCorrelogram = colourCorrelogram;
	}
}
