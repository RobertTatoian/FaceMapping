
package scene3Dabstract;


/**
 * Created by wpgodone on 12/21/2015.
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
		
		
	public float getHeight( )
		{
			return height;
		}
		
		
	public float getLength( )
		{
			return length;
		}
		
		
	public float getMaxX( )
		{
			return x + width;
		}
		
		
	public float getMaxY( )
		{
			return y + height;
		}
		
		
	public float getMaxZ( )
		{
			return z + length;
		}
		
		
	public float getMinX( )
		{
			return x;
		}
		
		
	public float getMinY( )
		{
			return y;
		}
		
		
	public float getMinZ( )
		{
			return z;
		}
		
		
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
