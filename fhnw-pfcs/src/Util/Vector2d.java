package Util;

public class Vector2d {

    public double x;
    public double y;

    public Vector2d() {
        this(1.0, 1.0);
    }

    public Vector2d(Vector2d vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d norm() {
        double abs = Math.sqrt(x * x + y * y);
        return new Vector2d(x / abs, y / abs);
    }

    @Override
    public Vector2d clone() {
        return new Vector2d(x, y);
    }

    @Override
    public String toString() {
        return "Vector2d[x: " + x + ", y: " + y + "]";
    }
}
