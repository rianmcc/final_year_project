package org.queens.app.imagesearchengine.cooccurrencematrix;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;

import org.queens.app.imagesearchengine.Feature;
import org.queens.app.imagesearchengine.LibraryImage;
import org.queens.app.imagesearchengine.utils.ImageUtils;

public class CooccurrenceMatrix extends Feature {

	/*
	 * An inner class that contains the co-occurrence matrix and relevant
	 * methods and fields
	 */
	class Matrix {
		private int combinationCount;
		private int[][] matrix;
		private double[][] normalisedMatrix;

		public double[][] getNormalisedMatrix() {
			return normalisedMatrix;
		}

		public int getCombinationCount() {
			return combinationCount;
		}

		public Matrix(int xOffset, int yOffset) {
			calculateGLCM(xOffset, yOffset);
			calculateNormalisedMatrix();
		}

		private void calculateGLCM(int xOffset, int yOffset) {
			matrix = new int[numOfColours][numOfColours];
			int[] pixelData = new int[3];

			int referenceGrayValue = 0;
			int neighbourGrayValue = 0;

			/*
			 * The if/else is needed because negative offsets break the first
			 * for loop so a second is needed to remedy this
			 */
			combinationCount = 0;
			if (yOffset >= 0) {
				for (int x = 0; x != imageRaster.getWidth() - xOffset; x++) {
					for (int y = 0; y != imageRaster.getHeight() - yOffset; y++) {
						referenceGrayValue = imageRaster.getPixel(x, y,
								pixelData)[0];
						neighbourGrayValue = imageRaster.getPixel(x + xOffset,
								y + yOffset, pixelData)[0];
						matrix[referenceGrayValue][neighbourGrayValue]++;

						// We have to make the matrix symmetrical around the
						// diagonal
						// so we do the same calculation again except swapping
						// the
						// reference
						// and neighbour pixel
						referenceGrayValue = imageRaster.getPixel(x + xOffset,
								y + yOffset, pixelData)[0];
						neighbourGrayValue = imageRaster.getPixel(x, y,
								pixelData)[0];
						matrix[referenceGrayValue][neighbourGrayValue]++;

						// Need to add two because the matrices are symmetrical
						// e.g. calculating (1, 0) and (-1, 0)
						combinationCount += 2;
					}
				}
			} else {
				for (int x = 0; x != imageRaster.getWidth() - xOffset; x++) {
					for (int y = -yOffset; y != imageRaster.getHeight(); y++) {
						referenceGrayValue = imageRaster.getPixel(x, y,
								pixelData)[0];
						neighbourGrayValue = imageRaster.getPixel(x + xOffset,
								y + yOffset, pixelData)[0];
						matrix[referenceGrayValue][neighbourGrayValue]++;

						// We have to make the matrix symmetrical around the
						// diagonal so we do the same calculation again except
						// swapping the reference and neighbour pixel
						referenceGrayValue = imageRaster.getPixel(x + xOffset,
								y + yOffset, pixelData)[0];
						neighbourGrayValue = imageRaster.getPixel(x, y,
								pixelData)[0];
						matrix[referenceGrayValue][neighbourGrayValue]++;

						// Need to add two because the matrices are symmetrical
						// e.g. calculating (1, 0) and (-1, 0)
						combinationCount += 2;
					}
				}
			}
		}

		private void calculateNormalisedMatrix() {
			normalisedMatrix = new double[numOfColours][numOfColours];

			for (int i = 0; i != numOfColours; i++) {
				for (int j = 0; j != numOfColours; j++) {
					normalisedMatrix[i][j] = (double) matrix[i][j]
							/ combinationCount;
				}
			}
		}
	}

	// The number of colours in the image
	private int numOfColours;

	// Image needs to be converted to grayscale
	// before we start working on it so we need
	// a WriteableRaster
	private WritableRaster imageRaster;

	// Array of feature vectors
	// In order: energy, contrast, entropy, inverse difference moment,
	// homogeneity
	double featuresAvgs[] = new double[6];

	public CooccurrenceMatrix(BufferedImage image) {
		// Copy image into WritableRaster because
		// we don't want to modify the original
		// image.
		imageRaster = image.copyData(null);
		ImageUtils.makeGray(imageRaster);
		numOfColours = 64;
		// Reduce to numOfColours colours
		int[] pixel = new int[3];
		for (int x = 0; x != imageRaster.getWidth(); x++) {
			for (int y = 0; y != imageRaster.getHeight(); y++) {
				pixel = imageRaster.getPixel(x, y, pixel);
				for (int i = 0; i != pixel.length; i++) {
					pixel[i] = pixel[i] / 4;
				}
				imageRaster.setPixel(x, y, pixel);
			}
		}
	}

	@Override
	public void extractFeature() {

		Matrix[] matrices = { new Matrix(1, 0), new Matrix(0, 1),
				new Matrix(1, 1), new Matrix(1, -1) };

		double[] energies = new double[matrices.length];
		double[] contrasts = new double[matrices.length];
		double[] entropies = new double[matrices.length];
		double[] inverseDifferenceMoments = new double[matrices.length];
		double[] homogeineities = new double[matrices.length];
		double[] correlations = new double[matrices.length];

		for (int i = 0; i != matrices.length; i++) {
			energies[i] = getEnergy(matrices[i]);
			contrasts[i] = getContrast(matrices[i]);
			entropies[i] = getEntropy(matrices[i]);
			inverseDifferenceMoments[i] = getInverseDifferenceMoment(matrices[i]);
			homogeineities[i] = getHomogeneity(matrices[i]);
			correlations[i] = getCorrelation(matrices[i]);
		}

		featuresAvgs[0] = getArrayAvg(energies);
		featuresAvgs[1] = getArrayAvg(contrasts);
		featuresAvgs[2] = getArrayAvg(entropies);
		featuresAvgs[3] = getArrayAvg(inverseDifferenceMoments);
		featuresAvgs[4] = getArrayAvg(homogeineities);
		featuresAvgs[5] = getArrayAvg(correlations);

	}

