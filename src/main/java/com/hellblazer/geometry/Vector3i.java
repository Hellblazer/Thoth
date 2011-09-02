package com.hellblazer.geometry;

import javax.vecmath.Tuple3i;

public class Vector3i extends Tuple3i implements java.io.Serializable {

    // Combatible with 1.1
    static final long serialVersionUID = 3761969948420550442L;

    /**
     * Constructs and initializes a Vector3i to (0,0,0).
     */
    public Vector3i() {
        super();
    }

    /**
     * Constructs and initializes a Vector3i from the specified xyz coordinates.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @param z
     *            the z coordinate
     */
    public Vector3i(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Constructs and initializes a Vector3i from the array of length 3.
     * 
     * @param v
     *            the array of length 3 containing xyz in order
     */
    public Vector3i(int[] v) {
        super(v);
    }

    /**
     * Constructs and initializes a Vector3i from the specified Tuple3f.
     * 
     * @param t1
     *            the Tuple3i containing the initialization x y z data
     */
    public Vector3i(Tuple3i t1) {
        super(t1);
    }

    /**
     * Constructs and initializes a Vector3i from the specified Vector3i.
     * 
     * @param v1
     *            the Vector3i containing the initialization x y z data
     */
    public Vector3i(Vector3i v1) {
        super(v1);
    }

    /**
     * Returns the angle in radians between this vector and the vector
     * parameter; the return value is constrained to the range [0,PI].
     * 
     * @param v1
     *            the other vector
     * @return the angle in radians in the range [0,PI]
     */
    public final double angle(Vector3i v1) {
        double vDot = dot(v1) / (length() * v1.length());
        if (vDot < -1.0) {
            vDot = -1.0;
        }
        if (vDot > 1.0) {
            vDot = 1.0;
        }
        return Math.acos(vDot);
    }

    /**
     * Sets this vector to the vector cross product of vectors v1 and v2.
     * 
     * @param v1
     *            the first vector
     * @param v2
     *            the second vector
     */
    public final void cross(Vector3i v1, Vector3i v2) {
        int x, y;

        x = v1.y * v2.z - v1.z * v2.y;
        y = v2.x * v1.z - v2.z * v1.x;
        z = v1.x * v2.y - v1.y * v2.x;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the dot product of this vector and vector v1.
     * 
     * @param v1
     *            the other vector
     * @return the dot product of this and v1
     */
    public final double dot(Vector3i v1) {
        return x * v1.x + y * v1.y + z * v1.z;
    }

    /**
     * Returns the length of this vector.
     * 
     * @return the length of this vector
     */
    public final double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Returns the squared length of this vector.
     * 
     * @return the squared length of this vector
     */
    public final double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Normalizes this vector to the indicated length.
     */
    public final void normalizeTo(int length) {
        double norm;

        norm = 1.0 / Math.sqrt(x * x + y * y + z * z);
        double x_norm = x * norm;
        double y_norm = y * norm;
        double z_norm = z * norm;

        x = (int) (x_norm * length);
        y = (int) (y_norm * length);
        z = (int) (z_norm * length);
    }

    /**
     * Sets the value of this tuple to the scalar multiplication of the scale
     * factor with this.
     * 
     * @param s
     *            the scalar value
     */
    public final void scale(double s) {
        x *= s;
        y *= s;
        z *= s;
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
     * Sets the value of this tuple to the scalar division of itself.
     * 
     * @param s
     *            the scalar value
     */
    public final void scaleInverse(int s) {
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
