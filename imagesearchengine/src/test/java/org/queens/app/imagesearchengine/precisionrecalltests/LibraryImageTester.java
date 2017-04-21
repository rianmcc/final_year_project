package org.queens.app.imagesearchengine.precisionrecalltests;

import java.awt.image.BufferedImage;

import org.queens.app.imagesearchengine.EnumCategory;
import org.queens.app.imagesearchengine.EnumCategory.Category;
import org.queens.app.imagesearchengine.LibraryImage;

public class LibraryImageTester extends LibraryImage {
	private Category category;

	public LibraryImageTester(BufferedImage imageData, String fileName) {
		super(imageData);
		category = EnumCategory.getCategoryFromString(fileName);
	}
	
	public Category getCategory() {
		return category;
	}

}
