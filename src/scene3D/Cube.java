
package scene3D;


import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import scene3Dabstract.BoundingBox3D;
import scene3Dabstract.GraphicObject3D;
import scene3Dabstract.SimpleGraphicObject3D;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by wpgodone on 12/21/2015.
 */
public class Cube extends SimpleGraphicObject3D {
	
	private GraphicObject3D	parent;
							
	private PApplet			applet;
							
	private BoundingBox3D	relativeBoundingBox;
							
	private float			translateX, translateY, translateZ;
	private float			rotationX, rotationY, rotationZ;
	private float			size;
							
	private boolean			fill	= true;

	private PImage frontTexture, leftTexture, rightTexture;

	public Cube(float x, float y, float z, float size, PApplet applet)
		{
			this(x, y, z, size, 0, 0, 0, applet, null);
		}
		
		
	public Cube(float x, float y, float z, float size, float rotX, float rotY, float rotZ, PApplet applet,
	        GraphicObject3D parent)
		{
			this.applet = applet;
			this.parent = parent;
			
			translateX = x;
			translateY = y;
			translateZ = z;
			
			rotationX = rotX;
			rotationY = rotY;
			rotationZ = rotZ;
			
			this.size = size;
			
			relativeBoundingBox = new BoundingBox3D(x, y, z, size, size, size);
		}
		
		
	@Override
	public boolean isInside(float x, float y, float z)
		{
			// Transform the coordinates to relative coordinates
			PVector pos = worldToRelativeCoordinates(x, y, z);
			return relativeBoundingBox.isInside(pos.x, pos.y, pos.z);
		}
		
		
	public boolean intersects(Cube cube)
		{
			List <PVector> vertices = cube.getVertices();
			for (PVector vertex : vertices)
				{
					if (isInside(vertex.x, vertex.y, vertex.z)) { return true; }
				}
			return false;
		}
		
		
	@Override
	public void draw( )
		{
			applet.pushMatrix();
			applet.textureMode(applet.NORMAL);
			
			applet.translate(translateX, translateY, translateZ);
			applet.rotateX(rotationX);
			applet.rotateY(rotationY);
			applet.rotateZ(rotationZ);
			
			applet.stroke(1, 1, 1);
			applet.strokeWeight(1f);

			// BEGIN CUBE
			if (fill) {
				applet.fill(0, 100, 100);
			} else {
				applet.noFill();
			}

			applet.beginShape(PApplet.QUADS);

			if (frontTexture != null)
					applet.texture(frontTexture);

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

			if (leftTexture != null)
				applet.texture(leftTexture);
			
			// Left side
			applet.vertex(-size / 2, size / 2, size / 2, 1, 0);
			applet.vertex(-size / 2, size / 2, -size / 2, 0, 0);
			applet.vertex(-size / 2, -size / 2, -size / 2, 0, 1);
			applet.vertex(-size / 2, -size / 2, size / 2, 1, 1);

			applet.endShape();
			applet.beginShape(PApplet.QUADS);

			if (rightTexture != null)
				applet.texture(rightTexture);
			
			// Right side
			applet.vertex(size / 2, size / 2, -size / 2, 1, 1);
			applet.vertex(size / 2, size / 2, size / 2, 0, 1);
			applet.vertex(size / 2, -size / 2, size / 2, 0, 1);
			applet.vertex(size / 2, -size / 2, -size / 2, 1, 0);
			
			applet.endShape();
			applet.popMatrix();
		}
		
		
	@Override
	public void update( )
		{
		
		}
		
		
	public BoundingBox3D getRelativeBoundingBox( )
		{
			return relativeBoundingBox;
		}
		
		
	/**
	 * Returns a list of the eight vertices in relative coordinates
	 * 
	 * @return
	 */
	public List <PVector> getVertices( )
		{
			List <PVector> vertices = new LinkedList <PVector>();
			
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
			this.fill = fill;
		}

	public void setFrontTexture(PImage frontTexture) {
		this.frontTexture = frontTexture;
	}

	public void setLeftTexture(PImage leftTexture) {
		this.leftTexture = leftTexture;
	}

	public void setRightTexture(PImage rightTexture) {
		this.rightTexture = rightTexture;
	}

}
