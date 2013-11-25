package Util;

public class Vector3d {

    public double x;
    public double y;
    public double z;

    public Vector3d() {
        this(1.0, 1.0, 1.0);
    }

    public Vector3d(Vector3d vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d cross(Vector3d v1, Vector3d v2) {
        return new Vector3d(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x);
    }

    public double dot(Vector3d v1, Vector3d v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
}
