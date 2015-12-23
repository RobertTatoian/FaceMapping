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
 * Displays and manages 3 frames: 1. The face texture 2. The webcam input (with
 * rectangles showing detected features) 3. A 3d shape with the face texture
 * mapped onto it
 *
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class Main extends PApplet {

	public static void main(String _args[])
		{
			// Call system to load the OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// Create the Processing window
			PApplet.main(new String[ ] { Main.class.getName() });
		}

	private boolean			debug		= false;
	private DetectedFace	detectedFace;

	private FaceDetector	faceDetector;

	private float			rotation	= 0;
										
	private World			scene;
							
							
	private int				worldX, worldY;
							
							
	@Override
	public void draw( )
		{
			background(150);

			pushMatrix();

			translate(worldX, worldY);

			camera(cos(rotation) * 360, 0, 600f, 0, 0, 0, 0, 1, 0);

			update();

			final PImage frame = faceDetector.getFrame();

			if (debug)
				{
					image(frame, -worldX, -worldY);
				}

			if ((detectedFace != null) && debug)
				{
					final PImage texture = detectedFace.toPImage();

					image(texture, -worldX, -worldY, (200.0f / texture.height) * texture.width, 200);
				}

			scene.draw();

			popMatrix();
		}


	@Override
	public void exitActual( )
		{
			faceDetector.releaseCamera();
		}


	@Override
	public void keyPressed( )
		{
			switch (key)
				{
					case '`':
						debug = !debug;
						break;
					case 'a':
						System.out.println("Left");
						rotation -= 50;
						break;
					case 'd':
						System.out.println("Right");
						rotation += 50;
						break;
					default:
						System.out.println(key);
						break;
				}
		}
		
		
	@Override
	public void settings( )
		{
			size(1024, 768, P3D);
		}


	@Override
	public void setup( )
		{
			faceDetector = new FaceDetector(this);

			surface.setResizable(false);

			scene = new World(this);

			worldX = width / 2;
			worldY = height / 2;

		}
		
		
	// what class should contain this method?
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
