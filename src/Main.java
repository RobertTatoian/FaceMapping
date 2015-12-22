import facemapping.DetectedFace;
import facemapping.FaceDetector;
import org.opencv.core.Core;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Displays and manages 3 frames:
 * 1. The face texture
 * 2. The webcam input (with rectangles showing detected features)
 * 3. A 3d shape with the face texture mapped onto it
 */
public class Main extends PApplet {
	private FaceDetector faceDetector;
	private DetectedFace detectedFace;

	public void settings()
		{
			size(640, 480, P3D);
			surface.setResizable(false);

			camera(640f, 480f, -600f, width / 2, height / 2, 0, 0, 1, 0);
		}

	public void setup()
		{
			faceDetector = new FaceDetector(this);
		}

	public void draw()
		{
			update();

			image(faceDetector.getFrame(), 0, 0);

			if (detectedFace != null)
			{
				PImage texture = detectedFace.toPImage();
				image(texture, 0, 0, (200.0f / texture.height) * texture.width, 200);
			}
		}

	public void update()
		{
			DetectedFace face = faceDetector.detectFace();
			if (face != null)
			{
				detectedFace = face;
			}
		}

	public void exitActual()
		{
			faceDetector.releaseCamera();
		}

	public static void main(String _args[])
		{
			// Call system to load the OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// Create the Processing window
			PApplet.main(new String[]{ Main.class.getName() });
		}
}
