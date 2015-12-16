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
			applet.pushMatrix();
			applet.scale(100f, -100f, 100f);

			applet.stroke(1f);
			applet.strokeWeight(.01f);
		//	applet.rect(0, 0, 10, 10);

			applet.beginShape(PApplet.QUADS);
			// TODO texture mapping

			// I think I have the coordinates mixed up

			// Floor
			applet.fill(100, 200, 0);
			applet.vertex( 10f, 10f, 10f);
			applet.vertex(-10f, 10f, 10f);
			applet.vertex(-10f, 10f, -10f);
			applet.vertex( 10f, 10f, -10f);

			// BEGIN CUBE
			applet.fill(0, 100, 100);

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

			applet.fill(100, 0, 0);

			// Front side
			applet.vertex( 1f,  1f, 1f);
			applet.vertex(-1f,  1f, 1f);
			applet.vertex(-1f, -1f, 1f);
			applet.vertex( 1f, -1f, 1f);

			applet.fill(0, 100, 100);

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
			applet.popMatrix();
		}
}
