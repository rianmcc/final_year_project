package org.queens.app.imagesearchengine;

import java.awt.image.BufferedImage;

public class LibraryImage implements Comparable<LibraryImage> {
	private BufferedImage imageData;
	private double distance;
	
	public LibraryImage(BufferedImage imageData) {
		this.imageData = imageData;
	}

	public BufferedImage getImageData() {
		return imageData;
	}

	public void setImageData(BufferedImage imageData) {
		this.imageData = imageData;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(LibraryImage o) {
		double compareDistance = ((LibraryImage)o).getDistance();
		
		return Double.compare(distance, compareDistance);
	}
	
	

}
