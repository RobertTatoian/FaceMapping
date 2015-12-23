import facemapping.DetectedFace;
import facemapping.FaceDetector;
import org.opencv.core.Core;

import processing.core.PApplet;
import processing.core.PImage;
import scene3D.Cube;
import scene3D.World;

import java.util.ArrayList;
import java.util.List;


/**
 * Displays and manages 3 frames: 1. The face texture 2. The webcam input (with
 * rectangles showing detected features) 3. A 3d shape with the face texture
 * mapped onto it
 *
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class Main extends PApplet {

	private int				worldX, worldY;

	private FaceDetector	faceDetector;
	private DetectedFace	detectedFace;

	private World			scene;

	private boolean			debug		= false;

	private float rotation = PI, elevation = 0f;

	private float centerX, centerY, centerZ;

	private float eyeX = 360, eyeZ = 600;

	public void settings( )
		{
			size(1024, 768, P3D);
		}


	public void setup( )
		{
			faceDetector = new FaceDetector(this);

			surface.setResizable(false);

			// The initialization class does not need to know about the finer
			// details of the program.
			scene = new World(this);

			worldX = width / 2;
			worldY = height / 2;
		}


	public void draw( )
		{
			background(150);

			pushMatrix();

			translate(worldX, worldY);

			// TODO Make the camera rotate around the entire cube.
			camera(eyeX, 0, eyeZ, eyeX + centerX, centerY, eyeZ + centerZ, 0, 1, 0);

			update();
			PImage frame = faceDetector.getFrame();

			if (debug)
				{
					image(frame, -worldX, -worldY);
				}

			if (detectedFace != null && debug)
				{
					PImage texture = detectedFace.toPImage();

					image(texture, -worldX, -worldY, (200.0f / texture.height) * texture.width, 200);
				}

			scene.draw();

			popMatrix();
		}


	public void update( )
		{
			DetectedFace face = faceDetector.detectFace();
			if (face != null)
				{
					detectedFace = face;
				}

			scene.update();
			List <PImage> textures = splitFaceTexture(face);
			for (Cube c : scene.getCollection())
				{
					c.setLeftTexture(textures.get(0));
					c.setFrontTexture(textures.get(1));
					c.setRightTexture(textures.get(2));
				}
		}


	// TODO what class should contain this method?

	/**
	 * Takes the output of a face texture and splits it into three textures for
	 * each side of a cube accounting for the position of the origin in image
	 * coordinates.
	 *
	 * @param face
	 * @return
	 */
	private List <PImage> splitFaceTexture(DetectedFace face)
		{
			List <PImage> textures = new ArrayList <PImage>(3);
			PImage faceTexture = face.toPImage();
			PImage left = new PImage(300, 300);
			PImage front = new PImage(300, 300);
			PImage right = new PImage(300, 300);

			for (int i = 0; i < 300; i++)
				{
					for (int j = 0; j < 300; j++)
						{
							front.set(i, j, faceTexture.get(i + 100, 300 - j));

							if (i < 100)
								{
									left.set(i + 200, 300 - j, faceTexture.get(i, j));
									right.set(i, 300 - j, faceTexture.get(i + 400, j));
								}
						}
				}
			textures.add(left);
			textures.add(front);
			textures.add(right);

			return textures;
		}


	public void keyPressed( )
		{
			switch (key)
				{
					case '`':
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


	@Override
	public void mouseMoved()
		{
			rotation =  (mouseX - width) * 1f / (width / 2f) * HALF_PI;
			elevation = ((mouseY - height / 2f) * 0.5f / height) * HALF_PI;

			centerX = cos(rotation) * cos(elevation);
			centerY = sin(rotation) * sin(elevation);
			centerZ = -cos(elevation);
		}

	public void exitActual( )
		{
			faceDetector.releaseCamera();
		}


	public static void main(String _args[])
		{
			// Call system to load the OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// Create the Processing window
			PApplet.main(new String[ ] { Main.class.getName() });
		}
}
