package org.queens.app.imagesearchengine.utils;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageUtilsTest {

	@Test
	public final void testMakeGray() {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("src/test/resources/makeGrayTest.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WritableRaster imageRaster = image.getRaster();
		
		ImageUtils.makeGray(imageRaster);
		
		int[] pixel = new int[3];
		imageRaster.getPixel(0, 0, pixel);
		assertArrayEquals(new int[] {0,0,0}, pixel);
		imageRaster.getPixel(0, 1, pixel);
		assertArrayEquals(new int[] {133,133,133}, pixel);
		imageRaster.getPixel(1, 0, pixel);
		assertArrayEquals(new int[] {131,131,131}, pixel);
		imageRaster.getPixel(1, 1, pixel);
		assertArrayEquals(new int[] {113,113,113}, pixel);
		imageRaster.getPixel(2, 0, pixel);
		assertArrayEquals(new int[] {210,210,210}, pixel);
		imageRaster.getPixel(2, 1, pixel);
		assertArrayEquals(new int[] {165,165,165}, pixel);
	}
	
	@Test
	public final void testRgbToHsv() {
		assertArrayEquals(new int[] {0,0,100}, ImageUtils.rgbToHsv(new int[] {255,255,255}));
		assertArrayEquals(new int[] {0,0,0}, ImageUtils.rgbToHsv(new int[] {0,0,0}));
		assertArrayEquals(new int[] {0,0,50}, ImageUtils.rgbToHsv(new int[] {128,128,128}));
		assertArrayEquals(new int[] {106,63,100}, ImageUtils.rgbToHsv(new int[] {128,255,92}));
	}

}
