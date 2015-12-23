
package scene3D;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PVector;
import scene3Dabstract.GraphicObject3D;
import scene3Dabstract.SimpleGraphicObject3D;


/**
 * The cube object that is displayed in the 3D world.
 *
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class Cube extends SimpleGraphicObject3D {

	/**
	 * The absolute bounding box for a cube object
	 */
	private BoundingBox3D		absoluteBoundingBox;
	/**
	 * The reference to the main application
	 */
	private final PApplet		applet;

	/**
	 * The default color for each cube in hexadecimal
	 */
	private int					color		= 0xFF00A0A0;

	/**
	 * The image object for each side of the cube
	 */
	private PImage				frontTexture, leftTexture, rightTexture;

	/**
	 * Determines if this cube object is the bounding cube for all the other
	 * cubes
	 */
	private boolean				isBounding	= true;

	/**
	 * The relative bounding box for each cube
	 */
	private final BoundingBox3D	relativeBoundingBox;

	/**
	 * The rotation of each axis for the cube object
	 */
	private float				rotationX, rotationY, rotationZ;

	/**
	 * The size of the cube
	 */
	private final float			size;

	/**
	 * The translation of each axis for the cube object
	 */
	private float				translateX, translateY, translateZ;

	/**
	 * How fast the cube rotates in the X direction
	 */
	private float				xRotationalVelocity;

	/**
	 * How fast the cube translates in the X direction
	 */
	private float				xTranslationalVelocity;

	/**
	 * How fast the cube rotates in the Y direction
	 */
	private float				yRotationalVelocity;

	/**
	 * How fast the cube translates in the Y direction
	 */
	private float				yTranslationalVelocity;

	/**
	 * How fast the cube rotates in the Z direction
	 */
	private float				zRotationalVelocity;

	/**
	 * How fast the cube translates in the Z direction
	 */
	private float				zTranslationalVelocity;


	/**
	 * A more detailed constructor for the cube object, create a cube to the
	 * specified size and places it at a specific position and rotation.
	 *
	 * @param x
	 *            The X coordinate of the cube
	 * @param y
	 *            The Y coordinate of the cube
	 * @param z
	 *            The Z coordinate of the cube
	 * @param size
	 *            The size of the cube
	 * @param rotX
	 *            The rotation in the X direction of the cube
	 * @param rotY
	 *            The rotation in the Y direction of the cube
	 * @param rotZ
	 *            The rotation in the Z direction of the cube
	 * @param applet
	 *            The reference to the processing applet
	 */
	public Cube(float x, float y, float z, float size, float rotX, float rotY, float rotZ, PApplet applet)
		{
			this.applet = applet;

			translateX = x;
			translateY = y;
			translateZ = z;

			rotationX = 0;// rotX;
			rotationY = 0;// rotY;
			rotationZ = 0;// rotZ;

			this.size = size;

			relativeBoundingBox = new BoundingBox3D(x, y, z, size, size, size);
			computeAbsoluteBoundingBox();

			final float translationalLimit = 5f;
			xTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			yTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			zTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);

			final float rotationalLimit = 0.1f;
			xRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
			yRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
			zRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
		}


	/**
	 * Creates a cube object at the specified position and of a specified size.
	 *
	 * @param x
	 *            The X coordinate of the cube
	 * @param y
	 *            The Y coordinate of the cube
	 * @param z
	 *            The Z coordinate of the cube
	 * @param size
	 *            The size of the cube
	 * @param applet
	 *            The reference to the processing applet
	 */
	public Cube(float x, float y, float z, float size, PApplet applet)
		{
			this(x, y, z, size, 0, 0, 0, applet);
		}


	/**
	 * Computes the absolute bounding box of the cube object
	 */
	private void computeAbsoluteBoundingBox( )
		{
			final List <PVector> corners = getVertices();
			float xMin = Float.POSITIVE_INFINITY, yMin = Float.POSITIVE_INFINITY, zMin = Float.POSITIVE_INFINITY;
			float xMax = Float.NEGATIVE_INFINITY, yMax = Float.NEGATIVE_INFINITY, zMax = Float.NEGATIVE_INFINITY;

			for (final PVector corner : corners)
				{
					xMax = Math.max(xMax, corner.x);
					yMax = Math.max(yMax, corner.y);
					zMax = Math.max(zMax, corner.z);

					xMin = Math.min(xMin, corner.x);
					yMin = Math.min(yMin, corner.y);
					zMin = Math.min(zMin, corner.z);
				}

			absoluteBoundingBox = new BoundingBox3D(xMin, yMin, zMin, xMax - xMin, yMax - yMin, zMax - zMin);
		}


	/**
	 * Checks to see if two cubes are disjoint along an axis
	 *
	 * @param myVertices
	 *            A list of vertices of this cube object
	 * @param otherVertices
	 *            A list of vertices of another cube object
	 * @param axis
	 *            The axis of the cube object
	 * @return True if the total span is greater than the total length
	 */
	private boolean disjointAlongAxis(List <PVector> myVertices, List <PVector> otherVertices, PVector axis)
		{
			if ((axis.x == 0) && (axis.y == 0) && (axis.z == 0)) { return false; }

			float myMax = Float.NEGATIVE_INFINITY, myMin = Float.POSITIVE_INFINITY;
			float cubeMax = Float.NEGATIVE_INFINITY, cubeMin = Float.POSITIVE_INFINITY;

			// Find the max and min coordinate of each cube along the axis by
			// projecting each corner onto the axis
			for (final PVector corner : myVertices)
				{
					final float dist = corner.dot(axis) / axis.mag();
					myMax = Math.max(myMax, dist);
					myMin = Math.min(myMin, dist);
				}

			for (final PVector corner : otherVertices)
				{
					final float dist = corner.dot(axis) / axis.mag();
					cubeMax = Math.max(cubeMax, dist);
					cubeMin = Math.min(cubeMin, dist);
				}

			final float totalLength = (myMax - myMin) + (cubeMax - cubeMin);
			final float totalSpan = Math.max(myMax, cubeMax) - Math.min(myMin, cubeMin);
			return totalSpan > totalLength;
		}


	/**
	 * The draw method for the cube
	 */
	@Override
	public void draw( )
		{
			applet.pushMatrix();
			applet.textureMode(PConstants.NORMAL);

			applet.translate(translateX, translateY, translateZ);
			applet.rotateX(rotationX);
			applet.rotateY(rotationY);
			applet.rotateZ(rotationZ);

			applet.stroke(1, 1, 1);
			applet.strokeWeight(1f);

			// BEGIN CUBE
			if (isBounding)
				{
					applet.fill(color);
				}
			else
				{
					applet.noFill();
				}

			applet.beginShape(PConstants.QUADS);

			if (frontTexture != null)
				{
					applet.texture(frontTexture);
				}

			// Front side
			applet.vertex(size / 2, size / 2, size / 2, 1, 1);
			applet.vertex(-size / 2, size / 2, size / 2, 0, 1);
			applet.vertex(-size / 2, -size / 2, size / 2, 0, 0);
			applet.vertex(size / 2, -size / 2, size / 2, 1, 0);

			applet.endShape();
			applet.beginShape(PConstants.QUADS);

			// Top side
			applet.vertex(size / 2, size / 2, -size / 2);
			applet.vertex(-size / 2, size / 2, -size / 2);
			applet.vertex(-size / 2, size / 2, size / 2);
			applet.vertex(size / 2, size / 2, size / 2);

			// Bottom side
			applet.vertex(size / 2, -size / 2, size / 2);
			applet.vertex(-size / 2, -size / 2, size / 2);
			applet.vertex(-size / 2, -size / 2, -size / 2);
			applet.vertex(size / 2, -size / 2, -size / 2);

			// Back side
			applet.vertex(size / 2, -size / 2, -size / 2);
			applet.vertex(-size / 2, -size / 2, -size / 2);
			applet.vertex(-size / 2, size / 2, -size / 2);
			applet.vertex(size / 2, size / 2, -size / 2);

			applet.endShape();
			applet.beginShape(PConstants.QUADS);

			if (leftTexture != null)
				{
					applet.texture(leftTexture);
				}

			// Left side
			applet.vertex(-size / 2, size / 2, size / 2, 1, 0);
			applet.vertex(-size / 2, size / 2, -size / 2, 0, 0);
			applet.vertex(-size / 2, -size / 2, -size / 2, 0, 1);
			applet.vertex(-size / 2, -size / 2, size / 2, 1, 1);

			applet.endShape();
			applet.beginShape(PConstants.QUADS);

			if (rightTexture != null)
				{
					applet.texture(rightTexture);
				}

			// Right side
			applet.vertex(size / 2, size / 2, -size / 2, 1, 1);
			applet.vertex(size / 2, size / 2, size / 2, 0, 1);
			applet.vertex(size / 2, -size / 2, size / 2, 0, 1);
			applet.vertex(size / 2, -size / 2, -size / 2, 1, 0);

			applet.endShape();

			update();

			applet.popMatrix();
		}


	/**
	 * Gets the absolute bounding box
	 *
	 * @return The absolute bounding box
	 */
	public BoundingBox3D getAbsoluteBoundingBox( )
		{
			return absoluteBoundingBox;
		}


	/**
	 * Gets the color
	 *
	 * @return The color of the cube object
	 */
	public int getColor( )
		{
			return color;
		}


	/**
	 * Gets the normal vectors of the cube object
	 *
	 * @return A list object contain the normal vectors
	 */
	public List <PVector> getNormalVectors( )
		{
			final List <PVector> normals = new LinkedList <PVector>();

			final PMatrix rotMatrix = getRotationMatrix();

			PVector current = new PVector(1, 0, 0);
			current = rotMatrix.mult(current, null);
			normals.add(current);

			current = new PVector(0, 1, 0);
			current = rotMatrix.mult(current, null);
			normals.add(current);

			current = new PVector(0, 0, 1);
			current = rotMatrix.mult(current, null);
			normals.add(current);

			return normals;
		}


	/**
	 * Gets the parent of the cube object
	 */
	@Override
	public GraphicObject3D getParent( )
		{
			return null;
		}


	/**
	 * Gets the relative bounding box of the cube
	 *
	 * @return The relative bounding box of the cube object
	 */
	public BoundingBox3D getRelativeBoundingBox( )
		{
			return relativeBoundingBox;
		}


	/**
	 * Gets the rotation in the X direction
	 *
	 * @return The rotation in the X direction of the cube object
	 */
	@Override
	public float getRotationX( )
		{
			return rotationX;
		}


	/**
	 * Gets the rotation in the Y direction
	 *
	 * @return The rotation in the Y direction of the cube object
	 */
	@Override
	public float getRotationY( )
		{
			return rotationY;
		}


	/**
	 * Gets the rotation in the Z direction
	 *
	 * @return The rotation in the Z direction of the cube object
	 */
	@Override
	public float getRotationZ( )
		{
			return rotationZ;
		}


	/**
	 * Gets the translation in the X direction
	 *
	 * @return The translation in the X direction of the cube object
	 */
	@Override
	public float getTranslationX( )
		{
			return translateX;
		}


	/**
	 * Gets the translation in the Y direction
	 *
	 * @return The translation in the Y direction of the cube object
	 */
	@Override
	public float getTranslationY( )
		{
			return translateY;
		}


	/**
	 * Gets the translation in the Z direction
	 *
	 * @return The translation in the Z direction of the cube object
	 */
	@Override
	public float getTranslationZ( )
		{
			return translateZ;
		}


	/**
	 * Returns a list of the eight vertices in world coordinates
	 *
	 * @return A list of vertices of the object
	 */
	public List <PVector> getVertices( )
		{
			final List <PVector> vertices = new ArrayList <PVector>();

			// Front face
			vertices.add(new PVector(size / 2, size / 2, size / 2));
			vertices.add(new PVector(-size / 2, size / 2, size / 2));
			vertices.add(new PVector(-size / 2, -size / 2, size / 2));
			vertices.add(new PVector(size / 2, -size / 2, size / 2));

			// Back face
			vertices.add(new PVector(size / 2, size / 2, -size / 2));
			vertices.add(new PVector(-size / 2, size / 2, -size / 2));
			vertices.add(new PVector(-size / 2, -size / 2, -size / 2));
			vertices.add(new PVector(size / 2, -size / 2, -size / 2));

			// Convert each corner to world coordinates
			for (int i = 0; i < vertices.size(); i++)
				{
					vertices.set(i, relativeToParentCoordinates(vertices.get(i)));
				}

			return vertices;
		}


	/**
	 * Gets the rotational velocity in the X direction
	 *
	 * @return The rotational velocity in the X direction of the cube object
	 */
	public float getXRotationalVelocity( )
		{
			return xRotationalVelocity;
		}


	/**
	 * Gets the translation in the X direction
	 *
	 * @return The translation in the X direction of the cube object
	 */
	public float getXTranslationalVelocity( )
		{
			return xTranslationalVelocity;
		}


	/**
	 * Gets the rotational velocity in the Y direction
	 *
	 * @return The rotational velocity in the Y direction of the cube object
	 */
	public float getYRotationalVelocity( )
		{
			return yRotationalVelocity;
		}


	/**
	 * Gets the translational velocity in the Y direction
	 *
	 * @return The translational velocity in the Y direction of the cube object
	 */
	public float getYTranslationalVelocity( )
		{
			return yTranslationalVelocity;
		}


	/**
	 * Gets the rotational velocity in the Z direction
	 *
	 * @return The rotational velocity in the Z direction of the cube object
	 */
	public float getZRotationalVelocity( )
		{
			return zRotationalVelocity;
		}


	/**
	 * Gets the translational velocity in the Z direction
	 *
	 * @return The translational velocity in the Z direction of the cube object
	 */
	public float getZTranslationalVelocity( )
		{
			return zTranslationalVelocity;
		}


	/**
	 * Detects if a cube intersects another cube
	 *
	 * @param cube
	 *            The cube object to test
	 * @return True if it does, False if not
	 */
	public boolean intersects(Cube cube)
		{
			final List <PVector> myVertices = getVertices();
			final List <PVector> cubeVertices = cube.getVertices();
			final List <PVector> normalVecs = getNormalVectors();

			for (final PVector normal : normalVecs)
				{
					if (disjointAlongAxis(myVertices, cubeVertices, normal)) { return false; }
				}

			final List <PVector> cubeNormals = cube.getNormalVectors();

			for (final PVector normal : cubeNormals)
				{
					if (disjointAlongAxis(myVertices, cubeVertices, normal)) { return false; }
				}

			for (final PVector myNormal : normalVecs)
				{
					for (final PVector normal : cubeNormals)
						{
							if (disjointAlongAxis(myVertices, cubeVertices, myNormal.cross(normal))) { return false; }
						}
				}

			return true;
		}


	@Override
	public boolean isInside(float x, float y, float z)
		{
			// Transform the coordinates to relative coordinates
			final PVector pos = parentToRelativeCoordinates(x, y, z);
			return relativeBoundingBox.isInside(pos.x, pos.y, pos.z);
		}


	/**
	 * Reverses the translational velocity of the cube object
	 */
	public void reverseTranslationalVelocity( )
		{
			xTranslationalVelocity *= -1;
			yTranslationalVelocity *= -1;
			zTranslationalVelocity *= -1;
		}


	/**
	 * Sets the color
	 *
	 * @param color
	 *            The color to set
	 */
	public void setColor(int color)
		{
			this.color = color;
		}


	/**
	 * Sets the fill
	 *
	 * @param fill
	 *            The fill to set
	 */
	public void setFill(boolean fill)
		{
			this.isBounding = fill;
		}


	/**
	 * Sets the front texture
	 *
	 * @param frontTexture
	 *            The front texture to set
	 */
	public void setFrontTexture(PImage frontTexture)
		{
			this.frontTexture = frontTexture;
		}


	/**
	 * Sets the left texture
	 *
	 * @param leftTexture
	 *            The left texture to set
	 */
	public void setLeftTexture(PImage leftTexture)
		{
			this.leftTexture = leftTexture;
		}


	/**
	 * Sets the right texture
	 *
	 * @param rightTexture
	 *            The right texture to set
	 */
	public void setRightTexture(PImage rightTexture)
		{
			this.rightTexture = rightTexture;
		}


	/**
	 * Sets the rotation on the X axis
	 *
	 * @param angle
	 *            The new rotation to set
	 */
	@Override
	public void setRotationX(float angle)
		{
			this.rotationX = angle;
		}


	/**
	 * Sets the rotation on the Z axis
	 *
	 * @param angle
	 *            The new rotation to set
	 */
	@Override
	public void setRotationY(float angle)
		{
			this.rotationY = angle;
		}


	/**
	 * Sets the rotation on the Z axis
	 *
	 * @param angle
	 *            The new rotation to set
	 */
	@Override
	public void setRotationZ(float angle)
		{
			this.rotationZ = angle;
		}


	/**
	 * Set the translation in the X direction
	 *
	 * @param x
	 *            The new translation of the cube object
	 */
	@Override
	public void setTranslationX(float x)
		{
			this.translateX = x;
		}


	/**
	 * Set the translation in the Y direction
	 *
	 * @param y
	 *            The new translation of the cube object
	 */
	@Override
	public void setTranslationY(float y)
		{
			this.translateY = y;
		}


	/**
	 * Set the translation in the Z direction
	 *
	 * @param z
	 *            The new translation of the cube object
	 */
	@Override
	public void setTranslationZ(float z)
		{
			this.translateZ = z;
		}


	/**
	 * Sets the rotational velocity in the X direction
	 *
	 * @param velocity
	 *            The new velocity to set
	 */
	public void setXRotationalVelocity(float velocity)
		{
			this.xRotationalVelocity = velocity;
		}


	/**
	 * Sets the translational velocity in the X direction
	 *
	 * @param velocity
	 *            The new velocity to set
	 */
	public void setXTranslationalVelocity(float velocity)
		{
			this.xTranslationalVelocity = velocity;
		}


	/**
	 * Sets the rotational velocity in the Y direction
	 *
	 * @param velocity
	 *            The new velocity to set
	 */
	public void setYRotationalVelocity(float velocity)
		{
			this.yRotationalVelocity = velocity;
		}


	/**
	 * Sets the translational velocity in the X direction
	 *
	 * @param velocity
	 *            The new velocity to set
	 */
	public void setYTranslationalVelocity(float velocity)
		{
			this.yTranslationalVelocity = velocity;
		}


	/**
	 * Sets the rotational velocity in the Z direction
	 *
	 * @param velocity
	 *            The new velocity to set
	 */
	public void setZRotationalVelocity(float velocity)
		{
			this.zRotationalVelocity = velocity;
		}


	/**
	 * Sets the translational velocity in the X direction
	 *
	 * @param velocity
	 *            The new velocity to set
	 */
	public void setZTranslationalVelocity(float velocity)
		{
			this.zTranslationalVelocity = velocity;
		}


	/**
	 * The update function of the cube object
	 */
	@Override
	public void update( )
		{
			if (isBounding)
				{
					translateX += xTranslationalVelocity;
					translateY += yTranslationalVelocity;
					translateZ += zTranslationalVelocity;
					rotationX += xRotationalVelocity;
					rotationY += yRotationalVelocity;
					rotationZ += zRotationalVelocity;
				}
			computeAbsoluteBoundingBox();
		}
}
