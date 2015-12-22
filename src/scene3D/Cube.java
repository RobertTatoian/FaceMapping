
package scene3D;


import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import scene3Dabstract.BoundingBox3D;
import scene3Dabstract.GraphicObject3D;
import scene3Dabstract.SimpleGraphicObject3D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by wpgodone on 12/21/2015.
 */
public class Cube extends SimpleGraphicObject3D {
	
	private GraphicObject3D	parent;
							
	private PApplet			applet;
							
	private BoundingBox3D	relativeBoundingBox;
	private BoundingBox3D	absoluteBoundingBox;
							
	private float			translateX, translateY, translateZ;
	private float			rotationX, rotationY, rotationZ;
	private float			size;
							
	private boolean			isBounding	= true;
										
	private PImage			frontTexture, leftTexture, rightTexture;
							
	private float			xRotationalVelocity;
							
	private float			yRotationalVelocity;
							
	private float			zRotationalVelocity;
							
	private float			xTranslationalVelocity;
							
	private float			yTranslationalVelocity;
							
	private float			zTranslationalVelocity;
							
							
	public Cube(float x, float y, float z, float size, PApplet applet)
		{
			this(x, y, z, size, 0, 0, 0, applet);
			
			float translationalLimit = 5f;
			xTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			yTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			zTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			
			float rotationalLimit = 0.1f;
			xRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
			yRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
			zRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
		}
		
		
	public Cube(float x, float y, float z, float size, float rotX, float rotY, float rotZ, PApplet applet)
		{
			this.applet = applet;
			
			translateX = x;
			translateY = y;
			translateZ = z;
			
			rotationX = rotX;
			rotationY = rotY;
			rotationZ = rotZ;
			
			this.size = size;
			
			relativeBoundingBox = new BoundingBox3D(x, y, z, size, size, size);
			computeAbsoluteBoundingBox();
			
			float translationalLimit = 5f;
			xTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			yTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			zTranslationalVelocity = this.applet.random(-translationalLimit, translationalLimit);
			
			float rotationalLimit = 0.1f;
			xRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
			yRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
			zRotationalVelocity = this.applet.random(-rotationalLimit, rotationalLimit);
		}
		
		
	@Override
	public boolean isInside(float x, float y, float z)
		{
			// Transform the coordinates to relative coordinates
			PVector pos = parentToRelativeCoordinates(x, y, z);
			return relativeBoundingBox.isInside(pos.x, pos.y, pos.z);
		}
		
		
	public boolean intersects(Cube cube)
		{
			List <PVector> vertices = cube.getVertices();
			
			for (PVector pos : vertices)
				{
					if (absoluteBoundingBox.isInside(pos.x, pos.y, pos.z)) { return true; }
				}
			return false;
		}
		
		
	public boolean intersectsSAT(Cube cube)
		{
			List <PVector> myVertices = getVertices();
			List <PVector> cubeVertices = getVertices();
			
			List <PVector> normalVecs = getNormalVectors();
			
			for (PVector normal : normalVecs)
				{
					if (intersectsAlongAxis(myVertices, cubeVertices, normal)) return true;
				}
				
			List <PVector> cubeNormals = cube.getNormalVectors();
			
			for (PVector normal : cubeNormals)
				{
					if (intersectsAlongAxis(myVertices, cubeVertices, normal)) return true;
				}
				
			for (PVector myNormal : normalVecs)
				for (PVector cubeNormal : cubeNormals)
					{
						if (intersectsAlongAxis(myVertices, cubeVertices, myNormal.cross(cubeNormal))) return true;
					}
					
			return false;
		}
		
		
	private boolean intersectsAlongAxis(List <PVector> myVertices, List <PVector> otherVertices, PVector axis)
		{
			float myMax = Float.NEGATIVE_INFINITY, myMin = Float.POSITIVE_INFINITY;
			float cubeMax = Float.NEGATIVE_INFINITY, cubeMin = Float.POSITIVE_INFINITY;
			
			// Find the max and min coordinate of each cube along the axis by
			// projecting
			// each corner onto the axis
			for (PVector corner : myVertices)
				{
					float dist = corner.dot(axis);
					myMax = Math.max(myMax, dist);
					myMin = Math.min(myMin, dist);
				}
				
			for (PVector corner : otherVertices)
				{
					float dist = corner.dot(axis);
					cubeMax = Math.max(cubeMax, dist);
					cubeMin = Math.min(cubeMin, dist);
				}
				
			float totalLength = (myMax - myMin) + (cubeMax - cubeMin);
			float totalSpan = Math.max(myMax, cubeMax) - Math.min(myMin, cubeMin);
			return totalSpan <= totalLength;
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
					applet.fill(0, 100, 100);
				}
			else
				{
					applet.noFill();
				}
				
