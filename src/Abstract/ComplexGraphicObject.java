package Abstract;

import java.util.Collection;

public abstract class ComplexGraphicObject extends GraphicObject {
    /**
     * A collection of this GraphicObject's subparts.
     */
    private Collection<GraphicObject> children;

    /**
     * Default constructor
     */
    public ComplexGraphicObject() {
    }

    /**
     * Determines if the point is any subparts.
     * @param x The x coordinate of the point (in world coordinates)
     * @param y The y coordinate of the point (in world coordinates)
     */
    @Override
    public boolean isInside(float x, float y) {
        if (children == null) {
            return false;
        }

        for (GraphicObject obj : children) {
            if (obj.isInside(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draws each supbart
     */
    @Override
    public void draw() {
        if (children == null) {
            return;
        }
        for (GraphicObject obj : children) {
            obj.draw();
        }
    }

    /**
     * Updates each subpart
     */
    @Override
    public void update() {
        if (children == null) {
            return;
        }
        for (GraphicObject obj : children) {
            obj.update();
        }
    }

    /**
     * Returns the collection of subparts.
     */
    public Collection<GraphicObject> getChildren() {
        return children;
    }
}
