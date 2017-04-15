package org.queens.app.imagesearchengine.colourhistogram;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.queens.app.imagesearchengine.Feature;

public class ColourHistogram extends Feature {
	// Array size will be the number of bins in histogram
	private int[][][] histogram;
	
	private BufferedImage image;
	private Raster imageRaster;

	public ColourHistogram(BufferedImage image) {
		this.image = image;
		imageRaster = image.getData();
	}

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
		int hueBinSize = 256 / numOfHueBins;
		int satBin = -1;
		int valBin = -1;
		int hueBin = -1;
		int[] hueBinValues = {0, 24, 39, 119, 189, 269, 294};

		for (int x = 0; x != imageRaster.getWidth(); x++) {
			for (int y = 0; y != imageRaster.getHeight(); y++) {
				pixelValues = imageRaster.getPixel(x, y, pixelValues);
				int[] hsv = rgbToHsv(pixelValues);
				
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
	
	int[] rgbToHsv(int[] rgb) {
		float hue = 0f;
		float saturation = 0f;
		float value = 0f;
		float r = rgb[0] / 255f;
		float g = rgb[1] / 255f;
		float b = rgb[2] / 255f;
		
		float min = Math.min(Math.min(r, g), b);
		float max = Math.max(Math.max(r, g), b);
		float delta = max - min;
		
		value = max;
		
		if (max == r) {
			if (g >= b) {
				hue = 60 * ((g-b)/delta);
			} else {
				hue = 60 * (((g-b)/delta) % 6) + 360;
			}
		}
		else if (max == g) {
			hue = 60 * (((b-r)/delta) + 2);
		}
		else if (max == b) {
			hue = 60 * (((r-g)/delta) + 4);
		}
		
		if (max == 0) {
			saturation = 0;
		} else
			saturation = delta / max;
		
		int[] hsv = new int[3];
		hsv[0] = (int) hue;
		hsv[1] = (int) (saturation * 100);
		hsv[2] = (int) (value * 100);
		
		return hsv;

	}

	public int[][][] getHistogram() {
		return histogram;
	}
	
	public static double calculateDistance(ColourHistogram h1, ColourHistogram h2) {
		double distance = 0;

		for (int i = 0; i != h1.getHistogram().length; i++)
			for (int j = 0; j != h1.getHistogram()[0].length; j++)
				for (int k = 0; k != h1.getHistogram()[0][0].length; k++) {
					distance += Math.abs(h1.getHistogram()[i][j][k]
							- h2.getHistogram()[i][j][k]);
				}
		
		return distance;
	}

}
