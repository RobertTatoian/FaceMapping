package scene3Dabstract;

import java.util.Collection;

/**
 * Created by wpgodone on 12/22/2015.
 */
public abstract class ComplexGraphicObject3D<E extends GraphicObject3D> extends GraphicObject3D {

	private Collection<E> objects;

	public ComplexGraphicObject3D(Collection<E> objects)
		{
			this.objects = objects;
		}

	public void draw()
		{
			for (GraphicObject3D object : objects)
				{
					object.draw();
				}
		}

	public void update()
		{
			for (GraphicObject3D object : objects)
				{
					object.update();
				}
		}


	public Collection<E> getCollection()
		{
			return objects;
		}

	public void addElement(E obj)
	{
		objects.add(obj);
	}
}
