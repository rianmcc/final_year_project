package org.queens.app.imagesearchengine.colourhistogram;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.colourhistogram.ColourHistogram;

public class ColourHistogramTest {

	@Test
	public final void testExtractFeature() {
		ColourHistogram tester = null;
		try {
			tester = new ColourHistogram(ImageIO.read(new File(
					"src/test/resources/colourHistogramTest.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tester.extractFeature();
				
		// Test some of the bins to ensure they are correctly populated
		assertArrayEquals(new int[]{0,10,0}, tester.getHistogram()[0][2]);
		assertArrayEquals(new int[]{3,8,38}, tester.getHistogram()[1][0]);
		
//		try (BufferedWriter br = new BufferedWriter(new FileWriter("data.csv"))) {
//			
//		} catch (Exception e) {
//			
//		}
	
	}
}
