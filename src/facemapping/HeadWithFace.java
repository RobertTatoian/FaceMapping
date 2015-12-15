package facemapping;

import processing.core.PApplet;
import processing.core.PImage;

public class HeadWithFace /* extends SimpleGraphicsObject */ {

	/** size of the texture **/
	private float width, height;

	/**
	 * Height/width of the eyes from the bottom of the texture as a percentage of the
	 * texture's height.
	 */
	private float eyeHeight;

	private PApplet applet;

	private PImage baseTexture;
	private PImage detectedFaceTexture;
	private PImage overallTexture;


	public HeadWithFace(PApplet applet)
		{
			this.applet = applet;
		}


	public void draw()
		{
			applet.fill(0, 100, 100);
			applet.stroke(1f);

			applet.beginShape(PApplet.QUADS);
			// TODO texture mapping

			// Top side
			applet.vertex( 1f, 1f, -1f);
			applet.vertex(-1f, 1f, -1f);
			applet.vertex(-1f, 1f,  1f);
			applet.vertex( 1f, 1f,  1f);

			// Bottom side
			applet.vertex( 1f, -1f,  1f);
			applet.vertex(-1f, -1f,  1f);
			applet.vertex(-1f, -1f, -1f);
			applet.vertex( 1f, -1f, -1f);

			// Front side
			applet.vertex( 1f,  1f, 1f);
			applet.vertex(-1f,  1f, 1f);
			applet.vertex(-1f, -1f, 1f);
			applet.vertex( 1f, -1f, 1f);

			// Back side
			applet.vertex( 1f, -1f, -1f);
			applet.vertex(-1f, -1f, -1f);
			applet.vertex(-1f,  1f, -1f);
			applet.vertex( 1f,  1f, -1f);

			// Left side
			applet.vertex(-1f,  1f,  1f);
			applet.vertex(-1f,  1f, -1f);
			applet.vertex(-1f, -1f, -1f);
			applet.vertex(-1f, -1f,  1f);

			// Right side
			applet.vertex(1f,  1f, -1f);
			applet.vertex(1f,  1f,  1f);
			applet.vertex(1f, -1f,  1f);
			applet.vertex(1f, -1f, -1f);

			applet.endShape();
		}
}
