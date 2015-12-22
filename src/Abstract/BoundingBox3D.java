package Abstract;

/**
 * Created by wpgodone on 12/21/2015.
 */
public class BoundingBox3D {

	private float x, y, z;

	private float width, height, length;

	public BoundingBox3D(float x, float y, float z, float width, float height, float length)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.length = length;
	}


	public boolean isInside(float x, float y, float z)
	{
		return this.x <= x && x <= this.x + width
						&& this.y <= y && y <= this.y + height
						&& this.z <= z && z <= this.z + length;
	}
}
