
package scene3D;


import java.util.ArrayList;

import processing.core.PApplet;


/**
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class World {
	
	Cube				boundingCube;
	ArrayList <Cube>	cubesInWorld	= new ArrayList <Cube>();
										
										
	public World(PApplet theApp)
		{
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
					Cube interiorCube = new Cube(xPos, yPos, zPos, size, xRot, yRot, zRot, theApp, null);
					cubesInWorld.add(interiorCube);
				}
		}
		
		
	public void draw( )
		{
			
			boundingCube.draw();
			
			for (int i = 0; i < cubesInWorld.size(); i++)
				{
					cubesInWorld.get(i).draw();
				}
				
		}
		
		
	public void update( )
		{
		
		}
}
