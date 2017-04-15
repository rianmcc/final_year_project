package org.queens.app.imagesearchengine;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class EdgeDetectionTest {

	@Test
	public void test() {
		EdgeDetection tester = new EdgeDetection();
		
		BufferedImage testImage = null;
		try {
			testImage = ImageIO.read(new File("src/test/resources/histogram_test.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tester.compute(testImage);
	}

}