			applet.beginShape(PApplet.QUADS);
			
			if (frontTexture != null) applet.texture(frontTexture);
			
			// Front side
			applet.vertex(size / 2, size / 2, size / 2, 1, 1);
			applet.vertex(-size / 2, size / 2, size / 2, 0, 1);
			applet.vertex(-size / 2, -size / 2, size / 2, 0, 0);
			applet.vertex(size / 2, -size / 2, size / 2, 1, 0);
			
			applet.endShape();
			applet.beginShape(PApplet.QUADS);
			
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
			applet.beginShape(PApplet.QUADS);
			
			if (leftTexture != null) applet.texture(leftTexture);
			
			// Left side
			applet.vertex(-size / 2, size / 2, size / 2, 1, 0);
			applet.vertex(-size / 2, size / 2, -size / 2, 0, 0);
			applet.vertex(-size / 2, -size / 2, -size / 2, 0, 1);
			applet.vertex(-size / 2, -size / 2, size / 2, 1, 1);
			
			applet.endShape();
			applet.beginShape(PApplet.QUADS);
			
			if (rightTexture != null) applet.texture(rightTexture);
			
			// Right side
			applet.vertex(size / 2, size / 2, -size / 2, 1, 1);
			applet.vertex(size / 2, size / 2, size / 2, 0, 1);
			applet.vertex(size / 2, -size / 2, size / 2, 0, 1);
			applet.vertex(size / 2, -size / 2, -size / 2, 1, 0);
			
			applet.endShape();
			
			update();
			
			applet.popMatrix();
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
		}
		
		
	public BoundingBox3D getRelativeBoundingBox( )
		{
			return relativeBoundingBox;
		}
		
		
	public BoundingBox3D getAbsoluteBoundingBox( )
		{
			return absoluteBoundingBox;
		}
		
		
	private void computeAbsoluteBoundingBox( )
		{
			List <PVector> corners = getVertices();
			float xMin = Float.POSITIVE_INFINITY, yMin = Float.POSITIVE_INFINITY, zMin = Float.POSITIVE_INFINITY;
			float xMax = Float.NEGATIVE_INFINITY, yMax = Float.NEGATIVE_INFINITY, zMax = Float.NEGATIVE_INFINITY;
			
			for (PVector corner : corners)
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
	 * Returns a list of the eight vertices in world coordinates
	 * 
	 * @return
	 */
	public List <PVector> getVertices( )
		{
			List <PVector> vertices = new ArrayList <PVector>();
			
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
		
		
	public List <PVector> getNormalVectors( )
		{
			List <PVector> vertices = new LinkedList <PVector>();
			vertices.add(relativeToParentCoordinates(new PVector(1, 0, 0)));
			vertices.add(relativeToParentCoordinates(new PVector(0, 1, 0)));
			vertices.add(relativeToParentCoordinates(new PVector(0, 0, 1)));
			return vertices;
		}
		
		
	@Override
	public GraphicObject3D getParent( )
		{
			return parent;
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
		
		
	public void setTranslationX(float x)
		{
			this.translateX = x;
		}
		
		
	public void setTranslationY(float y)
		{
			this.translateY = y;
		}
		
		
	public void setTranslationZ(float z)
		{
			this.translateZ = z;
		}
		
		
	public void setRotationX(float angle)
		{
			this.rotationX = angle;
		}
		
		
	public void setRotationY(float angle)
		{
			this.rotationY = angle;
		}
		
		
	public void setRotationZ(float angle)
		{
			this.rotationZ = angle;
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
		
}
