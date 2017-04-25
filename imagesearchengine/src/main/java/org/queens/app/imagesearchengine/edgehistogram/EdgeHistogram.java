package org.queens.app.imagesearchengine.edgehistogram;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;

import org.queens.app.imagesearchengine.Feature;
import org.queens.app.imagesearchengine.LibraryImage;
import org.queens.app.imagesearchengine.utils.ImageUtils;

public class EdgeHistogram extends Feature {
	/*
	 * MPEG-7 standard uses 80 bins for EHD's. 5 edge types x 16 sub-images = 80
	 * bins
	 */
	private int[] bins = new int[80];

	// Threshold value for deciding between an
	// edge feature or NO_EDGE
	private double threshold;

	// Constants for edge types
//	private static final int VERTICAL_EDGE = 0;
//	private static final int HORIZONTAL_EDGE = 1;
//	private static final int DEGREE45_EDGE = 2;
//	private static final int DEGREE135_EDGE = 3;
//	private static final int NON_DIRECTIONAL_EDGE = 4;
	private static final int NO_EDGE = 5;

	// Number of pixels on one axis
	private int blockSize;
	private int subImageSize = 128;

	// Image needs to be converted to grayscale
	// before we start working on it
	private WritableRaster imageRaster;
	
	public EdgeHistogram(BufferedImage image) {
		// Copy image into WritableRaster because
		// we don't want to modify the original
		// image.
		imageRaster = image.copyData(null);
		ImageUtils.makeGray(imageRaster);
		threshold = 11;
		blockSize = 16;
	}
	
	public EdgeHistogram(BufferedImage image, int threshold, int blockSize) {
		// Copy image into WritableRaster because
		// we don't want to modify the original
		// image.
		imageRaster = image.copyData(null);
		ImageUtils.makeGray(imageRaster);
		this.threshold = threshold;
		this.blockSize = blockSize;
	}

	public int[] getBins() {
		return bins;
	}

	public void extractFeature() {
		int[] localHistogram;
		int subImageIndex = 0;

		for (int y = 0; y <= 512 - subImageSize; y += subImageSize) {
			for (int x = 0; x <= 512 - subImageSize; x += subImageSize) {
				localHistogram = calculateLocalHistogram(x, y);
				for (int i = 0; i != localHistogram.length; i++) {
					bins[i + (subImageIndex * 5)] = localHistogram[i];
				}
				subImageIndex++;
			}
		}
	}

	int[] calculateLocalHistogram(int subImageX, int subImageY) {
		int[] localHistogram = new int[5];
		int edgeFeature;

		for (int x = subImageX; x <= (subImageX + subImageSize - blockSize); x += blockSize) {
			for (int y = subImageY; y <= (subImageY + subImageSize - blockSize); y += blockSize) {
				edgeFeature = getEdgeFeature(x, y);
				if (edgeFeature != NO_EDGE)
					localHistogram[edgeFeature]++;
			}
		}

		return localHistogram;
	}

	/*
	 * Takes an image block and gets its edge feature
	 */
	int getEdgeFeature(int i, int j) {
		double subBlockAvgs[] = getSubBlocksAvgs(i, j);

		// Filter coefficients for edge detection
		double vertical_filter[] = { 1.0, -1.0, 1.0, -1.0 };
		double horizontal_filter[] = { 1.0, 1.0, -1.0, -1.0 };
		double degree45_filter[] = { Math.sqrt(2), 0.0, 0.0, -Math.sqrt(2) };
		double degree135_filter[] = { 0.0, Math.sqrt(2), -Math.sqrt(2), 0.0 };
		double nonDirectional_filter[] = { 2.0, -2.0, -2.0, 2.0 };

		double magnitudes[] = new double[5];

		// Get the magnitudes for all 5 edge types
		for (int k = 0; k != 4; k++) {
			magnitudes[0] += subBlockAvgs[k] * vertical_filter[k];
		}
		magnitudes[0] = Math.abs(magnitudes[0]);

		for (int k = 0; k != 4; k++) {
			magnitudes[1] += subBlockAvgs[k] * horizontal_filter[k];
		}
		magnitudes[1] = Math.abs(magnitudes[1]);

		for (int k = 0; k != 4; k++) {
			magnitudes[2] += subBlockAvgs[k] * degree45_filter[k];
		}
		magnitudes[2] = Math.abs(magnitudes[2]);

		for (int k = 0; k != 4; k++) {
			magnitudes[3] += subBlockAvgs[k] * degree135_filter[k];
		}
		magnitudes[3] = Math.abs(magnitudes[3]);

		for (int k = 0; k != 4; k++) {
			magnitudes[4] += subBlockAvgs[k] * nonDirectional_filter[k];
		}
		magnitudes[4] = Math.abs(magnitudes[4]);

		// Find the maximum magnitude and store its index
		int edgeFeature = 0;
		for (int currIndex = 1; currIndex != 5; currIndex++) {
			double magnitude = magnitudes[currIndex];
			if (magnitude > magnitudes[edgeFeature]) {
				edgeFeature = currIndex;
			}
		}

		// If the max value is below the threshold,
		// there is no edge
		if (magnitudes[edgeFeature] < threshold) {
			edgeFeature = NO_EDGE;
		}

		return edgeFeature;
	}

