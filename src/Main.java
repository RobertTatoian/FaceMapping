import facemapping.FaceDetector;
import org.opencv.core.Core;
import processing.core.PApplet;

/**
 * Displays and manages 3 frames:
 * 1. The face texture
 * 2. The webcam input (with rectangles showing detected features)
 * 3. A 3d shape with the face texture mapped onto it
 */
public class Main extends PApplet {
	FaceDetector faceDetector;

	public void settings()
		{
			size(640, 480, P3D);
		}

	public void setup()
		{
			faceDetector = new FaceDetector(this);
		}

	public void draw()
		{
			update();

			image(faceDetector.getFrame(), 0, 0);
		}

	public void update()
		{
			faceDetector.detectFace();
		}

	public void exitActual()
		{
			super.exitActual();
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
