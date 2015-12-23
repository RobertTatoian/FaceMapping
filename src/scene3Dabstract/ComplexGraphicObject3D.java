
package scene3Dabstract;


import java.util.Collection;


/**
 * A graphic object that is composed of multiple graphic objects.
 *
 * @author Robert Tatoian
 * @author Warren Godone-Maresca
 * @version 1.0
 */
public abstract class ComplexGraphicObject3D <E extends GraphicObject3D> extends GraphicObject3D {

	/**
	 * The collection of sub-parts
	 */
	private final Collection <E> objects;


	/**
	 * Instantiates this with a the collection of sub-parts.
	 *
	 * @param objects
	 *            A collection of graphic objects
	 */
	public ComplexGraphicObject3D(Collection <E> objects)
		{
			this.objects = objects;
		}


	/**
	 * Adds an item to the underlying collection of sub-items.
	 *
	 * @param obj
	 *            The graphic object to be added.
	 */
	public void addElement(E obj)
		{
			objects.add(obj);
		}


	/**
	 * Draws the object. By default, it draws each sub-part.
	 */
	@Override
	public void draw( )
		{
			for (final GraphicObject3D object : objects)
				{
					object.draw();
				}
		}


	/**
	 * Accessor for the underlying collection of sub-parts
	 *
	 * @return Returns the collection of objects from the collection
	 */
	public Collection <E> getCollection( )
		{
			return objects;
		}


	/**
	 * Updates each the internal state. By default it will update each sub-part.
	 */
	@Override
	public void update( )
		{
			for (final GraphicObject3D object : objects)
				{
					object.update();
				}
		}
}