	/*
	 * Computes the average gray level for the 4 sub-blocks in an image block
	 * 0,1 
	 * 2,3
	 * 
	 * Takes 2 coordinates (i,j) for the top left of the block
	 */
	double[] getSubBlocksAvgs(int i, int j) {
		double[] avgs = new double[4];
		// Amount of pixels on one axis
		int sizeOfSubBlock = blockSize / 2;
		// TODO try ints for performance
		double[] pixelData = new double[3];

		int xOffset = 0;
		int yOffset = 0;

		// SubBlock 0
		for (int x = 0; x != sizeOfSubBlock; x++) {
			for (int y = 0; y != sizeOfSubBlock; y++) {
				avgs[0] += imageRaster.getPixel(i + x + xOffset, j + y
						+ yOffset, pixelData)[0];
			}
		}
		avgs[0] /= sizeOfSubBlock * sizeOfSubBlock;

		xOffset += sizeOfSubBlock;

		// SubBlock 1
		for (int x = 0; x != sizeOfSubBlock; x++) {
			for (int y = 0; y != sizeOfSubBlock; y++) {
				avgs[1] += imageRaster.getPixel(i + x + xOffset, j + y
						+ yOffset, pixelData)[0];
			}
		}
		avgs[1] /= sizeOfSubBlock * sizeOfSubBlock;

		xOffset = 0;
		yOffset += sizeOfSubBlock;

		// SubBlock 2
		for (int x = 0; x != sizeOfSubBlock; x++) {
			for (int y = 0; y != sizeOfSubBlock; y++) {
				avgs[2] += imageRaster.getPixel(i + x + xOffset, j + y
						+ yOffset, pixelData)[0];
			}
		}
		avgs[2] /= sizeOfSubBlock * sizeOfSubBlock;

		xOffset += sizeOfSubBlock;

		// SubBlock 3
		for (int x = 0; x != sizeOfSubBlock; x++) {
			for (int y = 0; y != sizeOfSubBlock; y++) {
				avgs[3] += imageRaster.getPixel(i + x + xOffset, j + y
						+ yOffset, pixelData)[0];
			}
		}
		avgs[3] /= sizeOfSubBlock * sizeOfSubBlock;

		return avgs;
	}
	
	public static double calculateDistance(EdgeHistogram e1, EdgeHistogram e2) {
		// TODO decide whether to use global distance or not. Experiment with it
		double localEdgeDistance = 0;
		double globalEdgeDistance = 0;
		double edgeDistance = 0;
		
		// Calculate local histogram distances using Manhattan distance
		for (int i = 0; i != 80; i++) {
			localEdgeDistance += Math.abs(e1.getBins()[i]
					- e2.getBins()[i]);
		}
		
		int[] e1GlobalEdge = new int[5];
		int[] e2GlobalEdge = new int[5];
		
		for (int j = 0; j != 80; j++) {
			e1GlobalEdge[j % 5] += e1.getBins()[j];
		}
		for (int j = 0; j != 80; j++) {
			e2GlobalEdge[j % 5] += e2.getBins()[j];
		}
		
		for (int i = 0; i != 5; i++) {
			globalEdgeDistance += Math.abs(e1.getBins()[i] - e2.getBins()[i]);
		}
		
		edgeDistance = localEdgeDistance;// + (5d*globalEdgeDistance);
		return edgeDistance;
	}
	
	public static void normaliseLibraryDistances(List<LibraryImage> library) {
		double max, min, dist;

		max = library.get(0).getShapeDistance();
		min = library.get(0).getShapeDistance();
		dist = 0;
		for (int i = 1; i != library.size(); i++) {
			dist = library.get(i).getShapeDistance();
			if (dist > max) {
				max = dist;
			}
			if (dist < min) {
				min = dist;
			}
		}
		for (LibraryImage img : library) {
			img.setShapeDistance((img.getShapeDistance() - min)/(max - min));
		}
	}

}
