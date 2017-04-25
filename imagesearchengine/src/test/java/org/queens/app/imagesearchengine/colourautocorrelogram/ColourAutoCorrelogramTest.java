package org.queens.app.imagesearchengine.colourautocorrelogram;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.colourautocorrelogram.ColourAutoCorrelogram;

public class ColourAutoCorrelogramTest {

	@Test
	public final void testExtractFeature() {
		ColourAutoCorrelogram tester = null;
		try {
			tester = new ColourAutoCorrelogram(ImageIO.read(new File(
					"src/test/resources/correlogramTest.png")),new int[]{1});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tester.extractFeature();
		
		assertEquals(2f, tester.getCorrelogram()[0][0], 1e-15);
		assertEquals(5f, tester.getCorrelogram()[63][0], 1e-15);
		
	}

}
