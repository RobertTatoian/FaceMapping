
package facemapping;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import processing.core.PApplet;
import processing.core.PImage;


public class FaceMapping extends PApplet {

	private VideoCapture	camera				= new VideoCapture();

	Mat						frame				= new Mat();

	private double			cv_width;
	private double			cv_height;
	private int				absoluteFaceSize;
	PImage					img;

	String					face_cascade_name	= "CascadeClassifiers\\haarcascade_frontalface_alt.xml";
	String					eyes_cascade_name	= "CascadeClassifiers\\haarcascade_eye_tree_eyeglasses.xml";

	CascadeClassifier		face_cascade		= new CascadeClassifier();
	CascadeClassifier		eyes_cascade		= new CascadeClassifier();


	public void settings( )
		{
			// Open the first available camera
			camera.open(0);

			if (camera.isOpened())
				{
					cv_width = camera.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
					cv_height = camera.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
					System.out.println("The width of the camera being used is: " + (int)cv_width);
					System.out.println("The height of the camera being used is: " + (int)cv_height);
					size((int)cv_width, (int)cv_height);
				}
			else
				{
					System.err.println("Exiting application: video stream is closed.");
					System.exit(1);
				}

		}


	public void setup( )
		{
			/*
			 * Load the CascadeClassifiers Throw an error and exit if we can't
			 * find the file.
			 */

			// Probably could use some better error handling...
			if (face_cascade.load(face_cascade_name))
				{
					System.out.println("Succeded loading face_cascade_name.xml");
				}
			else
				{
					System.err.println("Error loading face_cascade_name.xml");
					System.err.println("Exiting application: Cannot find face classifer.");
					System.exit(2);
				}

			if (eyes_cascade.load(eyes_cascade_name))
				{
					System.out.println("Succeded loading eyes_cascade_name.xml");
				}
			else
				{
					System.err.println("Error loading eyes_cascade_name.xml");
					System.err.println("Exiting application: Cannot find eyes classifer.");
					System.exit(2);
				}

		}


	public void draw( )
		{

			camera.read(frame); // Read the frame from the camera
			detectAndDisplay(frame); // Detect the face
			img = new PImage(toBufferedImage(frame)); // Convert the frame with
														// the detected face to
														// a PImage
			image(img, 0, 0); // Display that image at (0,0)

		}


	/*
	 * Converts the Mat object to an Image object so that it can be encapsulated
	 * by a PImage to work with processing. Found at:
	 * http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui
	 * All credit to dannyxyz22 at Stack Overflow.
	 */
	public Image toBufferedImage(Mat m)
		{

			int type = BufferedImage.TYPE_BYTE_GRAY;
			if (m.channels() > 1)
				{
					type = BufferedImage.TYPE_3BYTE_BGR;
				}
			int bufferSize = m.channels() * m.cols() * m.rows();
			byte[ ] b = new byte[bufferSize];
			m.get(0, 0, b); // get all the pixels
			BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
			final byte[ ] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			System.arraycopy(b, 0, targetPixels, 0, b.length);
			return image;
		}


	/*
	 * I follow parts of the tutorial found below to get this face detection
	 * code. I'm currently trying to get it to work with the eyes.
	 * http://opencv-java-tutorials.readthedocs.org/en/latest/08%20-%20Face%
	 * 20Recognition%20and%20Tracking.html
	 */
	private void detectAndDisplay(Mat frame)
		{

			// init
			MatOfRect faces = new MatOfRect();
			Mat grayFrame = new Mat();

			// convert the frame in gray scale
			Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
			// equalize the frame histogram to improve the result
			Imgproc.equalizeHist(grayFrame, grayFrame);

			// compute minimum face size (20% of the frame height)
			if (this.absoluteFaceSize == 0)
				{
					int height = grayFrame.rows();
					if (Math.round(height * 0.2f) > 0)
						{
							this.absoluteFaceSize = Math.round(height * 0.2f);
						}
				}

			// detect faces
			try
				{
					this.face_cascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
							new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
				}
			catch (Exception e)
				{
					System.err.println("There is a problem with face_cascade detection. Printing stack trace: ");
					e.printStackTrace();
					System.exit(-1);
				}

			// each rectangle in faces is a face
			Rect[ ] facesArray = faces.toArray();
			for (int i = 0; i < facesArray.length; i++)
				{
					Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 1);

					MatOfRect facesROI = new MatOfRect(facesArray[i]);

					this.eyes_cascade.detectMultiScale(grayFrame, facesROI);

					Rect[ ] eyesArray = facesROI.toArray();

					for (int j = 0; j < eyesArray.length; j++)
						{
							Imgproc.rectangle(frame, eyesArray[i].tl(), eyesArray[i].br(), new Scalar(0, 255, 0, 255),
									1);
						}

				}
		}


	/*
	 * REQUIRED TO RUN BOTH PROCESSING AND OPENCV!
	 */
	public static void main(String _args[])
		{
			// Call system to load the OpenCV library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// Create the Processing window
			PApplet.main(new String[ ] { facemapping.FaceMapping.class.getName() });

		}
}
