package scene3Dabstract;

import processing.core.PGraphics;
import processing.core.PMatrix;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

/**
 * Specifies the operations of an abstract GraphicObject3D, which: - optionally
 * has a reference to some other GraphicObject3D which is called the parent
 * here. - has a rotation and translation relative to the parent if it is not
 * null, or relative to the world coordinates otherwise - can be drawn - can be
 * updated - can determine if it contains some point given in world coordinates
 */
public abstract class GraphicObject3D {

	/**
	 * Determines if the given point (in world-coordinates) is inside this object.
	 *
	 * Note that this method is guaranteed to be fast, but necessarily accurate.
	 * If a bounding box is used, there may be significant chances of false
	 * positives depending on how much of the negative space there is in the
	 * bounding box.
	 *
	 * @param x The x coordinate of the point (in world coordinates)
	 * @param y The y coordinate of the point (in world coordinates)
	 * @param z The z coordinate of the point (in world coordinates)
	 *
	 * @return true if this GraphicObject3D contains the point, false otherwise
	 */
	public abstract boolean isInside(float x, float y, float z);

	/**
	 * Draws this object
	 */
	public abstract void draw();

	/**
	 * Updates the internal state of this object
	 */
	public abstract void update();

	/**
	 * Returns the current parent.
	 * 
	 * If null is returned, the object's reference frame is relative to the world.
	 *
	 * @return A reference to the parent, a GraphicObject3D, if it exists, null
	 * otherwise.
	 */
	public abstract GraphicObject3D getParent();

	/**
	 * Returns the current rotation about the x-axis (relative to the reference
	 * frame of the parent).
	 *
	 * @return The angle of ration in degrees
	 */
	public abstract float getRotationX();

	/**
	 * Returns the current rotation about the y-axis (relative to the reference
	 * frame of the parent).
	 *
	 * @return The angle of ration in degrees
	 */
	public abstract float getRotationY();

	/**
	 * Returns the current rotation about the z-axis (relative to the reference
	 * frame of the parent).
	 *
	 * @return The angle of ration in degrees
	 */
	public abstract float getRotationZ();

	/**
	 * Returns the translation along the x-axis (in world units).
	 *
	 * @return The x-axis translation in coordinates of the parent's reference
	 * frame if the parent exists or in the world's coordinates otherwise.
	 */
	public abstract float getTranslationX();

	/**
	 * Returns the translation along the y-axis (in world units).
	 *
	 * @return The y-axis translation in coordinates of the parent's reference
	 * frame if the parent exists or in the world's coordinates otherwise.
	 */
	public abstract float getTranslationY();

	/**
	 * Returns the translation along the z-axis (in world units).
	 *
	 * @return The z-axis translation in coordinates of the parent's reference
	 * frame if the parent exists or in the world's coordinates otherwise.
	 */
	public abstract float getTranslationZ();

	/**
	 * Set the translation of the object (relative to the parent's refernce frame)
	 * along the x-axis.
	 * 
	 * @param x The amount of translation (in world units).
	 */
	public abstract void setTranslationX(float x);

	/**
	 * Sets the translation of the object (relative to the parent's reference frame)
	 * along the y-axis.
	 *
	 * @param y The amount of translation (in world units).
	 */
	public abstract void setTranslationY(float y);

	/**
	 * Sets the translation of the object (relative to the parent's reference frame)
	 * along the x-axis.
	 *
	 * @param z The amount of translation (in world units).
	 */
	public abstract void setTranslationZ(float z);


	/**
	 * Sets the rotation of the object about the x-axis (relative to the parent's
	 * reference frame).
	 *
	 * @param angle The amount of rotation (in world degrees).
	 */
	public abstract void setRotationX(float angle);

	/**
	 * Sets the rotation of the object about the y-axis (relative to the parent's
	 * reference frame).
	 *
	 * @param angle The amount of rotation (in world degrees).
	 */
	public abstract void setRotationY(float angle);

	/**
	 * Sets the rotation of the object about the z-axis (relative to the parent's
	 * reference frame).
	 *
	 * @param angle The amount of rotation (in world degrees).
	 */
	public abstract void setRotationZ(float angle);

	/**
	 * Returns the transformation matrix from the parent's reference frame to this
	 * object's reference frame.
	 * 
	 * @return A 4-dimensional matrix (that is, PMatrix3d)
	 */
	public PMatrix getTransformationMatrix()
		{
			// Use PGraphics to get the transformation matrix. Might induce some overhead,
			// but it's much simpler.
			PGraphics g = new PGraphics3D();
			g.translate(getTranslationX(), getTranslationY(), getTranslationZ());
			g.rotateX(getRotationX());
			g.rotateY(getRotationY());
			g.rotateZ(getRotationZ());

			return g.getMatrix();
		}

	public PMatrix getRotationMatrix()
	{
		// Use PGraphics to get the transformation matrix. Might induce some overhead,
		// but it's much simpler.
		PGraphics g = new PGraphics3D();
		g.rotateX(getRotationX());
		g.rotateY(getRotationY());
		g.rotateZ(getRotationZ());

		return g.getMatrix();
	}

	/**
	 * Converts a point in the parent's coordinate system to this object's reference
	 * frame.
	 */
	public PVector parentToRelativeCoordinates(float x, float y, float z)
		{
			return parentToRelativeCoordinates(new PVector(x, y, z));
		}

	/**
	 * Converts a point in the parent's coordinate system to this object's reference
	 * frame.
	 */
	public PVector parentToRelativeCoordinates(PVector pos)
		{
			if (getParent() != null)
				{
					pos = parentToRelativeCoordinates(pos);
				}

			PMatrix transMatrix = getTransformationMatrix();
			return transMatrix.mult(pos, null);
		}

	/**
	 * Converts a point in this object's reference frame to the reference frame of
	 * the parent.
	 */
	public PVector relativeToParentCoordinates(float x, float y, float z)
	{
		return parentToRelativeCoordinates(new PVector(x, y, z));
	}

	/**
	 * Converts a point in this object's reference frame to the reference frame of
	 * the parent.
	 */
	public PVector relativeToParentCoordinates(PVector pos)
	{
		if (getParent() != null)
		{
			pos = parentToRelativeCoordinates(pos);
		}

		PMatrix transMatrix = getTransformationMatrix();
		return transMatrix.mult(pos, null);
	}
}
