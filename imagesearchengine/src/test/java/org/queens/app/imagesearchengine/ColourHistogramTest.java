package org.queens.app.imagesearchengine;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.queens.app.imagesearchengine.utils.DbConnection;

public class ColourHistogramTest {

	@Test
	public void testCache() throws SQLException, IOException {
		ColourHistogram tester = new ColourHistogram(0);

		int[][] histogram = tester.checkOrUpdateCache(ImageIO.read(new File(
				"src/test/resources/md5_test.jpg")));
		
		System.out.println(Arrays.deepToString(histogram));

		// Finish test
	}

}
