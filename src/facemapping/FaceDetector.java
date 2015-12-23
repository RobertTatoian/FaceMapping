
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
import org.opencv.videoio.Videoio;

import processing.core.PApplet;
import processing.core.PImage;


/**
 * Created by wpgodone on 12/17/2015.
 */
public class FaceDetector {
	
	private int						absoluteFaceSize;
	private final VideoCapture		camera;
	@SuppressWarnings("unused")
	private double					cvHeight;
	@SuppressWarnings("unused")
	private double					cvWidth;
	private final DetectedFace		detectedFace		= new DetectedFace(null);
	private final CascadeClassifier	eyesCascade			= new CascadeClassifier();

	private final String			eyesCascadeName		= "CascadeClassifiers\\haarcascade_eye_tree_eyeglasses.xml";
	private final CascadeClassifier	faceCascade			= new CascadeClassifier();
	private final String			faceCascadeName		= "CascadeClassifiers\\haarcascade_frontalface_alt.xml";
	private final Mat				frame;

	private final CascadeClassifier	mouthCascade		= new CascadeClassifier();

	private final String			mouthCascadeName	= "CascadeClassifiers\\haarcascade_smile.xml";
	private final CascadeClassifier	profileCascade		= new CascadeClassifier();

	private final String			profileCascadeName	= "CascadeClassifiers\\haarcascade_profileface.xml";


	public FaceDetector(PApplet applet)
		{
			this.camera = new VideoCapture();
			this.frame = new Mat();
			faceCascade.load(faceCascadeName);
			eyesCascade.load(eyesCascadeName);
			mouthCascade.load(mouthCascadeName);
			profileCascade.load(profileCascadeName);
			
			initialize();
		}
		
		
	public DetectedFace detectFace( )
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
		
		
	public PImage getFrame( )
		{
			return new PImage(toBufferedImage(frame));
		}
		
		
	private void initialize( )
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
		
		
	public void releaseCamera( )
		{
			try
				{
					camera.release();
					System.exit(0);
				}
			catch (final Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
	/**
	 * The coordinates of the eyes are relative to the detectedFace
	 *
	 * @param greyFaceSubMat
	 * @param detectedFace
	 * @return
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
			final int bufferSize = m.channels() * m.cols() * m.rows();
			final byte[ ] b = new byte[bufferSize];
			m.get(0, 0, b); // get all the pixels
			final BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
			final byte[ ] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			System.arraycopy(b, 0, targetPixels, 0, b.length);
			return image;
		}
}
