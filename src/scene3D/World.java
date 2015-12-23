
package scene3D;

import java.util.ArrayList;

import processing.core.PApplet;
import scene3Dabstract.BoundingBox3D;
import scene3Dabstract.ComplexGraphicObject3D;
import scene3Dabstract.GraphicObject3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Sets up the scene. Since this is the world, it's defined in world coordinates
 *
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class World extends ComplexGraphicObject3D <Cube> {

	/**
	 * Size of the bounding Cube
	 */
	private static final int	BOUNDING_CUBE_SIZE	= 400;

	/**
	 * The bounding cube
	 */
	private final Cube			boundingCube;

	/**
	 * The list of all cubes in the world (except the bounding cube).
	 */
	private ArrayList <Cube>	cubesInWorld;

	/**
	 * Initializes the scene.
 	 * @param theApp The applet for the world to be drawn in.
	 */
	public World(PApplet theApp)
		{
			super(new ArrayList <Cube>());
			cubesInWorld = (ArrayList <Cube>)super.getCollection();

			// Create a cube that will contain all the other cubes in the
			// application and act as a barrier.
			// Also remove the fill around the cube so we can see inside it.
			boundingCube = new Cube(0, 0, 0, BOUNDING_CUBE_SIZE, theApp);
			boundingCube.setFill(false);
			
			// Randomly decide the number of cubes we should generate.
			int numberOfCubesToGenerate = (int)theApp.random(3.0f, 10.0f);
			
			// Generate the cubes and add them to the array list.
			for (int i = 0; i < numberOfCubesToGenerate; i++)
				{
					float size = theApp.random(20.0f, 60.0f);
					float posLimit = BOUNDING_CUBE_SIZE / 2 - size * .75f;
					float xPos = theApp.random(-posLimit, posLimit);
					float yPos = theApp.random(-posLimit, posLimit);
					float zPos = theApp.random(-posLimit, posLimit);
					float xRot = theApp.random(2.0f, 10.0f);
					float yRot = theApp.random(2.0f, 10.0f);
					float zRot = theApp.random(2.0f, 10.0f);
					Cube interiorCube = new Cube(xPos, yPos, zPos, size, xRot, yRot, zRot, theApp);
					cubesInWorld.add(interiorCube);
				}
		}

	/**
	 * Makes sure that all cube in the world stay inside the bounding cube.
	 */
	private void checkForCollisionsWithBoundingCube( )
		{
			BoundingBox3D bounds = boundingCube.getAbsoluteBoundingBox();
			for (Cube cube : cubesInWorld)
				{
					BoundingBox3D absolute = cube.getAbsoluteBoundingBox();
					
					if (absolute.getMinX() < bounds.getMinX())
						{
							cube.setTranslationX(
							        bounds.getMinX() + Math.abs(cube.getTranslationX() - absolute.getMinX()));
							cube.setXTranslationalVelocity(-cube.getXTranslationalVelocity());
						}
					else if (absolute.getMaxX() > bounds.getMaxX())
						{
							cube.setTranslationX(
							        bounds.getMaxX() - Math.abs(cube.getTranslationX() - absolute.getMaxX()));
							cube.setXTranslationalVelocity(-cube.getXTranslationalVelocity());
						}
							
					if (absolute.getMinY() < bounds.getMinY())
						{
							cube.setTranslationY(
							        bounds.getMinY() + Math.abs(cube.getTranslationY() - absolute.getMinY()));
							cube.setYTranslationalVelocity(-cube.getYTranslationalVelocity());
						}
					else if (absolute.getMaxY() > bounds.getMaxY())
						{
							cube.setTranslationY(
							        bounds.getMaxY() - Math.abs(cube.getTranslationY() - absolute.getMaxY()));
							cube.setYTranslationalVelocity(-cube.getYTranslationalVelocity());
						}
							
					if (absolute.getMinZ() < bounds.getMinZ())
						{
							cube.setTranslationZ(
							        bounds.getMinZ() + Math.abs(cube.getTranslationZ() - absolute.getMinZ()));
							cube.setZTranslationalVelocity(-cube.getZTranslationalVelocity());
						}
					else if (absolute.getMaxZ() > bounds.getMaxZ())
						{
							cube.setTranslationZ(
							        bounds.getMaxZ() - Math.abs(cube.getTranslationZ() - absolute.getMaxZ()));
							cube.setZTranslationalVelocity(-cube.getZTranslationalVelocity());
						}
				}
				
		}

	/**
	 * Checks for collisions between cubes in the world and colors an collided
	 * cubes red.
	 */
	private void checkForCubeCollisions( )
		{
			int size = cubesInWorld.size();
			for (Cube c : cubesInWorld)
				{
					c.setColor(0xFF00A0A0);
				}
				
			for (int i = 0; i < size; i++)
				{
					for (int j = i + 1; j < size; j++)
						{
							if (cubesInWorld.get(i).intersects(cubesInWorld.get(j)))
								{
									cubesInWorld.get(i).setColor(0xFFA00000);
									cubesInWorld.get(j).setColor(0xFFA00000);
								}
						}
				}
		}

	/**
	 * Draws the world.
	 */
	@Override
	public void draw( )
		{
			super.draw();
			boundingCube.draw();
		}

	/**
	 * Updates the world.
	 */
	@Override
	public void update( )
	{
		checkForCubeCollisions();
		checkForCollisionsWithBoundingCube();
		super.update();
	}

	/**
	 * Always returns null since the world has no rotation
	 *
	 * @return null
	 */
	@Override
	public GraphicObject3D getParent( )
		{
			return null;
		}

	/**
	 * Returns 0 since the World is defined in the world reference frame.
	 */
	@Override
	public float getRotationX( )
		{
			return 0;
		}

	/**
	 * Returns 0 since the World is defined in the world reference frame.
	 */
	@Override
	public float getRotationY( )
		{
			return 0;
		}

	/**
	 * Returns 0 since the World is defined in the world reference frame.
	 */
	@Override
	public float getRotationZ( )
		{
			return 0;
		}

	/**
	 * Returns 0 since the World is defined in the world reference frame.
	 */
	@Override
	public float getTranslationX( )
		{
			return 0;
		}

	/**
	 * Returns 0 since the World is defined in the world reference frame.
	 */
	@Override
	public float getTranslationY( )
		{
			return 0;
		}

	/**
	 * Returns 0 since the World is defined in the world reference frame.
	 */
	@Override
	public float getTranslationZ( )
		{
			return 0;
		}

	/**
	 * Checks if a point is within the bounds of the scene
 	 * @param x
	 *            The x coordinate of the point (in world coordinates)
	 * @param y
	 *            The y coordinate of the point (in world coordinates)
	 * @param z
	 *            The z coordinate of the point (in world coordinates)
	 * @return
	 */
	@Override
	public boolean isInside(float x, float y, float z)
		{
			return boundingCube.isInside(x, y, z);
		}

	/**
	 * @throws NotImplementedException
	 */
	@Override
	public void setRotationX(float angle)
		{
			throw new NotImplementedException();
		}

	/**
	 * @throws NotImplementedException
	 */
	@Override
	public void setRotationY(float angle)
		{
			throw new NotImplementedException();
		}

	/**
	 * @throws NotImplementedException
	 */
	@Override
	public void setRotationZ(float angle)
		{
			throw new NotImplementedException();
		}

	/**
	 * @throws NotImplementedException
	 */
	@Override
	public void setTranslationX(float x)
		{
			throw new NotImplementedException();
		}

	/**
	 * @throws NotImplementedException
	 */
	@Override
	public void setTranslationY(float y)
		{
			throw new NotImplementedException();
		}

	/**
	 * @throws NotImplementedException
	 */
	@Override
	public void setTranslationZ(float z)
		{
			throw new NotImplementedException();
		}
}
