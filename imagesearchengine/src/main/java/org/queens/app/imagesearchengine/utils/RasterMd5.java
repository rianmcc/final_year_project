package org.queens.app.imagesearchengine.utils;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

public class RasterMd5 {
	
	private RasterMd5() {
	}
	
	public String generateRasterMd5(Raster image) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write((RenderedImage) image, "jpg", outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = outputStream.toByteArray();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(data);
		byte[] digest = md.digest();

		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		
		return hashtext;
	}

}
