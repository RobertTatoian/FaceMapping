
package facemapping;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import processing.core.PConstants;
import processing.core.PImage;


/**
 * Encapsulates the information for a detected face and generates a PImage of
 * the overall face.
 * 
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class DetectedFace {
	
	/**
	 * The height of the texture
	 */
	private static final int	TEXTURE_HEIGHT	= 300;
												
	/**
	 * The part of the face texture containing the front of the face
	 */
	private PImage				frontTexture;
								
	/**
	 * The part of the face texture containing the sides of the face
	 */
	private PImage				profileTexture;
								
	/**
	 * The number of faces added to the texture so far
	 */
	private float				numberFaces		= 0;
												
												
	/**
	 * Instantiates a detected face object.
	 */
	public DetectedFace( )
		{
			this.frontTexture = new PImage((TEXTURE_HEIGHT * 5) / 3, TEXTURE_HEIGHT, PConstants.ARGB);
			this.profileTexture = new PImage((TEXTURE_HEIGHT * 5) / 3, TEXTURE_HEIGHT, PConstants.ARGB);
		}
		
		
	/**
	 * Blends two ARGB pixels together according to the given weight.
	 *
	 * @param oldPixel
	 *            The first pixel value
	 * @param newPixel
	 *            The second pixel value
	 * @param weight
	 *            The weight of the second pixel value, should be in [0, 1]
	 * @return The blended pixel value
	 */
	private int blendPixels(int oldPixel, int newPixel, float weight)
		{
			if ((oldPixel == 0) || (oldPixel == 0xFF000000))
				{
					oldPixel = newPixel;
				}
			int r = (int)((((oldPixel & 0xFF0000) >> 16) * (1 - weight)) + (((newPixel & 0xFF0000) >> 16) * weight));
			int g = (int)((((oldPixel & 0x00FF00) >> 8) * (1 - weight)) + (((newPixel & 0x00FF00) >> 8) * weight));
			int b = (int)(((oldPixel & 0x0000FF) * (1 - weight)) + ((newPixel & 0x0000FF) * weight));
			
			return (0xFF << 24) | (r << 16) | (g << 8) | (b);
		}
		
		
	/**
	 * Converts an OpenCV matrix to a PImage.
	 *
	 * @param m
	 *            A matrix of BGR pixels.
	 * @return A PImage of ARGB pixels with the same dimensions.
	 */
	private PImage matToPImage(Mat m)
		{
			int width = m.width(), height = m.height();
			PImage result = new PImage(width, height, PConstants.ARGB);
			for (int i = 0; i < height; i++)
				{
					for (int j = 0; j < width; j++)
						{
							// i = row = y coordinate, j = column = x coordinate
							double[ ] colorValues = m.get(i, j);
							
							// apparently the colors are bgr, not rgb
							int r = (int)(colorValues[2]), g = (int)(colorValues[1]), b = (int)(colorValues[0]);
							int color = (0xFF << 24) | (r << 16) | (g << 8) | b;
							result.set(j, i, color);
						}
				}
			return result;
		}
		
		
	/**
	 * Helper method that overlays a newly detected face onto the preexisting
	 * texture. The new texture is given more weight than the preexisting
	 * texture. The two textures should have the same dimensions
	 *
	 * @param oldTexture
	 *            The preexisting texture
	 * @param newFace
	 *            A texture to be blended into the old texture.
	 */
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
		
		
	/**
	 * Returns a PImage of the detected face
	 * 
	 * @return A PImage containing the colorized detected face.
	 */
	public PImage toPImage( )
		{
			PImage output = new PImage((TEXTURE_HEIGHT * 5) / 3, TEXTURE_HEIGHT, PConstants.ARGB);
			
			for (int i = 250; i < output.width; i++)
				{
					for (int j = 0; j < output.width; j++)
						{
							float weight = i < 300 ? 1f : 0f;
							if ((300 < i) && (i < 350))
								{
									weight = (350f - i) / 50f;
								}
							output.set(i, j, blendPixels(profileTexture.get(i, j), frontTexture.get(i, j), weight));
							int r = (output.get(i, j) & 0xFF0000) >> 16;
							int g = (output.get(i, j) & 0x00FF00) >> 8;
							int b = (output.get(i, j) & 0x0000FF);
							if ((r < 30) && (g < 30) && (b < 30))
								{
									// output.set(i, j, (r + g + b) / 3);
									output.set(i, j, 0xFF000000);
									// output.set(i, j, output.get(250, 100));
								}
								
							output.set(output.width - i, j, output.get(i, j));
						}
				}
				
			return output;
		}
		
		
	/**
	 * Updates the face texture with a newly detected frontal face.
	 *
	 * @param face
	 *            An OpenCV matrix that contains a frontal face
	 * @param leftEye
	 *            The coordinates of the left eye, {x, y}
	 * @param rightEye
	 *            The coordinates of the right eye, {x, y}
	 */
	public void updateFrontTexture(Mat face, float[ ] leftEye, float[ ] rightEye)
		{
			if ((leftEye == null) || (rightEye == null)) { throw new NullPointerException("One of the eyes is null"); }
			
			// Rotate the face to using the position of the eyes
			double dx = rightEye[0] - leftEye[0];
			double dy = rightEye[1] - leftEye[1];
			double angle = (Math.atan2(dy, dx) * 180) / Math.PI;
			Point center = new Point(face.width() / 2, face.height() / 2);
			
			Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1);
			Mat rotatedFace = new Mat();
			Imgproc.warpAffine(face, rotatedFace, rotMatrix, face.size());
			
			// Translate and scale the image using the position of the eyes
			// convert angle to radians
			angle *= -Math.PI / 180;
			
			double[ ] newLeftEye = new double[ ] {
			        (((leftEye[0] - center.x) * Math.cos(angle)) - ((leftEye[1] - center.y) * Math.sin(angle)))
			                + center.x,
			        ((leftEye[0] - center.x) * Math.sin(angle)) + ((leftEye[1] - center.y) * Math.cos(angle))
			                + center.y };
			double[ ] newRightEye = new double[ ] {
			        (((rightEye[0] - center.x) * Math.cos(angle)) - ((rightEye[1] - center.y) * Math.sin(angle)))
			                + center.x,
			        ((rightEye[0] - center.x) * Math.sin(angle)) + ((rightEye[1] - center.y) * Math.cos(angle))
			                + center.y };
			Point eyeCenter = new Point((newLeftEye[0] + newRightEye[0]) / 2, (newLeftEye[1] + newRightEye[1]) / 2);
			
			double eyeDistance = newRightEye[0] - newLeftEye[0];
			Mat centeredFace = Mat.zeros(new Size(eyeDistance * 5, eyeDistance * 3), rotatedFace.type());
			int rowShift = (int)((((centeredFace.height() - rotatedFace.height()) / 2)
			        + ((rotatedFace.height() * 2.0) / 5.0)) - eyeCenter.y);
			int colShift = (int)((centeredFace.width() / 2) - eyeCenter.x);
			for (int i = 0; i < rotatedFace.rows(); i++)
				{
					for (int j = 0; j < rotatedFace.cols(); j++)
						{
							if (((i + rowShift) >= 0) && ((i + rowShift) < centeredFace.height())
							        && ((j + colShift) >= 0) && ((j + colShift) < centeredFace.width()))
								{
									centeredFace.put(i + rowShift, j + colShift, rotatedFace.get(i, j));
								}
						}
				}
				
			new Point(newLeftEye[0] + colShift, newLeftEye[1] + rowShift);
			new Point(newRightEye[0] + colShift, newRightEye[1] + rowShift);
			
			// Blend in both the newly detected face and its reflection onto the
			// texture
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
		
		
	/**
	 * Updates the face texture with a newly detected side profile.
	 *
	 * @param profile
	 *            An OpenCV matrix containing a side face.
	 * @param eyeCoordinates
	 *            The coordinates of the eye in the profile, {x, y}
	 * @param facingRight
	 *            True if the face is facing right, false otherwise
	 */
	public void updateProfileTexture(Mat profile, float[ ] eyeCoordinates, boolean facingRight)
		{
			// as of now, there is now way to check the rotation of the profile
			double height = profile.size().height;
			
			Mat centeredFace = Mat.zeros(new Size((height * 5) / 3, height), profile.type());
			int rowShift = (int)(((profile.height() * 2.0) / 5.0) - eyeCoordinates[1]);
			int colShift = (int)(profile.width() - eyeCoordinates[0]);
			
			if (!facingRight)
				{
					colShift = centeredFace.width() - colShift - profile.width();
				}
				
			for (int i = 0; i < profile.rows(); i++)
				{
					for (int j = 0; j < profile.cols(); j++)
						{
							if (((i + rowShift) >= 0) && ((i + rowShift) < centeredFace.height())
							        && ((j + colShift) >= 0) && ((j + colShift) < centeredFace.width()))
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
}
