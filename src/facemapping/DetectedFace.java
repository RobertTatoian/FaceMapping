package facemapping;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * Encapsulates the information for a detected face. It contains an image of the
 * face and any detected features (eyes, mouth, etc) and metadata (angle, etc).
 *
 * It
 */
public class DetectedFace {

	private Mat colorFaceROI;
	private PImage frontTexture;
	private PImage profileTexture;

	private static final int TEXTURE_HEIGHT = 300;
	private static final int TEXTURE_WIDTH = 500;

	private float numberFaces = 0;

	public DetectedFace(Mat colorFaceROI)
		{

			this.frontTexture = new PImage((TEXTURE_HEIGHT * 5) / 3,  TEXTURE_HEIGHT, PImage.ARGB);
			this.profileTexture = new PImage((TEXTURE_HEIGHT * 5) / 3,  TEXTURE_HEIGHT, PImage.ARGB);
		}

	public void updateFrontTexture(Mat face, float[] leftEye, float[] rightEye)
		{
			if (leftEye == null || rightEye == null)
			{
				throw new NullPointerException("One of the eyes is null");
			}

			// Rotate the face to using the position of the eyes
			double dx = rightEye[0] - leftEye[0];
			double dy = rightEye[1] - leftEye[1];
			double angle = Math.atan2(dy, dx) * 180 / Math.PI;
			Point center = new Point(face.width() / 2, face.height() / 2);

			Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1);
			Mat rotatedFace = new Mat();
			Imgproc.warpAffine(face, rotatedFace, rotMatrix, face.size());

			// Translate and scale the image using the position of the eyes
			// convert angle to radians
			angle *= -Math.PI / 180;

			// TODO remove duplicate code
			double[] newLeftEye = new double[]{
							(leftEye[0] - center.x) * Math.cos(angle)
							-(leftEye[1] - center.y) *	Math.sin(angle) + center.x,
							(leftEye[0] - center.x) * Math.sin(angle)
							+(leftEye[1] - center.y) *	Math.cos(angle) + center.y
			};
			double[] newRightEye = new double[]{
							(rightEye[0] - center.x) * Math.cos(angle)
							-(rightEye[1] - center.y) *	Math.sin(angle) + center.x,
							(rightEye[0] - center.x) * Math.sin(angle)
							+(rightEye[1] - center.y) *	Math.cos(angle) + center.y
			};
			Point eyeCenter = new Point((newLeftEye[0] + newRightEye[0])/2, (newLeftEye[1] + newRightEye[1])/2);

			double eyeDistance = newRightEye[0] - newLeftEye[0];
			Mat centeredFace = Mat.zeros(new Size(eyeDistance * 5, eyeDistance * 3), rotatedFace.type());
			int rowShift = (int)((centeredFace.height() - rotatedFace.height())/2 + (rotatedFace.height() * 2.0 / 5.0) - eyeCenter.y);
			int colShift = (int)(centeredFace.width()/2 - eyeCenter.x);
			for (int i = 0; i < rotatedFace.rows(); i++)
			{
				for (int j = 0; j < rotatedFace.cols(); j++)
				{
					if (i + rowShift >= 0 && i + rowShift < centeredFace.height() && j + colShift >= 0 && j + colShift < centeredFace.width())
					{
						centeredFace.put(i + rowShift, j + colShift, rotatedFace.get(i, j));
					}
				}
			}

			Point tl = new Point(newLeftEye[0] + colShift, newLeftEye[1] + rowShift);
			Point br = new Point(newRightEye[0] + colShift, newRightEye[1] + rowShift);
	//		Imgproc.rectangle(centeredFace, tl, br, new Scalar(0, 0, 255, 255), 1);

			// TODO blend it into the texture
			Mat flipped = new Mat();
			Core.flip(centeredFace, flipped, 1);
			PImage front = matToPImage(centeredFace);
			PImage flip = matToPImage(flipped);
			float scale = (TEXTURE_HEIGHT + 0.0f) / centeredFace.height();
			flip.resize((int)(front.width * scale), (int)(front.height * scale));
			front.resize((int)(front.width * scale), (int)(front.height * scale));

