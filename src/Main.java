import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;

import facemapping.DetectedFace;
import facemapping.FaceDetector;
import processing.core.PApplet;
import processing.core.PImage;
import scene3D.Cube;
import scene3D.World;


/**
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class Main extends PApplet {

	/**
	 * The scale of the world, one pixel equals ten meters.
	 */
	private static float WORLD_SCALE = 10;


	/**
	 * The main function of the application
	 *
	 * @param _args
	 *            Command line arguments
	 */
	public static void main(String _args[])
		{
			// Call system to load the OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// Create the Processing window
			PApplet.main(new String[ ] { Main.class.getName() });
		}

	/**
	 * Center vector of the camera
	 */
	private float			centerX, centerY, centerZ = 0;

	/**
	 * Determines whether we're in debug mode.
	 */
	private boolean			debug		= false;

	/**
	 * Maintains a PImage of the face being detected
	 */
	private DetectedFace	detectedFace;

	/**
	 * Position of the camera
	 */
	private float			eyeX		= 360,
	                                eyeZ = 600;

	/**
	 * A FaceDetector to .. detect faces!
	 */
	private FaceDetector	faceDetector;

	/**
	 * Rotation and elevation (yaw and pitch) of the camera.
	 */
	private float			rotation	= PI,
	                                elevation = 0f;

	/**
	 * The scene
	 */
	private World			scene;

	/**
	 * Allows the conversion between world and pixel coordinates.
	 */
	private float			worldToPixel;

	/**
	 * The origin of the world in pixel coordinates.
	 */
	private int				worldX, worldY;


	/**
	 * The main draw loop of the application
	 */
	@Override
	public void draw( )
		{
			background(150);

			update();
			final PImage frame = faceDetector.getFrame();

			if (debug)
				{
					image(frame, 0, 0, width, height);

					final PImage texture = detectedFace.toPImage();

					pushMatrix();
					translate(0, 0, 1);

					image(texture, 0, 0, (200.0f / texture.height) * texture.width, 200);
					popMatrix();
				}
			else
				{
					pushMatrix();

					translate(worldX, worldY);
					scale(worldToPixel, -worldToPixel);

					camera(eyeX, 0, eyeZ, eyeX + centerX, centerY, eyeZ + centerZ, 0, 1, 0);

					scene.draw();
					popMatrix();


					if (mouseX > width * 2 / 3)
						{
							pushMatrix();
							fill(0, 0, 0);
							textAlign(CENTER, BOTTOM);
							text("Please turn the camera to the left", width / 2, height / 2, 0);
							popMatrix();
						}
				}

		}
		


	/**
	 * Instructs the Processing application that it's time to exit the program.
	 */
	@Override
	public void exitActual( )
		{
			faceDetector.releaseCamera();
			System.exit(0);
		}


	/**
	 * Detects if any keys were pressed in the Processing application
	 */
	@Override
	public void keyPressed( )
		{
			switch (key)
				{
					case '\b':
						debug = !debug;
						break;
					case 'a':
						System.out.println("Left");
						eyeX += 10 * sin(rotation);
						eyeZ += 10 * cos(rotation);
						break;
					case 'd':
						System.out.println("Right");
						eyeX -= 10 * sin(rotation);
						eyeZ += 10 * cos(rotation);
						break;
					case 'w':
						System.out.println("Forward");
						eyeX += 10 * cos(rotation);
						eyeZ += 10 * sin(rotation);
						break;
					case 's':
						System.out.println("Backward");
						eyeX -= 10 * cos(rotation);
						eyeZ -= 10 * sin(rotation);
						break;
					default:
						System.out.println(key);
						break;
				}
		}


	/**
	 * Detects if the mouse was moved in the Processing application
	 */
	@Override
	public void mouseMoved( )
		{
			// Center the camera if the mouse moves out of bounds
			if (mouseY <= 10 || mouseY + 10 >= height || mouseX <= 10 || mouseX + 30 >= width)
				{
					mouseY = height / 2;
					mouseX = width / 2;
				}
			rotation = (((mouseX - width) * 1f) / (width / 2f)) * HALF_PI;
			elevation = (((mouseY - (height / 2f)) * 0.5f) / height) * HALF_PI;

			centerX = cos(rotation) * cos(elevation);
			centerY = sin(rotation) * sin(elevation);
			centerZ = -cos(elevation);
		}


	/**
	 * Called to handle setting the size of the window.
	 */
	@Override
	public void settings( )
		{
			size(1024, 768, P3D);
		}


	/**
	 * Sets up the application
	 */
	@Override
	public void setup( )
		{
			faceDetector = new FaceDetector();

			surface.setResizable(false);

			// The initialization class does not need to know about the finer
			// details of the program.
			scene = new World(this);

			worldX = width / 2;
			worldY = height / 2;

			worldToPixel = (width / 2) / WORLD_SCALE;
		}


	/**
	 * Takes the output of a face texture and splits it into three textures for
	 * each side of a cube accounting for the position of the origin in image
	 * coordinates.
	 *
	 * @param face
	 *            A detected face
	 * @return A list of three square textures containing (in order): the left
	 *         side of the face, the front of the face, and the right side of
	 *         the face.
	 */
	private List <PImage> splitFaceTexture(DetectedFace face)
		{
			final List <PImage> textures = new ArrayList <PImage>(3);
			final PImage faceTexture = face.toPImage();
			final PImage left = new PImage(300, 300);
			final PImage front = new PImage(300, 300);
			final PImage right = new PImage(300, 300);

			for (int i = 0; i < 300; i++)
				{
					for (int j = 0; j < 300; j++)
						{
							front.set(i, j, faceTexture.get(i + 100, 300 - j));

							if (i < 100)
								{
									left.set(i + 200, 300 - j, faceTexture.get(500 - i, j));
									right.set(i, 300 - j, faceTexture.get(i + 400, j));
								}
						}
				}
			textures.add(left);
			textures.add(front);
			textures.add(right);

			return textures;
		}


	/**
	 * The main update function of the application
	 */
	public void update( )
		{
			final DetectedFace face = faceDetector.detectFace();
			if (face != null)
				{
					detectedFace = face;
				}

			scene.update();
			final List <PImage> textures = splitFaceTexture(face);
			for (final Cube c : scene.getCollection())
				{
					c.setLeftTexture(textures.get(0));
					c.setFrontTexture(textures.get(1));
					c.setRightTexture(textures.get(2));
				}
		}
}
