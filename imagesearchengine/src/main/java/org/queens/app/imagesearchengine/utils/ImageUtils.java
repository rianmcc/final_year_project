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

	public static int[] rgbToHsv(int[] rgb) {
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

}
