package org.queens.app.imagesearchengine.utils;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageUtils {
	
	// http://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
	public static void makeGray(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);
				System.out.println(r);
				System.out.println(g);
				System.out.println(b);


				int grayLevel = (r + g + b) / 3;
				int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
				img.setRGB(x, y, gray);
			}
		
	}
	
	// This method uses the WritableRaster instead of BufferedImage
	// and it is a lot faster
	public static void makeGray(WritableRaster img) {
		int[] rgb = new int[3];
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
				rgb = img.getPixel(x, y, rgb);

				int grayLevel = (rgb[0] + rgb[1] + rgb[2]) / 3;
				for (int i = 0; i != 3; i++)
					rgb[i] = grayLevel;
				img.setPixel(x, y, rgb);
			}
		
	}
	
	// An alternative algorithm that I got from this paper:
	// Content-based image retrieval using color and texture fused features
	// Doesn't seem to work as well (for shape anyway)
	public static void makeGrayOld(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				double grayLevel = (r*0.29) + (g*0.587) + (b*0.114);
				int grayLevelRound = (int) Math.round(grayLevel);

				int gray = (grayLevelRound << 16) + (grayLevelRound << 8) + grayLevelRound;
				img.setRGB(x, y, gray);
			}
	}

}
