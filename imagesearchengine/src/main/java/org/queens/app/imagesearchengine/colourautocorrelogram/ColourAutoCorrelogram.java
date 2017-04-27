package org.queens.app.imagesearchengine.colourautocorrelogram;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.List;

import org.queens.app.imagesearchengine.Feature;
import org.queens.app.imagesearchengine.GalleryImage;

public class ColourAutoCorrelogram extends Feature {

	Raster imageRaster;
	double[][] autoCorrelogram;
	int[] distanceSet;

	float binsPerColour = 4;
	float redValues = 256f / 4;
	float greenValues = 256f / 4;
	float blueValues = 256f / 4;
	
	// Constructor with default distanceSet
	public ColourAutoCorrelogram(BufferedImage image) {
		imageRaster = image.getData();
		distanceSet = new int[]{1,3,5,7};
	}

	// Constructor with custom distanceSet taken as parameter
	public ColourAutoCorrelogram(BufferedImage image, int[] distanceSet) {
		imageRaster = image.getData();
		this.distanceSet = distanceSet;
	}

	/*
	 * Takes a RGB pixel and it assigns it a bin based on quantisation values.
	 * 
	 * @return An int referring to the pixels bin
	 */
	private int getPixelBin(int[] pixel) {
		return (int) ((int) (pixel[0] / redValues) * (binsPerColour)
				* (binsPerColour) + (int) (pixel[1] / greenValues)
				* (binsPerColour) + (int) (pixel[2] / blueValues));
	}

	@Override
	public void extractFeature() {

		// The number of bins the auto-correlogram will use for colours
		int numOfBins = 64;

		// Quantise the image into the specified amount of bins
		int[][] quantizedImage = new int[imageRaster.getWidth()][imageRaster
				.getHeight()];
		int[] pixel = new int[3];
		for (int i = 0; i != imageRaster.getWidth(); i++) {
			for (int j = 0; j != imageRaster.getHeight(); j++) {
				pixel = imageRaster.getPixel(i, j, pixel);
				quantizedImage[i][j] = getPixelBin(pixel);
			}
		}

		// Builds a histogram based on the 64 quantised values which is used for
		// normalisation
		int[] histogram = new int[numOfBins];
		for (int x = 0; x < imageRaster.getWidth(); x++)
			for (int y = 0; y < imageRaster.getHeight(); y++) {
				histogram[quantizedImage[x][y]]++;
			}

		autoCorrelogram = new double[numOfBins][distanceSet.length];

		int imageWidth = imageRaster.getWidth();
		int imageHeight = imageRaster.getHeight();

		// Chebyshev distance
		for (int x = 0; x != imageWidth; x++) {
			for (int y = 0; y != imageHeight; y++) {
				int pColour = quantizedImage[x][y];
				for (int distIndex = 0; distIndex != distanceSet.length; distIndex++) {
					int dist = distanceSet[distIndex];

					// move horizontally across pixel grid on top and bottom
					for (int localX = -dist; localX <= dist; localX++) {
						// Top
						int globalX = x + localX;
						int globalY = y - dist;
						// Bounds checking
						if (globalX >= 0 && globalX < imageWidth
								&& globalY >= 0 && globalY < imageHeight) {
							// If the colours are the same, update the
							// autocorrelogram
							if (quantizedImage[globalX][globalY] == pColour) {
								autoCorrelogram[pColour][distIndex]++;
							}
						}
							// Bottom
							globalY = y + dist;
							// Bounds checking
							if (globalX >= 0 && globalX < imageWidth
									&& globalY >= 0 && globalY < imageHeight) {
								// If the colours are the same, update the
								// autocorrelogram
								if (quantizedImage[globalX][globalY] == pColour) {
									autoCorrelogram[pColour][distIndex]++;
								}
							}

						

					}
					// move vertically through pixel grid on left and right
					for (int localY = -dist + 1; localY <= dist - 1; localY++) {
						// Left
						int globalX = x - dist;
						int globalY = y + localY;
						// Bounds checking
						if (globalX >= 0 && globalX < imageWidth
								&& globalY >= 0 && globalY < imageHeight) {
							// If the colours are the same, update the
							// autocorrelogram
							if (quantizedImage[globalX][globalY] == pColour) {
								autoCorrelogram[pColour][distIndex]++;
							}
						}
						// Right
						globalX = x + dist;
						// Bounds checking
						if (globalX >= 0 && globalX < imageWidth
								&& globalY >= 0 && globalY < imageHeight) {
							// If the colours are the same, update the
							// autocorrelogram
							if (quantizedImage[globalX][globalY] == pColour) {
								autoCorrelogram[pColour][distIndex]++;
							}
						}

					}
				}
			}
		}
		
		// normalize the feature vector
		for (int c = 0; c != numOfBins; c++) {
			for (int d = 0; d != distanceSet.length; d++)
				if (histogram[c] > 0)
					autoCorrelogram[c][d] = (float) Math
							.floor(16d * (autoCorrelogram[c][d] / (((float) histogram[c]) * 8.0f * distanceSet[d])));
		}
	}

	/*
	 * l1 metric
	 */
	public static double calculateDistance(ColourAutoCorrelogram c1,
			ColourAutoCorrelogram c2) {
		double distance = 0;

		for (int i = 0; i < c1.getCorrelogram().length; i++) {
			double[] ints = c1.getCorrelogram()[i];
			for (int j = 0; j < ints.length; j++) {
				distance += Math.abs(c1.getCorrelogram()[i][j]
						- c2.getCorrelogram()[i][j]);
			}
		}
		return distance;
	}

	/*
	 * Weighted measure from Image Indexing Using Color Correlograms pp. 4
	 */
	public static double calculateDistanceOld(ColourAutoCorrelogram c1,
			ColourAutoCorrelogram c2) {
		double distance = 0;

		for (int i = 0; i != c1.getCorrelogram().length; i++) {
			for (int j = 0; j != c1.getCorrelogram()[0].length; j++) {
				distance += Math.abs(c1.getCorrelogram()[i][j]
						- c2.getCorrelogram()[i][j])
						/ (1 + c1.getCorrelogram()[i][j] + c2.getCorrelogram()[i][j]);
			}
		}
		return distance;
	}

	public static void normaliseGalleryDistances(List<GalleryImage> gallery) {
		double max, min, dist;

		max = gallery.get(0).getColourCorrelogramDistance();
		min = gallery.get(0).getColourCorrelogramDistance();
		dist = 0;
		for (int i = 1; i != gallery.size(); i++) {
			dist = gallery.get(i).getColourCorrelogramDistance();
			if (dist > max) {
				max = dist;
			}
			if (dist < min) {
				min = dist;
			}
		}
		for (GalleryImage img : gallery) {
			img.setColourCorrelogramDistance((img
					.getColourCorrelogramDistance() - min) / (max - min));
		}
	}

	public double[][] getCorrelogram() {
		return autoCorrelogram;
	}

}
