package assignment_6;

import Util.Dynamics;
import Util.Physics;
import Util.Vector3d;
import java.awt.Color;

import javax.media.opengl.GL2;

public abstract class Object {

    private final Vector3d acceleration = new Vector3d(0, -Physics.getGravity(), 0);
    private final Vector3d position; // position
    private Vector3d angleSpeed; // angle speed
    private final Vector3d speed; // speed
    private Quaternion orientation; // orientation
    private Quaternion q; // calculation quaternion
    private final RotateDynamics rotateDyn;
    private int timeToLive = 10000;
    protected Color color;

    public Object(Vector3d position, Vector3d angleSpeed, double v0, double angle) {
        // class variables
        this.position = position;
        this.angleSpeed = angleSpeed;

        angle = Math.toRadians(angle);
        speed = new Vector3d(Math.cos(angle) * v0, Math.sin(angle) * v0, 0);

        // quaternion
        q = new Quaternion(1, 0, 0, 0);

        // orientation
        orientation = new Quaternion(0, 0, 0, 0);

        // dynamics
        rotateDyn = new RotateDynamics();
    }

    protected void setDullness(double[] dullness) {
        rotateDyn.I = dullness;
    }

    protected void prepareDraw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(position.x, position.y, position.z);
        gl.glRotated(Math.toDegrees(orientation.scal), orientation.vec.x, orientation.vec.y, orientation.vec.z);
        gl.glColor3d(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    protected void finishDraw(GL2 gl) {
        gl.glPopMatrix();
    }

    public void update(double dt) {
        // rotation
        double[] rotate = {angleSpeed.x, angleSpeed.y, angleSpeed.z, q.scal, q.vec.x, q.vec.y, q.vec.z};
        rotate = rotateDyn.move(rotate, dt);
        angleSpeed = new Vector3d(rotate[0], rotate[1], rotate[2]);
        q = new Quaternion(rotate[3], rotate[4], rotate[5], rotate[6]);
        q = q.norm();
        orientation = new Quaternion(Math.acos(q.scal) * 2, q.vec.norm());

        // position
        position.x += speed.x * dt;
        position.y += speed.y * dt;
        position.z += speed.z * dt;
        speed.x += acceleration.x * dt;
        speed.y += acceleration.y * dt;
        speed.z += acceleration.z * dt;

        timeToLive--;
    }

    public abstract void draw(GL2 gl);

    public int getLiveTime() {
        return timeToLive;
    }

    private class RotateDynamics extends Dynamics {

        private double[] I;

        public RotateDynamics() {
        }

        @Override
        public double[] f(double[] x) {
            double[] w = {x[0], x[1], x[2]};
            double[] q = {x[3], x[4], x[5], x[6]};

            double w1 = 1.0 / I[0] * (I[1] - I[2]) * w[1] * w[2];
            double w2 = 1.0 / I[1] * (I[2] - I[0]) * w[2] * w[0];
            double w3 = 1.0 / I[2] * (I[0] - I[1]) * w[0] * w[1];

            double q0 = -1.0 / 2.0 * (q[1] * w[0] + q[2] * w[1] + q[3] * w[2]);
            double q1 = 1.0 / 2.0 * (q[0] * w[0] + q[2] * w[2] - q[3] * w[1]);
            double q2 = 1.0 / 2.0 * (q[0] * w[1] + q[3] * w[0] - q[1] * w[2]);
            double q3 = 1.0 / 2.0 * (q[0] * w[2] + q[1] * w[1] - q[2] * w[0]);

            double[] res = {w1, w2, w3, q0, q1, q2, q3};
            return res;
        }
    }
}
