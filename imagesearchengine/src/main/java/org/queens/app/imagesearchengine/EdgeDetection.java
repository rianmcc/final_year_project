package org.queens.app.imagesearchengine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EdgeDetection {

	public void computeEdges(BufferedImage image) {
		makeGray(image);

		try {
			File outputfile = new File("saved.jpeg");
			ImageIO.write((RenderedImage) image, "jpeg", outputfile);
		} catch (IOException e) {
		}

		
	}
	
	static BufferedImage outputImg;
	static int[][] pixelMatrix=new int[3][3];

	public void compute(BufferedImage inputImg) {
	    try {

	        outputImg=new BufferedImage(inputImg.getWidth(),inputImg.getHeight(),inputImg.getType());

	        for(int i=1;i<inputImg.getWidth()-1;i++){
	            for(int j=1;j<inputImg.getHeight()-1;j++){
	                pixelMatrix[0][0]=new Color(inputImg.getRGB(i-1,j-1)).getRed();
	                pixelMatrix[0][1]=new Color(inputImg.getRGB(i-1,j)).getRed();
	                pixelMatrix[0][2]=new Color(inputImg.getRGB(i-1,j+1)).getRed();
	                pixelMatrix[1][0]=new Color(inputImg.getRGB(i,j-1)).getRed();
	                pixelMatrix[1][2]=new Color(inputImg.getRGB(i,j+1)).getRed();
	                pixelMatrix[2][0]=new Color(inputImg.getRGB(i+1,j-1)).getRed();
	                pixelMatrix[2][1]=new Color(inputImg.getRGB(i+1,j)).getRed();
	                pixelMatrix[2][2]=new Color(inputImg.getRGB(i+1,j+1)).getRed();

	                int edge=(int) convolution(pixelMatrix);
	                outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
	            }
	        }

	        File outputfile = new File("outputImg.jpg");
	        ImageIO.write(outputImg,"jpg", outputfile);

	    } catch (IOException ex) {System.err.println("Image width:height="+inputImg.getWidth()+":"+inputImg.getHeight());}
	}
	public double convolution(int[][] pixelMatrix){

	    int gy=(pixelMatrix[0][0]*-1)+(pixelMatrix[0][1]*-2)+(pixelMatrix[0][2]*-1)+(pixelMatrix[2][0])+(pixelMatrix[2][1]*2)+(pixelMatrix[2][2]*1);
	    int gx=(pixelMatrix[0][0])+(pixelMatrix[0][2]*-1)+(pixelMatrix[1][0]*2)+(pixelMatrix[1][2]*-2)+(pixelMatrix[2][0])+(pixelMatrix[2][2]*-1);
	    return Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));

	}

	// http://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
	public static void makeGray(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				int grayLevel = (r + g + b) / 3;
				int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
				img.setRGB(x, y, gray);
			}
	}
}
