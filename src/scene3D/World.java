
package scene3D;


import java.util.ArrayList;
import java.util.Collection;

import processing.core.PApplet;
import scene3Dabstract.ComplexGraphicObject3D;
import scene3Dabstract.GraphicObject3D;


/**
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class World extends ComplexGraphicObject3D<Cube> {
	
	Cube				boundingCube;
	Collection<Cube> cubesInWorld	= new ArrayList<Cube>();
										
										
	public World(PApplet theApp)
		{
			super(new ArrayList<Cube>());
			cubesInWorld = getCollection();

			// Create a cube that will contain all the other cubes in the
			// application and act as a barrier.
			// Also remove the fill around the cube so we can see inside it.
			boundingCube = new Cube(0, 0, 0, 400, theApp);
			boundingCube.setFill(false);
			
			// Randomly decide the number of cubes we should generate.
			int numberOfCubesToGenerate = (int)theApp.random(2.0f, 10.0f);
			
			// Generate the cubes and add them to the array list.
			for (int i = 0; i < numberOfCubesToGenerate; i++)
				{
					float xPos = theApp.random(-190.00f, 190.00f);
					float yPos = theApp.random(-190.00f, 190.00f);
					float zPos = theApp.random(-190.00f, 190.00f);
					float size = theApp.random(10.0f, 75.0f);
					float xRot = theApp.random(2.0f, 10.0f);
					float yRot = theApp.random(2.0f, 10.0f);
					float zRot = theApp.random(2.0f, 10.0f);
					// TODO Perhaps get rid of the reference to a parent? What
					// are your thoughts?
					Cube interiorCube = new Cube(xPos, yPos, zPos, size, xRot, yRot, zRot, theApp);
					cubesInWorld.add(interiorCube);
				}
		}


	@Override
	public boolean isInside(float x, float y, float z) {
		return false;
	}

	public void draw( )
		{
			boundingCube.draw();
			super.draw();
		}

	/**
	 * Always returns null since the world has no rotation
	 * @return null
	 */
	@Override
	public GraphicObject3D getParent() {
		return null;
	}

	@Override
	public float getRotationX() {
		return 0;
	}

	@Override
	public float getRotationY() {
		return 0;
	}

	@Override
	public float getRotationZ() {
		return 0;
	}

	@Override
	public float getTranslationX() {
		return 0;
	}

	@Override
	public float getTranslationY() {
		return 0;
	}

	@Override
	public float getTranslationZ() {
		return 0;
	}

	@Override
	public void setTranslationX(float x) {

	}

	@Override
	public void setTranslationY(float y) {

	}

	@Override
	public void setTranslationZ(float z) {

	}

	@Override
	public void setRotationX(float angle) {

	}

	@Override
	public void setRotationY(float angle) {

	}

	@Override
	public void setRotationZ(float angle) {

	}
}
