package facemapping;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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
			Imgproc.warpAffine(face, rotatedFace, rotMatrix, new Size(face.width(), face.height()));

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
			PImage front = matToPImage(centeredFace);
			float scale = (TEXTURE_HEIGHT + 0.0f) / centeredFace.height();
			front.resize((int)(front.width * scale), (int)(front.height * scale));

			// TODO blend it into the texture
			overlayFace(frontTexture, front);
		}

	public void updateProfileTexture(Mat profile, float[] eyeCoordinates)
		{
			// as of now, there is now way to check the rotation of the profile
			double height = profile.size().height;

			Mat centeredFace = Mat.zeros(new Size(height * 5 / 3, height), profile.type());
			int rowShift = (int)(profile.height() * 2.0 / 5.0 - eyeCoordinates[1]);
			int colShift = (int)(profile.width() - eyeCoordinates[0]);
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

			PImage front = matToPImage(centeredFace);
			float scale = (TEXTURE_HEIGHT + 0.0f) / centeredFace.height();
			front.resize((int)(front.width * scale), (int)(front.height * scale));

			// TODO blend it into the texture
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

	private PImage getConvolution(PImage img, float[][] kernel)
	{
		PImage output = img.copy();
		int center = kernel.length / 2;

		for (int x = center; x < output.width - center; x++)
		{
			for (int y = center; y < output.height - center; y++)
			{
				float acc = 0; // accumulator
				for (int i = 0; i < kernel.length; i++)
				{
					for (int j = 0; j < kernel[i].length; j++)
					{
						int red = (img.get(x + i - center, y + j - center) & 0xFF);
						acc += kernel[i][j] * red * .25f;
						if (acc > 255) {
							System.out.println(acc + " " + i + " " + j);
						}
					}
				}
				if (acc > 255) {
					acc = 255;
				}
				int sum = (int)acc;
				output.set(x, y, (0xFF << 24) | (sum << 16) | (sum << 8) | sum);
			}
		}
		return output;
	}

	private PImage detectEdges(PImage img)
	{
		float[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
		float[][] kernelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
		PImage gx = getConvolution(img, kernelX);
		PImage gy = getConvolution(img, kernelY);

		PImage output = img.copy();
		for (int x = 0; x < output.width; x++)
		{
			for (int y = 0; y < output.width; y++)
			{
				int acc = (int)(Math.sqrt(gx.get(x, y) * gx.get(x, y) + gy.get(x, y) * gy.get(x, y)));
				if (acc > 255) {
					acc = 0;
				}
				output.set(x, y, (0xFF << 24) | (acc << 16) | (acc << 8) | acc);
			}
		}
		output.updatePixels();
		return output;
	}

	public PImage toPImage()
		{
			PImage output = new PImage((TEXTURE_HEIGHT * 5) / 3,  TEXTURE_HEIGHT, PImage.ARGB);
			PImage frontBlur = frontTexture.copy();
			PImage frontGrad = frontTexture.copy();
			PImage profileThreshold = profileTexture.copy();
			frontBlur.filter(PConstants.GRAY);
			frontBlur.filter(PConstants.BLUR);
			frontBlur.filter(PConstants.BLUR);
			profileThreshold.filter(PConstants.THRESHOLD);

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
