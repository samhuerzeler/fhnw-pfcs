package assignment_6;

import Util.Vector3d;

public class Quaternion {

    public double scal;
    public Vector3d vec;

    public Quaternion(double s, double v1, double v2, double v3) {
        this(s, new Vector3d(v1, v2, v3));
    }

    public Quaternion(double s, Vector3d v) {
        scal = s;
        vec = v;
    }

    public Quaternion norm() {
        double sum = scal * scal + vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
        double abs = Math.sqrt(sum);
        return new Quaternion(scal / abs, vec.x / abs, vec.y / abs, vec.z / abs);
    }

    @Override
    public String toString() {
        return "q[" + scal + "," + vec + "]";
    }
}