			overlayFace(frontTexture, front);
			overlayFace(frontTexture, flip);
		}

	public void updateProfileTexture(Mat profile, float[] eyeCoordinates, boolean facingRight)
		{
		  // as of now, there is now way to check the rotation of the profile
			double height = profile.size().height;

			Mat centeredFace = Mat.zeros(new Size(height * 5 / 3, height), profile.type());
			int rowShift = (int)(profile.height() * 2.0 / 5.0 - eyeCoordinates[1]);
			int colShift = (int)(profile.width() - eyeCoordinates[0]);

			if (!facingRight)
			{
				colShift = centeredFace.width() - colShift - profile.width();
			}

			for (int i = 0; i < profile.rows(); i++)
			{
				for (int j = 0; j < profile.cols(); j++)
				{
					if (i + rowShift >= 0 && i + rowShift < centeredFace.height() && j + colShift >= 0 && j + colShift < centeredFace.width())
					{
						centeredFace.put(i + rowShift, j + colShift, profile.get(i, j));
					}
				}
			}

			if (!facingRight)
			{
				Mat temp = new Mat();
				Core.flip(centeredFace, temp, 1);
				centeredFace = temp;
			}

			PImage front = matToPImage(centeredFace);
			float scale = (TEXTURE_HEIGHT + 0.0f) / centeredFace.height();
			front.resize((int)(front.width * scale), (int)(front.height * scale));

			overlayFace(profileTexture, front);
		}

	public float getEyeDistance()
	{
		return frontTexture.height / 3;
	}

	private PImage matToPImage(Mat m)
		{
			int width = m.width(), height = m.height();
			PImage result = new PImage(width, height, PImage.ARGB);
			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++)
				{
					// i = row = y coordinate, j = column = x coordinate
					double[] colorValues = m.get(i, j);

					// apparently the colors are bgr, not rgb
					int r = (int)(colorValues[2]),
									g = (int)(colorValues[1]),
									b = (int)(colorValues[0]);

					int alpha = 0xFF;

					if (Math.abs(j - width / 2) > width / 10)
					{
						double scale = (width / 10 + 0.0) / Math.abs(j - width / 2 + 0.0);
						scale = scale * scale;
					//	alpha *= scale;
					}

					int color = (alpha << 24) | (r << 16) | (g << 8) | b;
					result.set(j, i, color);
				}
			}
			return result;
		}

	private void overlayFace(PImage oldTexture, PImage newFace)
	{
		numberFaces++;
		float weight = (float)Math.sqrt(1.0f / numberFaces);

		for (int i = 0; i < oldTexture.width; i++)
		{
			for (int j = 0; j < oldTexture.height; j++)
			{
				oldTexture.set(i, j, blendPixels(oldTexture.get(i, j), newFace.get(i, j), weight));
			}
		}
	}

	private int blendPixels(int oldPixel, int newPixel, float weight)
	{
		if (oldPixel == 0 || oldPixel == 0xFF000000)
		{
			oldPixel = newPixel;
		}
		int r = (int)(((oldPixel & 0xFF0000) >> 16) * (1 - weight) + ((newPixel & 0xFF0000) >> 16) * weight);
		int g = (int)(((oldPixel & 0x00FF00) >> 8) * (1 - weight) + ((newPixel & 0x00FF00) >> 8) * weight);
		int b = (int)((oldPixel & 0x0000FF) * (1 - weight) + (newPixel & 0x0000FF) * weight);

		return (0xFF << 24) | (r << 16) | (g << 8) | (b);
	}

	/**
	 * Returns a PImage of the detected
	 */
	public PImage toPImage()
		{
			PImage output = new PImage((TEXTURE_HEIGHT * 5) / 3,  TEXTURE_HEIGHT, PImage.ARGB);

			for (int i = 250; i < output.width; i++)
			{
				for (int j = 0; j < output.width; j++)
				{
					float weight = i < 300 ? 1f : 0f;
					if (300 < i && i < 350)
					{
						weight = (350f - i) / 50f;
					}
					output.set(i, j, blendPixels(profileTexture.get(i, j), frontTexture.get(i, j), weight));
					int r = (output.get(i, j) & 0xFF0000) >> 16;
					int g = (output.get(i, j) & 0x00FF00) >> 8;
					int b = (output.get(i, j) & 0x0000FF);
					if (r < 30 && g < 30 && b < 30)
					{
					//	output.set(i, j, (r + g + b) / 3);
					//	output.set(i, j, 0xFFF0000);
						output.set(i, j, output.get(250, 100));
					}

					output.set(output.width - i, j, output.get(i, j));
				}
			}

			return output;
		}

	private boolean isBackground(int pixel)
	{
		float r = pixel & 0xFF0000 >> 16;
		float g = pixel & 0x00FF00 >> 8;
		float b = pixel & 0x0000FF;
		return r > 80 && g > 80 && b > 80;
	}
}
