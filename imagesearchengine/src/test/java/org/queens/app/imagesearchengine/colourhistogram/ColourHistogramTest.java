package org.queens.app.imagesearchengine.colourhistogram;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.colourhistogram.ColourHistogram;

public class ColourHistogramTest {

	@Test
	public void testCache() throws IOException {
		ColourHistogram tester = new ColourHistogram(ImageIO.read(new File(
				"src/test/resources/cat.jpg")));

		tester.extractFeature();
		int[][][] histogram = tester.getHistogram();
		
		//System.out.println(Arrays.deepToString(histogram));

		for(int i = 0; i < histogram.length; i++)
            for(int j = 0; j < histogram[i].length; j++)
                for(int p = 0; p < histogram[i][j].length; p++)
                    System.out.println("t[" + i + "][" + j + "][" + p + "] = " + histogram[i][j][p]);
		// Finish test
	}
	
	@Test
	public void testRgbToHsv() {
		ColourHistogram tester = null;
		
		try {
			tester = new ColourHistogram(ImageIO.read(new File(
					"src/test/resources/cat.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] rgb = {133,132,133};
		
		int[] hsv = tester.rgbToHsv(rgb);
		
		System.out.println(Arrays.toString(hsv));
	}

}
