package org.queens.app.imagesearchengine.cooccurrencematrix;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;

public class CooccurrenceMatrixTest {
	// TODO This whole class needs tested

	@Test
	public final void testExtractFeature() {
		CooccurrenceMatrix tester = null;
		try {
			BufferedImage img = ImageIO.read(new File("testdata/query_images/Crowds013.jpg"));
			BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = copy.createGraphics();
			g2d.setColor(Color.WHITE); // Or what ever fill color you want...
			g2d.fillRect(0, 0, copy.getWidth(), copy.getHeight());
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
			tester = new CooccurrenceMatrix(copy);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tester.extractFeature();
		
		// Manually get feature values from MATLAB and assert them to the actual values

		

	}

}
