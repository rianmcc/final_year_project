package org.queens.app.imagesearchengine;

/**
 * An {@link Enum} class for holding and assigning categories to images for precision
 * and recall calculation.
 * 
 * @author Rian McCreesh
 *
 */
public class EnumCategory {
	public enum Category {
		HORSE, LANDSCAPE, CROWD, F1CAR
	}

	/**
	 * Returns a Category for the given filename. Returns null if it can't match
	 * the name with a category.
	 * 
	 * @param 	fileName	
	 *            Name of file to be categorised.
	 * @return 	Category for the file name.
	 */
	public static Category getCategoryFromString(String fileName) {
		if (fileName.startsWith("Horses"))
			return Category.HORSE;
		else if (fileName.startsWith("Landscapes"))
			return Category.LANDSCAPE;
		else if (fileName.startsWith("F1-Cars"))
			return Category.F1CAR;
		else if (fileName.startsWith("Crowds"))
			return Category.CROWD;
		return null;
	}
}
