package facemapping;

import org.opencv.core.Mat;
import processing.core.PImage;

/**
 * Encapsulates the information for a detected face. It contains an image of the
 * face and any detected features (eyes, mouth, etc) and metadata (angle, etc).
 *
 * It
 */
public class DetectedFace {

	private Mat colorFaceROI;
	private PImage colorFaceImage;

	public DetectedFace(Mat colorFaceROI)
		{
			this.colorFaceROI = colorFaceROI;
			if (colorFaceROI.channels() == 1)
				{
					// Then the image is in greyscale
				}

			// Copy the matrix to a PImage
			this.colorFaceImage = new PImage(colorFaceROI.width(), colorFaceROI.height(), PImage.ARGB);
			for (int i = 0; i < colorFaceROI.height(); i++)
				{
					for (int j = 0; j < colorFaceROI.width(); j++)
						{
							// i = row = y coordinate, j = column = x coordinate
							double[] colorValues = colorFaceROI.get(i, j);

							// apparently the colors are bgr, not rgb
							int r = (int)(colorValues[2]),
									g = (int)(colorValues[1]),
									b = (int)(colorValues[0]);

							int color = 0xFF000000 | (r << 16) | (g << 8) | b;
							this.colorFaceImage.set(j, i, color);
						}
				}
		}


	public PImage toPImage()
		{
			return colorFaceImage;
		}

	public void getRightEye()
		{

		}
}
