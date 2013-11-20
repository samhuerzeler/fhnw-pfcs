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

    public Vector3d cross(Vector3d vec1, Vector3d vec2) {
        // TODO...
        Vector3d vec3 = new Vector3d();



        return vec3;
    }

    public double dot(Vector3d vec1, Vector3d vec2) {
        // TODO...
        return 0.0;
    }
}
