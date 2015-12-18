package facemapping;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
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
			this.colorFaceImage = matToPImage(colorFaceROI);
		}

	public void updateFrontalFace(Mat face, float[] leftEye, float[] rightEye)
		{
			if (leftEye == null || rightEye == null)
			{
				throw new NullPointerException("One of the eyes is null");
			}

			double dx = rightEye[0] - leftEye[0];
			double dy = rightEye[1] - leftEye[1];
			double angle = Math.atan2(dy, dx) * 180 / Math.PI;
			Point center = new Point(face.width() / 2, face.height() / 2);

			Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1);
			Mat rotatedFace = new Mat();
			Imgproc.warpAffine(face, rotatedFace, rotMatrix, new Size(face.width(), face.height()));

			PImage front = matToPImage(rotatedFace);
			this.colorFaceImage = front;
		}

	private PImage matToPImage(Mat m)
		{
			PImage result = new PImage(m.width(), m.height(), PImage.ARGB);
			for (int i = 0; i < m.height(); i++)
			{
				for (int j = 0; j < m.width(); j++)
				{
					// i = row = y coordinate, j = column = x coordinate
					double[] colorValues = m.get(i, j);

					// apparently the colors are bgr, not rgb
					int r = (int)(colorValues[2]),
									g = (int)(colorValues[1]),
									b = (int)(colorValues[0]);

					int color = 0xFF000000 | (r << 16) | (g << 8) | b;
					result.set(j, i, color);
				}
			}
			return result;
		}

	public PImage toPImage()
		{
			return colorFaceImage;
		}
}
