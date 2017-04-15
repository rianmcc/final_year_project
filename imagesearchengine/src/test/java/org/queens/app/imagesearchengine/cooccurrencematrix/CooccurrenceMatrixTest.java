package org.queens.app.imagesearchengine.cooccurrencematrix;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;

public class CooccurrenceMatrixTest {

	@Test
	public final void testExtractFeature() {
		CooccurrenceMatrix tester = null;
		try {
			tester = new CooccurrenceMatrix(ImageIO.read(new File(
					"testdata/query_images/Crowds013.jpg")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int r = 0;
		int g = 0;
		int b = 0;
		int zero = (r << 16) | (g << 8) | b;
		r = 1;
		g = 1;
		b = 1;
		int one = (r << 16) | (g << 8) | b;
		r = 2;
		g = 2;
		b = 2;
		int two = (r << 16) | (g << 8) | b;
		r = 3;
		g = 3;
		b = 3;
		int three = (r << 16) | (g << 8) | b;

		BufferedImage new_image = new BufferedImage(512, 512,
				BufferedImage.TYPE_INT_RGB);
		new_image.setRGB(0, 0, zero);
		new_image.setRGB(1, 0, zero);
		new_image.setRGB(2, 0, one);
		new_image.setRGB(3, 0, one);
		new_image.setRGB(0, 1, zero);
		new_image.setRGB(1, 1, zero);
		new_image.setRGB(2, 1, one);
		new_image.setRGB(3, 1, one);
		new_image.setRGB(0, 2, zero);
		new_image.setRGB(1, 2, two);
		new_image.setRGB(2, 2, two);
		new_image.setRGB(3, 2, two);
		new_image.setRGB(0, 3, two);
		new_image.setRGB(1, 3, two);
		new_image.setRGB(2, 3, three);
		new_image.setRGB(3, 3, three);
		
		for (int i = 0; i != new_image.getWidth(); i++) {
			for (int j = 0; j != new_image.getHeight(); j++) {
				if (j % 8 != 0)
					new_image.setRGB(i, j, Color.BLACK.getRGB());
				else
					new_image.setRGB(i, j, Color.WHITE.getRGB());
			}
			
		}
		
		//tester = new CooccurrenceMatrix(new_image);

		tester.extractFeature();
		
//		for (int i = 0; i != 256; i++) {
//			for (int j = 0; j != 256; j++) {
//				System.out.print(tester.getMatrix()[i][j] + "\t");
//			}
//			System.out.println();
//		}
		
		//System.out.println(Arrays.deepToString(tester.getMatrix()));

	}

}
