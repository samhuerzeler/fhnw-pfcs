package assignment_5;

import assignment_5.ParticleFlow.FlowDynamics;
import javax.media.opengl.GL2;

public class Particle {

    private static final double RADIUS = 0.05;
    private double x = 0;
    private double y = 0;
    private double speed = 0.01;
    private int framesLived = 0;

    public Particle(double x, double y, double speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void draw(GL2 gl) {
        gl.glColor3d(1.0, 0.9, .02);
        int circlePoints = 3;
        double circleStepSize = 2.0 * Math.PI / circlePoints;
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < circlePoints; i++) {
            gl.glVertex2d(x + RADIUS * Math.cos(i * circleStepSize), y + RADIUS * Math.sin(i * circleStepSize));
        }
        gl.glEnd();
    }

    public void move(FlowDynamics d) {
        double[] v = {x, y};
        double[] p = d.move(v, speed);
        x = p[0];
        y = p[1];

        ++framesLived;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getFramesLived() {
        return framesLived;
    }
}