
package facemapping;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import processing.core.PImage;


/**
 * Detects the Face and subcomponents that make up a face.
 * 
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class FaceDetector {
	
	/**
	 * The maximum face size to be detected (in pixels).
	 */
	private int						absoluteFaceSize;
									
	/**
	 * Captures input from the webcam
	 */
	private final VideoCapture		camera;
									
	/**
	 * Maintains a PImage of the overall face that has been detected so far.
	 */
	private final DetectedFace		detectedFace		= new DetectedFace();
														
	/**
	 * An OpenCV Cascade Classifier object for detecting the eyes.
	 */
	private final CascadeClassifier	eyesCascade			= new CascadeClassifier();
														
	/**
	 * The trained Haar cascade for eye detection
	 */
	private final String			eyesCascadeName		= "CascadeClassifiers\\haarcascade_eye_tree_eyeglasses.xml";
														
	/**
	 * An OpenCV Cascade Classifier object for detecting the face.
	 */
	private final CascadeClassifier	faceCascade			= new CascadeClassifier();
														
	/**
	 * The trained Haar cascade for face detection
	 */
	private final String			faceCascadeName		= "CascadeClassifiers\\haarcascade_frontalface_alt.xml";
														
	/**
	 * The current frame obtained from the webcam
	 */
	private final Mat				frame;
									
	/**
	 * An OpenCV Cascade Classifier object for detecting the mouth.
	 */
	private final CascadeClassifier	mouthCascade		= new CascadeClassifier();
														
	/**
	 * The trained Haar cascade for mouth detection
	 */
	private final String			mouthCascadeName	= "CascadeClassifiers\\haarcascade_smile.xml";
														
	/**
	 * An OpenCV Cascade Classifier object for detecting the profile of a face.
	 */
	private final CascadeClassifier	profileCascade		= new CascadeClassifier();
														
	/**
	 * The trained Haar cascade for profile detection
	 */
	private final String			profileCascadeName	= "CascadeClassifiers\\haarcascade_profileface.xml";
														
														
	/**
	 * Instantiates the face detector object. It will perform several IO
	 * operations and get input from your webcam.
	 */
	public FaceDetector( )
		{
			this.camera = new VideoCapture();
			this.frame = new Mat();
			faceCascade.load(faceCascadeName);
			eyesCascade.load(eyesCascadeName);
			mouthCascade.load(mouthCascadeName);
			profileCascade.load(profileCascadeName);
			
			initialize();
		}
		
		
	/**
	 * Detects a face in the current webcam input.
	 *
	 * @return A reference to the internal DetectedFace object.
	 */
	public DetectedFace detectFace( )
		{
			camera.read(frame);
			return detectFace(frame);
		}
		
		
	/**
	 * Detects a face in the given frame.
	 *
	 * @param frame
	 *            An OpenCV matrix to be checked.
	 * @return A reference to the internal DetectedFace object.
	 */
	private DetectedFace detectFace(Mat frame)
		{
			// Initialize
			final MatOfRect faces = new MatOfRect();
			final MatOfRect profiles = new MatOfRect();
			boolean facingRight = true;
			final Mat grayFrame = new Mat();
			
			// convert the frame in gray scale
			Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
			// equalize the frame histogram to improve the result
			Imgproc.equalizeHist(grayFrame, grayFrame);
			
			final Mat profileFrame = new Mat();
			grayFrame.copyTo(profileFrame);
			
			// compute minimum face size (20% of the frame height)
			if (this.absoluteFaceSize == 0)
				{
					final int height = grayFrame.rows();
					if (Math.round(height * 0.2f) > 0)
						{
							this.absoluteFaceSize = Math.round(height * 0.2f);
						}
				}
				
			// detect faces
			this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
			        new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
			this.profileCascade.detectMultiScale(profileFrame, profiles, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
			        new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
					
			Rect[ ] profileArray = profiles.toArray();
			if (profileArray.length == 0)
				{
					Core.flip(grayFrame, profileFrame, 1);
					this.profileCascade.detectMultiScale(profileFrame, profiles, 1.1, 2,
					        0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize),
					        new Size());
					facingRight = false;
					profileArray = profiles.toArray();
					
					for (final Rect profile : profileArray)
						{
							profile.x = grayFrame.width() - profile.x - profile.width;
						}
				}
				
			// each rectangle in faces is a face
			final Rect[ ] facesArray = faces.toArray();
			
			if ((profileArray.length == 0) && (facesArray.length == 1))
				{
					processFrontalFaces(grayFrame, frame, facesArray);
				}
			else
				if ((profileArray.length == 1) && (facesArray.length == 0))
					{
						processProfiles(grayFrame, frame, profileArray, facingRight);
					}
					
			return detectedFace;
		}
		
		
	/**
	 * Returns the current camera frame as a PImage
	 *
	 * @return A PImage of the current frame
	 */
	public PImage getFrame( )
		{
			return new PImage(toBufferedImage(frame));
		}
		
		
	/**
	 * Initializes the camera and other internal variables.
	 */
	private void initialize( )
		{
			// Open the first available camera
			camera.open(0);
			
			if (!camera.isOpened())
				{
					System.err.println("Exiting application: video stream is closed.");
					System.exit(1);
				}
		}
		
		
	/**
	 * Updates the DetectedFace's texture with a frontal face if possible.
	 *
	 * @param grayFrame
	 *            The frame in grayscale
	 * @param originalFrame
	 *            The frame in color
	 * @param faces
	 *            An array of the bounds of all faces that have been detected
	 * @return True if the texture has been updated, false otherwise.
	 */
	private boolean processFrontalFaces(Mat grayFrame, Mat originalFrame, Rect[ ] faces)
		{
			float[ ] leftEye = null, rightEye = null;
			for (final Rect face : faces)
				{
					int shift = Math.min(30, face.y);
					shift = Math.min(shift, frame.height() - ((face.y + face.height + shift) - 10));
					face.y -= shift;
					face.height += (shift * 2) - 10;
					
					/*
					 * Note tl() in the second argument is most likely top left
					 * and br() in the next argument is most likely bottom right
					 */
					Imgproc.rectangle(originalFrame, face.tl(), face.br(), new Scalar(0, 255, 0, 255), 1);
					
					// The face area that was detected in greyscale
					final Mat greyROI = grayFrame.submat(face);
					
					final float[ ][ ] eyeCoordinates = searchForEyes(greyROI, face);
					
					if (eyeCoordinates.length == 2)
						{
							final float max = Math.max(eyeCoordinates[0][3], eyeCoordinates[1][3]);
							final float min = Math.min(eyeCoordinates[0][3], eyeCoordinates[1][3]);
							if ((max / min) > 1.5)
								{
									// If one eye is much bigger than the other,
									// then the eyes weren't properly detected
									continue;
								}
								
							final float x1 = eyeCoordinates[0][0], x2 = eyeCoordinates[1][0];
							leftEye = x1 < x2 ? eyeCoordinates[0] : eyeCoordinates[1];
							rightEye = x1 < x2 ? eyeCoordinates[1] : eyeCoordinates[0];
							
							if ((leftEye[0] + (leftEye[3] / 2)) > (rightEye[0] - rightEye[3]))
								{
									// then the eyes overlap, so the eyes
									// weren't properly detected
									continue;
								}
								
							detectedFace.updateFrontTexture(originalFrame.submat(face), leftEye, rightEye);
							return true;
						}
				}
			return false;
		}
		
		
	/**
	 * Updates the DetectedFace's texture with side profiles if possible.
	 *
	 * @param grayFrame
	 *            The frame in grayscale
	 * @param originalFrame
	 *            The frame in color
	 * @param profiles
	 *            An array of the bounds of all profiles that have been
	 *            detected.
	 * @param facingRight
	 *            True if the profiles are facing right, false otherwise
	 * @return True if the texture has been updated, false otherwise.
	 */
	private boolean processProfiles(Mat grayFrame, Mat originalFrame, Rect[ ] profiles, boolean facingRight)
		{
			for (final Rect profile : profiles)
				{
					Imgproc.rectangle(originalFrame, profile.tl(), profile.br(), new Scalar(0, 0, 255, 255), 1);
					final Mat greyROI = grayFrame.submat(profile);
					final float[ ][ ] eyeCoordinates = searchForEyes(greyROI, profile);
					if ((eyeCoordinates.length == 1) && (eyeCoordinates[0][0] < (profile.width / 2)))
						{
							detectedFace.updateProfileTexture(originalFrame.submat(profile), eyeCoordinates[0],
							        facingRight);
							return true;
						}
						
				}
			return false;
		}
		
		
	/**
	 * Releases the webcam which was being used to detect faces.
	 */
	public void releaseCamera( )
		{
			try
				{
					camera.release();
				}
			catch (final Exception e)
				{
					e.printStackTrace();
				}
		}
		
		
	/**
	 * Searches for the eyes in a detected face.
	 *
	 * @param greyFaceSubMat
	 *            The OpenCV matrix containing the grayscale submatrix
	 * @param detectedFace
	 *            The OpenCV matrix containing the detected face
	 * @return An array of the coordinates of all eyes in the detected face
	 *         (relative the the detected face) in the format of {x, y, width,
	 *         height}
	 */
	private float[ ][ ] searchForEyes(Mat greyFaceSubMat, Rect detectedFace)
		{
			final MatOfRect eyes = new MatOfRect();
			eyesCascade.detectMultiScale(greyFaceSubMat, eyes);
			final Rect[ ] eyesArray = eyes.toArray();
			
			final float[ ][ ] eyeCoordinates = new float[eyesArray.length][ ];
			
			for (int j = 0; j < eyesArray.length; j++)
				{
					final Rect e = eyesArray[j];
					e.x += detectedFace.x;
					e.y += detectedFace.y;
					final Size eyeSize = e.size();
					
					eyeCoordinates[j] = new float[ ] { (float)((e.x - detectedFace.x) + (eyeSize.width / 2)),
					        (float)((e.y - detectedFace.y) + (eyeSize.height / 2)), (float)eyeSize.width,
					        (float)eyeSize.height };
							
					Imgproc.rectangle(frame, e.tl(), e.br(), new Scalar(255, 0, 0, 255), 1);
				}
			return eyeCoordinates;
		}
		
		
	/**
	 * Converts the Mat object to an Image object so that it can be encapsulated
	 * by a PImage to work with processing. Found at:
	 * http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui
	 * All credit to dannyxyz22 at Stack Overflow.
	 *
	 * @param m
	 *            An OpenCV Matrix object that contains the current frame
	 * @return An image object containing the OpenCV matrix
	 */
	public Image toBufferedImage(Mat m)
		{
			
			int type = BufferedImage.TYPE_BYTE_GRAY;
			
			if (m.channels() > 1)
				{
					type = BufferedImage.TYPE_3BYTE_BGR;
				}
				
			final int bufferSize = m.channels() * m.cols() * m.rows();
			
			final byte[ ] b = new byte[bufferSize];
			
			m.get(0, 0, b); // get all the pixels
			
			final BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
			
			final byte[ ] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			
			System.arraycopy(b, 0, targetPixels, 0, b.length);
			
			return image;
		}
}
