import facemapping.DetectedFace;
import facemapping.FaceDetector;
import org.opencv.core.Core;

import com.jogamp.newt.event.KeyEvent;

import processing.core.PApplet;
import processing.core.PImage;
import scene3D.Cube;
import scene3D.World;


/**
 * Displays and manages 3 frames: 1. The face texture 2. The webcam input (with
 * rectangles showing detected features) 3. A 3d shape with the face texture
 * mapped onto it
 */
public class Main extends PApplet {
	
	private int				worldX, worldY;
							
	private FaceDetector	faceDetector;
	private DetectedFace	detectedFace;
	private Cube			cube;
							
	private World			scene;
							
	float					cubeAngle	= 3.14f;
	private boolean			debug		= false;
										
	private float			rotation	= 0;
										
										
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
			
			//TODO Make the camera rotate around the entire cube.
			camera(cos(rotation) * 360, 0, 600f, 0, 0, 0, 0, 1, 0);
			
			update();
			
			PImage frame = faceDetector.getFrame();
			
			if (debug)
				{
					
					image(frame, -worldX, -worldY);
					
				}
				
			if (detectedFace != null)
				{
					PImage texture = detectedFace.toPImage();
					
					if (debug)
						{
							
							image(texture, -worldX, -worldY, (200.0f / texture.height) * texture.width, 200);
							
						}
				}
			// cube.draw();
			
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
			//
			// cubeAngle += 360 * 2;
			//
			// cube = new Cube(width * 0.75f, width / 4, 200, 20,
			// cubeAngle, 0, 0, this, null);
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
