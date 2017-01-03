package org.queens.app.imagesearchengine.utils;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class RasterMd5Test {

	@Test
	public void testRasterMd5() {
		// file md5: C620F927F83ED94DF5E45E3F52AC737E
		File imageFile = new File("src/test/resources/md5_test.jpg");
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String correctHash = "C620F927F83ED94DF5E45E3F52AC737E";
		String testHash = RasterMd5.generateRasterMd5(image);
		
		assertEquals(testHash, correctHash);
	}

}
