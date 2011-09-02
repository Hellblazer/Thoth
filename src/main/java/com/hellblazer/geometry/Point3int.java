package com.hellblazer.geometry;

import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;

public class Point3int extends Point3i {
    private static final long serialVersionUID = 1L;

    public Point3int() {
        super();
    }

    public Point3int(int x, int y, int z) {
        super(x, y, z);
    }

    public Point3int(int[] t) {
        super(t);
    }

    public Point3int(Tuple3i t1) {
        super(t1);
    }

    /**
     * Returns the distance between this point and point p1.
     * 
     * @param p1
     *            the other point
     * @return the distance
     */
    public final int distance(Point3i p1) {
        int dx, dy, dz;

        dx = x - p1.x;
        dy = y - p1.y;
        dz = z - p1.z;
        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Computes the L-1 (Manhattan) distance between this point and point p1.
     * The L-1 distance is equal to: abs(x1-x2) + abs(y1-y2) + abs(z1-z2).
     * 
     * @param p1
     *            the other point
     * @return the L-1 distance
     */
    public final int distanceL1(Point3i p1) {
        return Math.abs(x - p1.x) + Math.abs(y - p1.y) + Math.abs(z - p1.z);
    }

    /**
     * Computes the L-infinite distance between this point and point p1. The
     * L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2)].
     * 
     * @param p1
     *            the other point
     * @return the L-infinite distance
     */
    public final int distanceLinf(Point3i p1) {
        double tmp;
        tmp = Math.max(Math.abs(x - p1.x), Math.abs(y - p1.y));

        return (int) Math.max(tmp, Math.abs(z - p1.z));
    }

    /**
     * Returns the square of the distance between this point and point p1.
     * 
     * @param p1
     *            the other point
     * @return the square of the distance
     */
    public final int distanceSquared(Point3i p1) {
        int dx, dy, dz;

        dx = x - p1.x;
        dy = y - p1.y;
        dz = z - p1.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Sets the value of this tuple to the scalar division of itself.
     * 
     * @param s
     *            the scalar value
     */
    public final void scaleInverse(double s) {
        x /= s;
        y /= s;
        y /= s;
    }

    /**
     * Sets the value of this tuple to the scalar division of tuple t1 by s.
     * 
     * @param s
     *            the scalar value
     * @param t1
     *            the source tuple
     */
    public final void scaleInverse(int s, Tuple3i t1) {
        x = t1.x / s;
        y = t1.y / s;
        z = t1.z / s;
    }
}
