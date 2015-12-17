package Abstract;

/**
 * Created by wpgodone on 11/14/2015.
 */
public abstract class BoundingBox {

    /**
     * Bounds of the bounding box
     */
    private float xMin, xMax, yMin, yMax;

    /**
     * Instantiates the bounding box with the given coordinates
     */
    public BoundingBox(float xMin, float yMin, float xMax, float yMax) {
        setCoordinates(xMin, yMin, xMax, yMax);
    }

    /**
     * Determines if the box contains a point (which should be in the same
     * reference frame).
     */
    public boolean contains(float x, float y) {
       // TODO System.out.printf("%f < %f < %f and %f < %f < %f\n", xMin, x, xMax, yMin, y, yMax);
        return (xMin <= x && x <= xMax) && (yMin <= y && y <= yMax);
    }

    /**
     * Sets the coordinates of the box
     */
    private void setCoordinates(float xMin, float yMin, float xMax, float yMax) {
        if (xMin > xMax) {
            float temp = xMin;
            xMin = xMax;
            xMax = temp;
        }

        if (yMin > yMax) {
            float temp = yMin;
            yMin = yMax;
            yMax = temp;
        }

        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }


    /** Returns the minimum x-coordinate of the bounding box */
    public float getMinX() {
        return xMin;
    }

    /** Returns the maximum x-coordinate of the bounding box */
    public float getMaxX() {
        return xMax;
    }

    /** Returns the minimum y-coordinate of the bounding box */
    public float getMinY() {
        return yMin;
    }

    /** Returns the maxmimum y-coordinate of the bounding box */
    public float getMaxY() {
        return yMax;
    }

    /** Returns the height of the bounding box */
    public float getHeight() {
        return yMax - yMin;
    }

    /** Returns the width of the bounding box */
    public float getWidth() {
        return xMax - xMin;
    }

    /**
     * A bounding box in world coordinates
     */
    public static class Absolute extends BoundingBox {
        public Absolute(float xMin, float yMin, float xMax, float yMax) {
            super(xMin, yMin, xMax, yMax);
        }
    }

    /**
     * A bounding box in some relative reference frame
     */
    public static class Relative extends BoundingBox {
        public Relative(float xMin, float yMin, float xMax, float yMax) {
            super(xMin, yMin, xMax, yMax);
        }
    }
}
