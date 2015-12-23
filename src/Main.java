
import java.util.ArrayList;
import java.util.List;


import com.sun.webkit.dom.HTMLBRElementImpl;
import facemapping.DetectedFace;
import facemapping.FaceDetector;

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

	private World			scene;
							
							
	private int				worldX, worldY;
							
	private float rotation = PI, elevation = 0f;

	private float centerX, centerY, centerZ;

	private float eyeX = 360, eyeZ = 600;

	public void settings( )
		{
			size(1024, 768, P3D);
		}
		
		
	public void setup( )
		{
			noCursor();

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


			camera(cos(rotation) * 360, 0, 600f, 0, 0, 0, 0, 1, 0);

			
			// TODO Make the camera rotate around the entire cube.
			camera(eyeX, 0, eyeZ, eyeX + centerX, centerY, eyeZ + centerZ, 0, 1, 0);


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


	@Override
	public void mouseMoved()
		{
			rotation = TWO_PI - (mouseX * 1.f / width) * TWO_PI;
			elevation = ((mouseY - height / 2f) * 0.5f / height) * HALF_PI;

			centerX = cos(rotation) * cos(elevation);
			centerY = sin(rotation) * sin(elevation);
			centerZ = -cos(elevation);
		}

		
}
