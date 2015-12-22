package Abstract;

import processing.core.PGraphics;
import processing.core.PMatrix;
import processing.core.PVector;
import processing.opengl.PGraphics3D;

/**
 * Created by wpgodone on 12/21/2015.
 */
public abstract class SimpleGraphicObject3D extends GraphicObject3D {

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
