
package scene3D;


/**
 * A generic, immutable bounding box in 3d.
 *
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public class BoundingBox3D {
	
	/**
	 * Size of the bounding box
	 */
	private final float	width, height, length;
						
	/**
	 * Coordinates of the bounding box
	 */
	private final float	x, y, z;
						
						
	/**
	 * Initializes a bounding box with a given position and size.
	 *
	 * @param x
	 *            The x coordinate of the point (in world coordinates)
	 * @param y
	 *            The y coordinate of the point (in world coordinates)
	 * @param z
	 *            The z coordinate of the point (in world coordinates)
	 * @param width
	 *            Size along the x-axis
	 * @param height
	 *            Size along the y-axis
	 * @param length
	 *            Size along the z-axis
	 */
	public BoundingBox3D(float x, float y, float z, float width, float height, float length)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.width = width;
			this.height = height;
			this.length = length;
		}
		
		
	/**
	 * Accesses the height of the bounding box.
	 *
	 * @return The size of the bounding box along the y-axis in world units.
	 */
	public float getHeight( )
		{
			return height;
		}
		
		
	/**
	 * Accesses the length of the bounding box.
	 *
	 * @return The size of the bounding box along the z-axis in world units.
	 */
	public float getLength( )
		{
			return length;
		}
		
		
	/**
	 * Returns the maximum x coordinate of the bounding box.
	 *
	 * @return The maximum value between the x coordinate and the x coordinate
	 *         plus the width of the bounding box.
	 */
	public float getMaxX( )
		{
			return Math.max(x, x + width);
		}
		
		
	/**
	 * Returns the maximum y coordinate of the bounding box.
	 *
	 * @return The maximum value between the y coordinate and the y coordinate
	 *         plus the height of the bounding box.
	 */
	public float getMaxY( )
		{
			return Math.max(y, y + height);
		}
		
		
	/**
	 * Returns the maximum z coordinate of the bounding box.
	 *
	 * @return The maximum value between the z coordinate and the z coordinate
	 *         plus the depth of the bounding box.
	 */
	public float getMaxZ( )
		{
			return Math.max(z, z + length);
		}
		
		
	/**
	 * Returns the minimum x coordinate of the bounding box.
	 *
	 * @return The minimum value between the x coordinate and the x coordinate
	 *         plus the width of the bounding box.
	 */
	public float getMinX( )
		{
			return Math.min(x, x + width);
		}
		
		
	/**
	 * Returns the minimum y coordinate of the bounding box.
	 *
	 * @return The minimum value between the y coordinate and the y coordinate
	 *         plus the height of the bounding box.
	 */
	public float getMinY( )
		{
			return Math.min(y, y + height);
		}
		
		
	/**
	 * Returns the minimum z coordinate of the bounding box.
	 *
	 * @return The minimum value between the z coordinate and the z coordinate
	 *         plus the depth of the bounding box.
	 */
	public float getMinZ( )
		{
			return Math.min(z, z + length);
		}
		
		
	/**
	 * Accesses the width of the bounding box.
	 *
	 * @return The size of the bounding box along the x-axis in world units.
	 */
	public float getWidth( )
		{
			return width;
		}
		
		
	/**
	 * Checks if a point contained inside the bounding box.
	 *
	 * @param x
	 *            The x coordinate of the point (in world coordinates)
	 * @param y
	 *            The y coordinate of the point (in world coordinates)
	 * @param z
	 *            The z coordinate of the point (in world coordinates)
	 * @return Returns true if the point is inside or on the bounding box.
	 */
	public boolean isInside(float x, float y, float z)
		{
			return (this.x <= x) && (x <= (this.x + width)) && (this.y <= y) && (y <= (this.y + height))
			        && (this.z <= z) && (z <= (this.z + length));
		}
}
