package Abstract;

import processing.core.PApplet;
import processing.core.PMatrix;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wpgodone on 12/21/2015.
 */
public class Cube extends SimpleGraphicObject3D {

	private GraphicObject3D parent;

	private PMatrix transformationMatrix;
	private PApplet applet;

	private BoundingBox3D relativeBoundingBox;

	private float translateX, translateY, translateZ;
	private float rotationX, rotationY, rotationZ;
	private float size;

	public Cube(float x, float y, float z, float size, PApplet applet)
	{
		this(x, y, z, size, 0, 0, 0, applet, null);
	}

	public Cube(float x, float y, float z, float size, float rotX, float rotY, float rotZ, PApplet applet, GraphicObject3D parent)
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
		List<PVector> vertices = cube.getVertices();
		for (PVector vertex : vertices)
		{
			if (isInside(vertex.x, vertex.y, vertex.z))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw()
	{
		applet.pushMatrix();
		//applet.scale(100f, 100f, 100f);

		applet.translate(translateX, translateY, translateZ);
		applet.rotate(180f / applet.TWO_PI, rotationX, rotationY, rotationZ);
		this.transformationMatrix = applet.getMatrix().get();

		applet.stroke(1f);
		applet.strokeWeight(.01f);
		//	applet.rect(0, 0, 10, 10);

		applet.beginShape(PApplet.QUADS);
		// TODO texture mapping

		// I think I have the coordinates mixed up

		// Floor
		applet.fill(100, 200, 0);
		applet.vertex( 10f, 10f, 10f);
		applet.vertex(-10f, 10f, 10f);
		applet.vertex(-10f, 10f, -10f);
		applet.vertex( 10f, 10f, -10f);

		// BEGIN CUBE
		applet.fill(0, 100, 100);

		// Top side
		applet.vertex( 1f, 1f, -1f);
		applet.vertex(-1f, 1f, -1f);
		applet.vertex(-1f, 1f,  1f);
		applet.vertex( 1f, 1f,  1f);

		// Bottom side
		applet.vertex( 1f, -1f,  1f);
		applet.vertex(-1f, -1f,  1f);
		applet.vertex(-1f, -1f, -1f);
		applet.vertex( 1f, -1f, -1f);

		applet.endShape();
		applet.beginShape(PApplet.QUADS);

		// Front side
		applet.vertex( 1f,  1f, 1f, 1, 1);
		applet.vertex(-1f,  1f, 1f, 0, 1);
		applet.vertex(-1f, -1f, 1f, 0, 0);
		applet.vertex( 1f, -1f, 1f, 1, 0);

		applet.endShape();
		applet.beginShape(PApplet.QUADS);

		applet.fill(0, 100, 100);

		// Back side
		applet.vertex( 1f, -1f, -1f);
		applet.vertex(-1f, -1f, -1f);
		applet.vertex(-1f,  1f, -1f);
		applet.vertex( 1f,  1f, -1f);

		// Left side
		applet.vertex(-1f,  1f,  1f);
		applet.vertex(-1f,  1f, -1f);
		applet.vertex(-1f, -1f, -1f);
		applet.vertex(-1f, -1f,  1f);

		// Right side
		applet.vertex(1f,  1f, -1f);
		applet.vertex(1f,  1f,  1f);
		applet.vertex(1f, -1f,  1f);
		applet.vertex(1f, -1f, -1f);

		applet.endShape();
		applet.popMatrix();
	}

	@Override
	public void update() {

	}

	public BoundingBox3D getRelativeBoundingBox()
	{
		return relativeBoundingBox;
	}

	/**
	 * Returns a list of the eight vertices in relative coordinates
	 * @return
	 */
	public List<PVector> getVertices()
	{
		List<PVector> vertices = new LinkedList<PVector>();

		// Front face
		vertices.add(new PVector( size / 2,  size / 2,  size / 2));
		vertices.add(new PVector(-size / 2,  size / 2,  size / 2));
		vertices.add(new PVector(-size / 2, -size / 2,  size / 2));
		vertices.add(new PVector( size / 2, -size / 2,  size / 2));

		// Back face
		vertices.add(new PVector( size / 2,  size / 2,  -size / 2));
		vertices.add(new PVector(-size / 2,  size / 2,  -size / 2));
		vertices.add(new PVector(-size / 2, -size / 2,  -size / 2));
		vertices.add(new PVector( size / 2, -size / 2,  -size / 2));

		return vertices;
	}

	@Override
	public GraphicObject3D getParent() {
		return parent;
	}

	@Override
	public float getTranslationX() {
		return translateX;
	}

	@Override
	public float getTranslationY() {
		return translateY;
	}

	@Override
	public float getTranslationZ() {
		return translateZ;
	}

	@Override
	public float getRotationX() {
		return rotationX;
	}

	@Override
	public float getRotationY() {
		return rotationY;
	}

	@Override
	public float getRotationZ() {
		return rotationZ;
	}
}
