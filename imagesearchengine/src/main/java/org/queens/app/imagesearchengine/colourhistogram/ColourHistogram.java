package org.queens.app.imagesearchengine.colourhistogram;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.List;

import org.queens.app.imagesearchengine.Feature;
import org.queens.app.imagesearchengine.GalleryImage;
import org.queens.app.imagesearchengine.utils.ImageUtils;

public class ColourHistogram extends Feature {
	// Array size will be the number of bins in histogram
	private int[][][] histogram;
	
	private Raster imageRaster;

	public ColourHistogram(BufferedImage image) {
		imageRaster = image.getData();
	}

	// KEEP THIS FOR EXPERIMENTAL COMPARISON
	public void extractFeatureOld() {
		int numOfBins = 4;
		// Multi-dimensional array representing a matrix for
		// colours in an image of different intensities
		histogram = new int[numOfBins][numOfBins][numOfBins];
		int[] pixelValues = new int[3];
		int binSize = 256 / numOfBins;

		for (int x = 0; x != imageRaster.getWidth(); x++) {
			for (int y = 0; y != imageRaster.getHeight(); y++) {
				pixelValues = imageRaster.getPixel(x, y, pixelValues);
				histogram[pixelValues[0]/binSize][pixelValues[1]/binSize][pixelValues[2]/binSize]++;
			}
		}
	}
	
	public void extractFeature() {
		int numOfHueBins = 8;
		int numOfSatBins = 3;
		int numOfValBins = 3;
		// Multi-dimensional array representing a matrix for
		// colours in an image of different intensities
		histogram = new int[numOfHueBins][numOfSatBins][numOfValBins];
		int[] pixelValues = new int[3];
		int satBin = -1;
		int valBin = -1;
		int hueBin = -1;
		int[] hueBinValues = {0, 24, 39, 119, 189, 269, 294};

		for (int x = 0; x != imageRaster.getWidth(); x++) {
			for (int y = 0; y != imageRaster.getHeight(); y++) {
				pixelValues = imageRaster.getPixel(x, y, pixelValues);
				int[] hsv = ImageUtils.rgbToHsv(pixelValues);
				
				// Get hue bin
				if (hsv[0] > 314) {
					hueBin = 0;
				} else {
					loop:
					for (int i = hueBinValues.length-1; i >= 0; i--) {
						if (hsv[0] >= hueBinValues[i]) {
							hueBin = i+1;
							break loop;
						}
					}
				}				
				
				// Get saturation bin
				if (hsv[1] >= 70)
					satBin = 2;
				else if (hsv[1] >= 20)
					satBin = 1;
				else
					satBin = 0;
				
				// Get value bin
				if (hsv[2] >= 70)
					valBin = 2;
				else if (hsv[2] >= 20)
					valBin = 1;
				else
					valBin = 0;
				histogram[hueBin][satBin][valBin]++;
			}
		}
	}

	public int[][][] getHistogram() {
		return histogram;
	}
	
	/*
	 * L1 distance measure
	 */
	public static double calculateDistancel1(ColourHistogram h1, ColourHistogram h2) {
		double distance = 0;

		for (int i = 0; i != h1.getHistogram().length; i++)
			for (int j = 0; j != h1.getHistogram()[0].length; j++)
				for (int k = 0; k != h1.getHistogram()[0][0].length; k++) {
					distance += Math.abs(h1.getHistogram()[i][j][k]
							- h2.getHistogram()[i][j][k]);
				}
		
		return distance;
	}
	
	/*
	 * Weighted measure from
	 * Image Indexing Using Color Correlograms pp. 4
	 */
	public static double calculateDistance(ColourHistogram h1, ColourHistogram h2) {
		double distance = 0;

		for (int i = 0; i != h1.getHistogram().length; i++)
			for (int j = 0; j != h1.getHistogram()[0].length; j++)
				for (int k = 0; k != h1.getHistogram()[0][0].length; k++) {
					distance += (double) (Math.abs(h1.getHistogram()[i][j][k]
							- h2.getHistogram()[i][j][k]))/(1+h1.getHistogram()[i][j][k]
									+ h2.getHistogram()[i][j][k]);
				}
		
		return distance;
	}
	
	public static void normaliseGalleryDistances(List<GalleryImage> gallery) {
		double max, min, dist;
		
		max = gallery.get(0).getColorDistance();
		min = gallery.get(0).getColorDistance();
		dist = 0;
		for (int i = 1; i != gallery.size(); i++) {
			dist = gallery.get(i).getColorDistance();
			if (dist > max) {
				max = dist;
			}
			if (dist < min) {
				min = dist;
			}
		}	
		for (GalleryImage img : gallery) {
			img.setColorDistance((img.getColorDistance() - min)/(max - min));
		}
	}

}
