package Abstract;

import java.awt.*;
import java.util.Stack;

/**
 * Created by wpgodone on 11/13/2015.
 */
public abstract class SimpleGraphicObject extends GraphicObject {

    /**
     * Converts a point in some GraphicObject's reference frame to its child's
     * reference frame (given an identifier to the child).
     *
     * A reference to the parent is not used by this method.
     *
     * @param p The point (in the parent's reference frame) to be converted
     * @param child The child GraphicObject
     * @return A Point.Float in the new reference frame.
     */
    public static Point.Float parentToChildCoord(Point.Float p, GraphicObject child) {
        float a = child.getRotation();
        float x = child.getTranslateX();
        float y = child.getTranslateY();

        return new Point.Float((p.x - x) * (float)Math.cos(a) + (p.y - y) * (float)Math.sin(a),
                               -(p.x - x) * (float)Math.sin(a) + (p.y - y) * (float)Math.cos(a));
    }

    /**
     * Converts the point (given in the reference frame of the child) to the
     * reference frame of the parent.
     *
     * A reference to the parent is not used by this method.
     *
     * @param p The point (in the child's reference frame) to be converted
     * @param child The child GraphicObject
     * @return A Point.Float in the new reference frame.
     */
    public static Point.Float childToParentCoord(Point.Float p, GraphicObject child) {
        float a = child.getRotation();
        float x = child.getTranslateX();
        float y = child.getTranslateY();

        return new Point.Float(p.x * (float)Math.cos(a) - p.y * (float)Math.sin(a) + x,
                               p.x * (float)Math.sin(a) + p.y * (float)Math.cos(a) + y);
    }

    /**
     * Converts a point in world coordinates to the reference frame of the given
     * GraphicObject.
     *
     * @param x The x coordinate of the point
     * @param y The y coordinate of the point
     * @param obj The GraphicObject.
     * @return A Point.Float in the new reference frame
     */
    public static Point.Float worldToRelativeCoord(float x, float y,
                                             GraphicObject obj) {
        GraphicObject predecessor = obj;
        Stack<GraphicObject> links = new Stack<GraphicObject>();
        Point.Float p = new Point.Float(x, y);

        while (predecessor != null) {
            links.push(predecessor);
            predecessor = predecessor.getParent();
        }

        while (!links.isEmpty()) {
            p = parentToChildCoord(p, links.pop());
        }
        return p;
    }

    /**
     * Converts a point in relative coordinate to world coordinates.
     *
     * @param x The x coordinate of the point
     * @param y The y coordinate of the point
     * @param obj A GraphicObject in the same reference frame as the point
     * @return A Point.Float in the new reference frame
     */
    public static Point.Float relativeToWorldCoord(float x, float y,
                                             GraphicObject obj) {
        Point.Float p = new Point.Float(x, y);

        while (obj != null) {
            p = childToParentCoord(p, obj);
            obj = obj.getParent();
        }
        return p;
    }
}
