package org.queens.app.imagesearchengine.edgehistogram;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.edgehistogram.EdgeHistogram;

public class EdgeHistogramTest {
	
	@Test
	public final void testImageBlockVertical() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/imageBlockVertical.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		assertEquals(0, tester.getEdgeFeature(0, 0));
	}
	
	@Test
	public final void testImageBlockHorizontal() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/imageBlockHorizontal.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		assertEquals(1, tester.getEdgeFeature(0, 0));
	}
	
	@Test
	public final void testImageBlock45Degree() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/imageBlock45Degree.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		assertEquals(2, tester.getEdgeFeature(0, 0));
	}
	
	@Test
	public final void testImageBlock135Degree() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/imageBlock135Degree.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		assertEquals(3, tester.getEdgeFeature(0, 0));
	}
	
	@Test
	public final void testLocalHistogram() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/subImageTest.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		int[] localHistogram = tester.calculateLocalHistogram(0, 0);
		
		assertArrayEquals(new int[]{64,0,0,0,0}, localHistogram);
		
	}
	
	@Test
	public final void testExtractFeature() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/edgeHistogramTest.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		tester.extractFeature();
				
		assertEquals(64, tester.getBins()[0]);
		assertEquals(64, tester.getBins()[26]);
		assertEquals(64, tester.getBins()[50]);
		assertEquals(64, tester.getBins()[76]);



	}

}
