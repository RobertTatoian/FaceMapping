package scene3Dabstract;

import java.util.Collection;

/**
 * A graphic object that is composed of multiple graphic objects.
 */
public abstract class ComplexGraphicObject3D<E extends GraphicObject3D> extends GraphicObject3D {

	/**
	 * The collection of sub-parts
	 */
	private Collection<E> objects;

	/**
	 * Instantiates this with a the collection of sub-parts.
	 * @param objects A collection of graphic objects
	 */
	public ComplexGraphicObject3D(Collection<E> objects)
		{
			this.objects = objects;
		}

	/**
	 * Draws the object. By default, it draws each sub-part.
	 */
	public void draw()
		{
			for (GraphicObject3D object : objects)
				{
					object.draw();
				}
		}

	/**
	 * Updates each the internal state. By default it will update each sub-part.
	 */
	public void update()
		{
			for (GraphicObject3D object : objects)
				{
					object.update();
				}
		}


	/**
	 * Accessor for the underlying collection of sub-parts
	 * @return
	 */
	public Collection<E> getCollection()
		{
			return objects;
		}

	/**
	 * Adds an item to the underlying collection of sub-items.
	 * @param obj The graphic object to be added.
	 */
	public void addElement(E obj)
	{
		objects.add(obj);
	}
}
