import org.opencv.core.Core;
import processing.core.PApplet;

/**
 * Displays and manages 3 frames:
 * 1. The face texture
 * 2. The webcam input (with rectangles showing detected features)
 * 3. A 3d shape with the face texture mapped onto it
 */
public class Main extends PApplet {

	public void draw()
		{

		}

	public static void main(String _args[])
		{
			// Call system to load the OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// Create the Processing window
			PApplet.main(new String[]{facemapping.FaceMapping.class.getName()});
		}
}
