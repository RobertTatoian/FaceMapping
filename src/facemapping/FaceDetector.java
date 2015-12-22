package facemapping;

import org.opencv.core.*;
import org.opencv.core.Point;
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
	private String profileCascadeName = "CascadeClassifiers\\haarcascade_profileface.xml";

	private CascadeClassifier faceCascade = new CascadeClassifier();
	private CascadeClassifier	eyesCascade = new CascadeClassifier();
	private CascadeClassifier	mouthCascade = new CascadeClassifier();
	private CascadeClassifier	profileCascade = new CascadeClassifier();

	private int	absoluteFaceSize;

	private double cvWidth;
	private double cvHeight;

	private DetectedFace detectedFace = new DetectedFace(null);

	public FaceDetector(PApplet applet)
		{
			this.camera = new VideoCapture();
			this.frame = new Mat();
			this.applet = applet;

			faceCascade.load(faceCascadeName);
			eyesCascade.load(eyesCascadeName);
			mouthCascade.load(mouthCascadeName);
			profileCascade.load(profileCascadeName);

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
			return detectFace(frame, 0);
		}

	/*
	 * I follow parts of the tutorial found below to get this face detection
	 * code. I'm currently trying to get it to work with the eyes.
	 * http://opencv-java-tutorials.readthedocs.org/en/latest/08%20-%20Face%
	 * 20Recognition%20and%20Tracking.html
	 */
	private DetectedFace detectFace(Mat frame, float angle)
	{
		// Initialize
		MatOfRect faces = new MatOfRect();
		MatOfRect profiles = new MatOfRect();
		boolean facingRight = true;
		Mat grayFrame = new Mat();

		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);

		Mat profileFrame = new Mat();
		grayFrame.copyTo(profileFrame);

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
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2,
																			0 | Objdetect.CASCADE_SCALE_IMAGE,
																			new Size(this.absoluteFaceSize,
																							 this.absoluteFaceSize),
																			new Size());
		this.profileCascade.detectMultiScale(profileFrame, profiles, 1.1, 2,
																				 0 | Objdetect.CASCADE_SCALE_IMAGE,
																				 new Size(this.absoluteFaceSize,
																									this.absoluteFaceSize),
																				 new Size());

		Rect[] profileArray = profiles.toArray();
		if (profileArray.length == 0)
		{
			Core.flip(grayFrame, profileFrame, 1);
			this.profileCascade.detectMultiScale(profileFrame, profiles, 1.1, 2,
																					 0 | Objdetect.CASCADE_SCALE_IMAGE,
																					 new Size(this.absoluteFaceSize,
																										this.absoluteFaceSize),
																					 new Size());
			facingRight = false;
			profileArray = profiles.toArray();

			for (Rect profile : profileArray)
			{
				profile.x = grayFrame.width() - profile.x - profile.width;
			}
		}
		System.out.println(profiles.cols() + " " + profiles.rows());

		// each rectangle in faces is a face
		Rect[] facesArray = faces.toArray();

	/*	if (profileArray.length == 0 && facesArray.length == 0 && angle < 350)
		{
			Mat rotatedFrame = new Mat();
			angle += 30;
			Point center = new Point(frame.width() / 2, frame.height() / 2);
			Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1);
			Imgproc.warpAffine(frame, rotatedFrame, rotMatrix, frame.size());
			return detectFace(rotatedFrame, angle);
		}*/

		if (profileArray.length == 0 && facesArray.length == 1)
		{
			processFrontalFaces(grayFrame, frame, facesArray);
		}
		else if (profileArray.length == 1 && facesArray.length == 0)
		{
			processProfiles(grayFrame, frame, profileArray, facingRight);
		}

		return detectedFace;
	}

	private boolean processFrontalFaces(Mat grayFrame, Mat originalFrame, Rect[] faces)
	{
		float[] leftEye = null, rightEye = null;
		for (Rect face : faces)
		{
			int shift = Math.min(30, face.y);
			shift = Math.min(shift, frame.height() - (face.y + face.height + shift - 10));
			face.y -= shift;
			face.height += shift * 2 - 10;

			/*
			 * Note tl() in the second argument is most likely top left
			 * and br() in the next argument is most likely bottom right
			 */
			Imgproc.rectangle(originalFrame, face.tl(), face.br(), new Scalar(0, 255, 0, 255), 1);

			// The face area that was detected in greyscale
			Mat greyROI = grayFrame.submat(face);

			float[][] eyeCoordinates = searchForEyes(greyROI, face);

			if (eyeCoordinates.length == 2)
			{
				float max = Math.max(eyeCoordinates[0][3], eyeCoordinates[1][3]);
				float min = Math.min(eyeCoordinates[0][3], eyeCoordinates[1][3]);
				if (max / min > 1.5 )
				{
					// If one eye is much bigger than the other, then the eyes weren't properly detected
					continue;
				}

				float x1 = eyeCoordinates[0][0], x2 = eyeCoordinates[1][0];
				leftEye = x1 < x2 ? eyeCoordinates[0] : eyeCoordinates[1];
				rightEye = x1 < x2 ? eyeCoordinates[1] : eyeCoordinates[0];

				if (leftEye[0] + leftEye[3] / 2 > rightEye[0] - rightEye[3])
				{
					// then the eyes overlap, so the eyes weren't properly detected
					continue;
				}

				detectedFace.updateFrontTexture(originalFrame.submat(face), leftEye, rightEye);
				return true;
			}
		}
		return false;
	}

	private boolean processProfiles(Mat grayFrame, Mat originalFrame, Rect[] profiles, boolean facingRight)
	{
		for (Rect profile : profiles)
		{
			Imgproc.rectangle(originalFrame, profile.tl(), profile.br(), new Scalar(0, 0, 255, 255), 1);
			Mat greyROI = grayFrame.submat(profile);
			float[][] eyeCoordinates = searchForEyes(greyROI, profile);
			if (eyeCoordinates.length == 1 && eyeCoordinates[0][0] < profile.width / 2)
			{
				detectedFace.updateProfileTexture(originalFrame.submat(profile), eyeCoordinates[0], facingRight);
				return true;
			}

		}
		return false;
	}

	/**
	 * The coordinates of the eyes are relative to the detectedFace
	 * @param greyFaceSubMat
	 * @param detectedFace
	 * @return
	 */
	private float[][] searchForEyes(Mat greyFaceSubMat, Rect detectedFace)
	{
		MatOfRect eyes = new MatOfRect();
		eyesCascade.detectMultiScale(greyFaceSubMat, eyes);
		Rect[ ] eyesArray = eyes.toArray();

		float[][] eyeCoordinates = new float[eyesArray.length][];

		for (int j = 0; j < eyesArray.length; j++)
		{
			Rect e = eyesArray[j];
			e.x += detectedFace.x;
			e.y += detectedFace.y;
			Size eyeSize = e.size();

			eyeCoordinates[j] = new float[] {
							(float)(e.x  - detectedFace.x + eyeSize.width / 2),
							(float)(e.y - detectedFace.y + eyeSize.height / 2),
							(float) eyeSize.width,
							(float) eyeSize.height };

			Imgproc.rectangle(frame, e.tl(), e.br(), new Scalar(255, 0, 0, 255), 1);
		}
		return eyeCoordinates;
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
