package org.queens.app.imagesearchengine.utils;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageUtilsTest {

	@Test
	public final void test() {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("src/test/resources/cat.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WritableRaster imageRaster = image.getRaster();
		
		ImageUtils.makeGray(imageRaster);
	}

}
