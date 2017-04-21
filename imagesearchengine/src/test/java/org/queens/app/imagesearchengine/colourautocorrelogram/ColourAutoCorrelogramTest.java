package org.queens.app.imagesearchengine.colourautocorrelogram;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.colourautocorrelogram.ColourAutoCorrelogram;

public class ColourAutoCorrelogramTest {

	@Test
	public void test() {
		ColourAutoCorrelogram tester = null;
		try {
			tester = new ColourAutoCorrelogram(ImageIO.read(new File(
					"testdata/query_images/Crowds013.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tester.extractFeature();
		
		System.out.println(Arrays.deepToString(tester.autoCorrelogram));
	}

}
