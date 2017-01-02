package org.queens.app.imagesearchengine;

import java.awt.image.Raster;

public class ColourHistogram {
	// Array size will be the number of bins in histogram
	int[] histogram;
	
	public ColourHistogram(int histogramSize) {
		histogram = new int[histogramSize];
	}

	public int[][] generateImageHistogram(Raster image) {
		// Multi-dimensional array representing a matrix for
		// colours in an image of different intensities
		// x: red, green, blue
		// y: 0-63, 64-127, 128-191, 192-255
		int[][] colourHistogram = new int[3][4];
		int[] pixelValues = new int[3];
		int index = 0;
		
		for (int x = 0; x != image.getWidth(); x++) {
			for (int y = 0; y != image.getHeight(); y++) {
				pixelValues = image.getPixel(x, y, pixelValues);
				index = 0;
				for (int i : pixelValues) {
					if (i >= 192)
						colourHistogram[index][3]++;
					else if (i >= 128)
						colourHistogram[index][2]++;
					else if (i >= 64)
						colourHistogram[index][1]++;
					else
						colourHistogram[index][0]++;
					index++;
				}
			}
		}
		return colourHistogram;
	}

}
