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
	public void test() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/histogram_test.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		tester.getEdgeFeature(188, 153);
		
	}
	
	@Test
	public void testLocalHistogram() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/histogram_test.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		int[] localHistogram = tester.calculateLocalHistogram(384, 384);
		
        String[] strings = {"Vertical: ", "Horizontal: ", "45 Diagonal: ", "135 Diagonal: ", "Non-directional: "};
        for (int w = 0; w != 5; w++) {
        	System.out.println(strings[w] + localHistogram[w]);
        }
	}
	
	@Test
	public void testFullHistogram() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("testdata/query_images/Crowds013.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		tester.extractFeature();
		
        int[] localBins = tester.getBins();
        
        for (int value: localBins) {
        	System.out.println(value);
        }
	}
	
	@Test
	public void testImageBlockVertical() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/imageBlockVertical.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		tester.getEdgeFeature(0, 0);
	}
	
	@Test
	public void testImageBlockHorizontal() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/imageBlockHorizontal.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		tester.getEdgeFeature(0, 0);
	}
	
	@Test
	public void testSubImage() {
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/saved.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EdgeHistogram tester = new EdgeHistogram(testImage);
		
		int[] localHistogram = tester.calculateLocalHistogram(0, 0);
		
        String[] strings = {"Vertical: ", "Horizontal: ", "45 Diagonal: ", "135 Diagonal: ", "Non-directional: "};
        for (int w = 0; w != 5; w++) {
        	System.out.println(strings[w] + localHistogram[w]);
        }
	}


}
