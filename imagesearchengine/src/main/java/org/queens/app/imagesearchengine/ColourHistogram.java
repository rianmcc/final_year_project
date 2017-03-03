package org.queens.app.imagesearchengine;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.queens.app.imagesearchengine.utils.DbConnection;
import org.queens.app.imagesearchengine.utils.RasterMd5;
import org.queens.app.imagesearchengine.utils.Serial;

public class ColourHistogram {
	// Array size will be the number of bins in histogram
	private int[][] histogram;

	public ColourHistogram(int histogramSize) {
		histogram = new int[3][histogramSize];
	}

	public void getImageHistogram(BufferedImage image) {
		histogram = generateImageHistogram(image);
	}

	public int[][] generateImageHistogram(BufferedImage image) {
		Raster imageRaster = image.getData();
		int numOfBins = 32;
		// Multi-dimensional array representing a matrix for
		// colours in an image of different intensities
		int[][] colourHistogram = new int[3][numOfBins];
		int[] pixelValues = new int[3];
		int index = 0;
		int binSize = 256 / numOfBins;

		for (int x = 0; x != imageRaster.getWidth(); x++) {
			for (int y = 0; y != imageRaster.getHeight(); y++) {
				pixelValues = imageRaster.getPixel(x, y, pixelValues);
				index = 0;
				for (int i : pixelValues) {
					colourHistogram[index][i / binSize]++;
					index++;
				}
			}
		}
		return colourHistogram;
	}

	public int[][] checkOrUpdateCache(BufferedImage image) throws SQLException {
		Connection conn = DbConnection.createConnection();
		Statement stat = conn.createStatement();
		PreparedStatement pstmt;
		stat.executeUpdate("create table if not exists hist_cache (md5 string, histogram string);");

		String hash = RasterMd5.generateRasterMd5(image);
		String selectQuery = "SELECT histogram FROM hist_cache WHERE md5 = ?";
		pstmt = conn.prepareStatement(selectQuery);
		pstmt.setString(1, hash);
		ResultSet rs = pstmt.executeQuery();

		if (!rs.next()) {
			int[][] histogram = generateImageHistogram(image);

			String sql = "insert into hist_cache values(?,?)";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, hash);
				pstmt.setString(2, Serial.serialize(histogram));
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				pstmt.close();
				DbConnection.closeConnection(conn);
			}

			return histogram;
		} else {
			try {
				return Serial.deserialize(rs.getString("histogram"));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				pstmt.close();
				DbConnection.closeConnection(conn);
			}
		}
		return null;
	}

	public int[][] getHistogram() {
		return histogram;
	}

}
