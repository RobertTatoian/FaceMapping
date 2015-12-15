package facemapping;

import org.opencv.core.Mat;
import processing.core.PImage;

public class FaceTexture /* extends SimpleGraphicsObject */ {

	/** size of the texture **/
	private float width, height;

	/**
	 * Height/width of the eyes from the bottom of the texture as a percentage of the
	 * texture's height.
	 */
	private float eyeHeight;



	public FaceTexture(Mat frontalFace)
		{
			// TODO
		}


	/**
	 * Converts the texture to a PImage
	 */
	public PImage toPImage()
		{
			// TODO implement
			return null;
		}
}
