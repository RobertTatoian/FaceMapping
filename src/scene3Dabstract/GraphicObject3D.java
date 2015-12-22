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
	 * Determines if the given point (in world-coordinates) is inside this.
	 *
	 * Note that this method is guaranteed to be fast, but necessarily accurate.
	 * If a bounding box is used, there may be significant chances of false
	 * positives depending on how much of the negative space there is in the
	 * bounding box.
	 *
	 * @param x The x coordinate of the point (in world coordinates)
	 * @param y The y coordinate of the point (in world coordinates)
	 *
	 * @return true if this GraphicObject3D contains the point, false otherwise
	 */
	public abstract boolean isInside(float x, float y, float z);

	/**
	 * Draws this GraphicObject3D
	 */
	public abstract void draw();

	/**
	 * Updates the internal state of this GraphicObject3D
	 */
	public abstract void update();

	/**
	 * Returns the current parent
	 *
	 * @return A reference to the parent, a GraphicObject3D, if it exists, null
	 * otherwise.
	 */
	public abstract GraphicObject3D getParent();

	/**
	 * Returns the current rotation (relative to the reference frame of the
	 * parent).
	 *
	 * @return The angle of ration in radians
	 */
	public abstract float getRotationX();

	public abstract float getRotationY();

	public abstract float getRotationZ();

	/**
	 * Returns the translation along the x-axis.
	 *
	 * @return The x-axis translation in coordinates of the parent's reference
	 * frame if the parent exists or in the world's coordinates otherwise.
	 */
	public abstract float getTranslationX();

	/**
	 * Returns the translation along the y-axis.
	 *
	 * @return The y-axis translation in coordinates of the parent's reference
	 * frame if the parent exists or in the world's coordinates otherwise.
	 */
	public abstract float getTranslationY();

	public abstract float getTranslationZ();

	public abstract void setTranslationX(float x);

	public abstract void setTranslationY(float y);

	public abstract void setTranslationZ(float z);

	public abstract void setRotationX(float angle);

	public abstract void setRotationY(float angle);

	public abstract void setRotationZ(float angle);

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

	public PVector worldToRelativeCoordinates(float x, float y, float z)
		{
			return worldToRelativeCoordinates(new PVector(x, y, z));
		}

	public PVector worldToRelativeCoordinates(PVector pos)
		{
			if (getParent() != null)
				{
					pos = worldToRelativeCoordinates(pos);
				}

			PMatrix transMatrix = getTransformationMatrix();
			transMatrix.invert();
			return transMatrix.mult(pos, null);
		}
}