	private double getArrayAvg(double[] array) {
		double sum = 0;
		for (double i : array)
			sum += i;
		return sum / array.length;
	}

	public double[] getFeaturesAvgs() {
		return featuresAvgs;
	}

	private double getEnergy(Matrix m) {
		double energy = 0;

		for (int i = 0; i != numOfColours; i++) {
			for (int j = 0; j != numOfColours; j++) {
				energy += Math.pow(m.getNormalisedMatrix()[i][j], 2);
			}
		}

		return energy;
	}

	private double getContrast(Matrix m) {
		double contrast = 0;

		for (int i = 0; i != numOfColours; i++) {
			for (int j = 0; j != numOfColours; j++) {
				contrast += Math.pow(i - j, 2) * m.getNormalisedMatrix()[i][j];
			}
		}

		return contrast;
	}

	private double getEntropy(Matrix m) {
		double entropy = 0;

		for (int i = 0; i != numOfColours; i++) {
			for (int j = 0; j != numOfColours; j++) {
				/*
				 * We need to check that the value isn't zero because if it is,
				 * Math.log() will return NaN
				 */
				if (m.getNormalisedMatrix()[i][j] != 0) {
					entropy += m.getNormalisedMatrix()[i][j]
							* Math.log(m.getNormalisedMatrix()[i][j]);

				}
			}
		}
		entropy = -entropy;

		return entropy;
	}

	private double getInverseDifferenceMoment(Matrix m) {
		double inverseDifference = 0;

		for (int i = 0; i != numOfColours; i++) {
			for (int j = 0; j != numOfColours; j++) {
				if (i != j) {
					inverseDifference += (m.getNormalisedMatrix()[i][j])
							/ Math.pow(Math.abs(i - j), 2);
				}
			}
		}

		return inverseDifference;
	}

	private double getHomogeneity(Matrix m) {
		double homogeneity = 0;

		for (int i = 0; i != numOfColours; i++) {
			for (int j = 0; j != numOfColours; j++) {
				homogeneity += (m.getNormalisedMatrix()[i][j])
						/ (1 + Math.abs(i - j));
			}
		}

		return homogeneity;
	}

	private double getCorrelation(Matrix m) {
		double correlation = 0;
		double meanX = 0.0;
		double meanY = 0.0;
		double stdevX = 0.0;
		double stdevY = 0.0;
		
		for (int x = 0; x != numOfColours; x++) {
			for (int y = 0; y != numOfColours; y++) {
				meanX += x * m.getNormalisedMatrix()[x][y];
				meanY += y * m.getNormalisedMatrix()[x][y];

			}
		}

		for (int x = 0; x != numOfColours; x++) {
			for (int y = 0; y != numOfColours; y++) {
				stdevX += Math.pow(x - meanX, 2) * m.getNormalisedMatrix()[x][y];
				stdevY += Math.pow(y - meanY, 2) * m.getNormalisedMatrix()[x][y];
			}
		}
		
		stdevX = Math.sqrt(stdevX);
		stdevY = Math.sqrt(stdevY);

		for (int x = 0; x != numOfColours; x++) {
			for (int y = 0; y != numOfColours; y++) {
				correlation += (((x * y) * m.getNormalisedMatrix()[x][y]));
			}
		}
		correlation -= (meanX*meanY);
		correlation /= stdevX * stdevY;		
		
		return correlation;
	}

	public static double[] calculateDistance(CooccurrenceMatrix c1,
			CooccurrenceMatrix c2) {
		double[] distance = new double[c1.getFeaturesAvgs().length];

		// Manhattan Distance
		for (int i = 0; i != c1.getFeaturesAvgs().length; i++) {
			distance[i] += Math.abs(c1.getFeaturesAvgs()[i]
					- c2.getFeaturesAvgs()[i]);
		}
		return distance;
	}

	public static void normaliseLibraryDistances(List<LibraryImage> library) {
		double max, min, dist;
		double[][] textureDistances = new double[192][6];

		for (int j = 0; j != library.get(0).getTextureVectorDistances().length; j++) {
			max = library.get(0).getTextureVectorDistances()[j];
			min = library.get(0).getTextureVectorDistances()[j];
			dist = 0;
			for (int i = 1; i != library.size(); i++) {
				dist = library.get(i).getTextureVectorDistances()[j];
				if (dist > max) {
					max = dist;
				}
				if (dist < min) {
					min = dist;
				}
			}
			for (int i = 0; i != library.size(); i++) {
				textureDistances[i][j] = (library.get(i)
						.getTextureVectorDistances()[j] - min) / (max - min);
			}
		}

		for (int i = 0; i != library.size(); i++) {
			double sum = 0;
			for (double j : textureDistances[i]) {
				sum += j;
			}
			sum /= textureDistances[i].length;
			library.get(i).setTextureDistance(sum);
		}
	}
}
