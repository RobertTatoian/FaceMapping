
package scene3D;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PVector;
import scene3Dabstract.BoundingBox3D;
import scene3Dabstract.GraphicObject3D;
import scene3Dabstract.SimpleGraphicObject3D;

/**
 * 
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class Cube extends SimpleGraphicObject3D {
	
	private BoundingBox3D		absoluteBoundingBox;
								
	private final PApplet		applet;
	private int					color		= 0xFF00A0A0;				// default
											
	private PImage				frontTexture, leftTexture, rightTexture;
	private boolean				isBounding	= true;
	private final BoundingBox3D	relativeBoundingBox;
								
	private float				rotationX, rotationY, rotationZ;
								
	private final float			size;
								
	private float				translateX, translateY, translateZ;
								
	private float				xRotationalVelocity;
	private float				xTranslationalVelocity;
								
	private float				yRotationalVelocity;
	private float				yTranslationalVelocity;
								
	private float				zRotationalVelocity;
	private float				zTranslationalVelocity;
								
								
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
		
		
	public Cube(float x, float y, float z, float size, PApplet applet)
		{
			this(x, y, z, size, 0, 0, 0, applet);
		}
		
		
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
		
		
	public BoundingBox3D getAbsoluteBoundingBox( )
		{
			return absoluteBoundingBox;
		}
		
		
	public int getColor( )
		{
			return color;
		}
		
		
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
		
		
	@Override
	public GraphicObject3D getParent( )
		{
			return null;
		}
		
		
	public BoundingBox3D getRelativeBoundingBox( )
		{
			return relativeBoundingBox;
		}
		
		
	@Override
	public float getRotationX( )
		{
			return rotationX;
		}
		
		
	@Override
	public float getRotationY( )
		{
			return rotationY;
		}
		
		
	@Override
	public float getRotationZ( )
		{
			return rotationZ;
		}
		
		
	@Override
	public float getTranslationX( )
		{
			return translateX;
		}
		
		
	@Override
	public float getTranslationY( )
		{
			return translateY;
		}
		
		
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
		
		
	public float getXRotationalVelocity( )
		{
			return xRotationalVelocity;
		}
		
		
	public float getXTranslationalVelocity( )
		{
			return xTranslationalVelocity;
		}
		
		
	public float getYRotationalVelocity( )
		{
			return yRotationalVelocity;
		}
		
		
	public float getYTranslationalVelocity( )
		{
			return yTranslationalVelocity;
		}
		
		
	public float getZRotationalVelocity( )
		{
			return zRotationalVelocity;
		}
		
		
	public float getZTranslationalVelocity( )
		{
			return zTranslationalVelocity;
		}
		
		
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
		
		
	public void reverseTranslationalVelocity( )
		{
			xTranslationalVelocity *= -1;
			yTranslationalVelocity *= -1;
			zTranslationalVelocity *= -1;
		}
		
		
	public void setColor(int color)
		{
			this.color = color;
		}
		
		
	public void setFill(boolean fill)
		{
			this.isBounding = fill;
		}
		
		
	public void setFrontTexture(PImage frontTexture)
		{
			this.frontTexture = frontTexture;
		}
		
		
	public void setLeftTexture(PImage leftTexture)
		{
			this.leftTexture = leftTexture;
		}
		
		
	public void setRightTexture(PImage rightTexture)
		{
			this.rightTexture = rightTexture;
		}
		
		
	@Override
	public void setRotationX(float angle)
		{
			this.rotationX = angle;
		}
		
		
	@Override
	public void setRotationY(float angle)
		{
			this.rotationY = angle;
		}
		
		
	@Override
	public void setRotationZ(float angle)
		{
			this.rotationZ = angle;
		}
		
		
	@Override
	public void setTranslationX(float x)
		{
			this.translateX = x;
		}
		
		
	@Override
	public void setTranslationY(float y)
		{
			this.translateY = y;
		}
		
		
	@Override
	public void setTranslationZ(float z)
		{
			this.translateZ = z;
		}
		
		
	public void setXRotationalVelocity(float velocity)
		{
			this.xRotationalVelocity = velocity;
		}
		
		
	public void setXTranslationalVelocity(float velocity)
		{
			this.xTranslationalVelocity = velocity;
		}
		
		
	public void setXZotationalVelocity(float velocity)
		{
			this.zRotationalVelocity = velocity;
		}
		
		
	public void setYRotationalVelocity(float velocity)
		{
			this.yRotationalVelocity = velocity;
		}
		
		
	public void setYTranslationalVelocity(float velocity)
		{
			this.yTranslationalVelocity = velocity;
		}
		
		
	public void setZTranslationalVelocity(float velocity)
		{
			this.zTranslationalVelocity = velocity;
		}
		
		
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
