package facemapping;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by wpgodone on 12/17/2015.
 */
public class FaceDetector {

	private VideoCapture camera;
	private Mat frame;
	private PApplet applet;

	private String faceCascadeName = "CascadeClassifiers\\haarcascade_frontalface_alt.xml";
	private String eyesCascadeName = "CascadeClassifiers\\haarcascade_eye_tree_eyeglasses.xml";
	private String mouthCascadeName = "CascadeClassifiers\\haarcascade_smile.xml";

	private CascadeClassifier faceCascade = new CascadeClassifier();
	private CascadeClassifier	eyesCascade = new CascadeClassifier();
	private CascadeClassifier	mouthCascade = new CascadeClassifier();

	private int	absoluteFaceSize;

	private double cvWidth;
	private double cvHeight;

	public FaceDetector(PApplet applet)
		{
			this.camera = new VideoCapture();
			this.frame = new Mat();
			this.applet = applet;

			faceCascade.load(faceCascadeName);
			eyesCascade.load(eyesCascadeName);
			mouthCascade.load(mouthCascadeName);

			initialize();
		}

	private void initialize()
		{
			// Open the first available camera
			camera.open(0);

				if (camera.isOpened())
				{
					cvWidth = camera.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
					cvHeight = camera.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
				}
			else
				{
					System.err.println("Exiting application: video stream is closed.");
					System.exit(1);
				}
		}

	public DetectedFace detectFace()
		{
			camera.read(frame);
			return detectFace(frame);
		}

	/*
	 * I follow parts of the tutorial found below to get this face detection
	 * code. I'm currently trying to get it to work with the eyes.
	 * http://opencv-java-tutorials.readthedocs.org/en/latest/08%20-%20Face%
	 * 20Recognition%20and%20Tracking.html
	 */
	private DetectedFace detectFace(Mat frame)
	{
		// Initialize
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		DetectedFace detectedFace = null;

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
			this.faceCascade.detectMultiScale(grayFrame,
																				faces,
																				1.1,
																				2,
																				0 | Objdetect.CASCADE_SCALE_IMAGE,
																				new Size(this.absoluteFaceSize,
																								 this.absoluteFaceSize),
																				new Size());
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
			/*
			 * Note tl() in the second argument is most likely top left
			 * and br() in the next argument is most likely bottom right
			 */
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 1);

			// The face area that was detected in greyscale
			Mat greyROI = grayFrame.submat(facesArray[i]);
			detectedFace = new DetectedFace(frame.submat(facesArray[i]));

			// The rectangular bounds on the face area
			MatOfRect facesROI = new MatOfRect(facesArray[i]);

			Imgcodecs.imwrite("Captured Images//Faces//FaceROI_" + i + ".png",
												greyROI);

			searchForEyes(greyROI, facesArray[i]);
			searchForMouth(greyROI, facesROI, facesArray[i]);
		}

		return detectedFace;
	}

	private void searchForEyes(Mat greyFaceSubMat, Rect detectedFace)
	{

		MatOfRect eyes = new MatOfRect();
		eyesCascade.detectMultiScale(greyFaceSubMat, eyes);
		Rect[ ] eyesArray = eyes.toArray();

		for (int j = 0; j < eyesArray.length; j++)
		{
			Rect e = eyesArray[j];
			e.x += detectedFace.x;
			e.y += detectedFace.y;

			Imgproc.rectangle(frame, e.tl(), e.br(), new Scalar(255, 0, 0, 255), 1);
		}
	}


	private void searchForMouth(Mat greyFaceSubMat, MatOfRect rectOfFace, Rect detectedFaceRect)
	{
		mouthCascade.detectMultiScale(greyFaceSubMat, rectOfFace, 1.5, 3, 0, new Size(50d, 50d),
																	 detectedFaceRect.size());

		Rect[ ] mouthRect = rectOfFace.toArray();

		for (int j = 0; j < mouthRect.length; j++)
		{
			Imgcodecs.imwrite("Captured Images//Mouths//Mouth ROI_" + j + ".png", greyFaceSubMat);

			mouthRect[j].x += detectedFaceRect.x;
			mouthRect[j].y += detectedFaceRect.y;

			Imgproc.rectangle(frame, mouthRect[j].tl(), mouthRect[j].br(), new Scalar(255, 255, 0, 255), 1);
		}
	}

	public PImage getFrame()
	{
		return new PImage(toBufferedImage(frame));
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
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public void releaseCamera()
	{
		try
		{
			camera.release();
			System.exit(0);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
