package assignment_2;

import Util.Physics;
import Util.Time;
import Util.Vector3d;
import java.awt.Color;
import javax.media.opengl.GL2;

public class TennisBall {

    private double k;
    private Color color = new Color(255, 255, 255);
    private int framesLived = 0;
    private double mass = 0.058; // mass 58g
    private double radius = 0.034; // ball radius 34cm
    private double area = radius * radius * Math.PI; // angestroemte flaeche m^2 
    private Vector3d position;
    private Vector3d velocity;
    private Vector3d acceleration;

    public TennisBall(Vector3d position, double velocity, double w, Color color) {
        this.color = color;
        this.position = new Vector3d(position);

        double vx = velocity * Math.cos(Math.toRadians(w));
        double vy = velocity * Math.sin(Math.toRadians(w));
        this.velocity = new Vector3d(vx, vy, 0);
        acceleration = new Vector3d(0, -Physics.getGravity(), 0);
        k = 0.5 * Physics.getAirDensity() * Physics.getCw() * area;
    }

    public void update() {
        double vxOld = velocity.x;
        double vyOld = velocity.y;
        velocity.x = vxOld + (-k / mass) * Math.sqrt((vxOld * vxOld) + (vyOld * vyOld)) * vxOld * Time.getDelta();
        velocity.y = vyOld + ((-k / mass) * Math.sqrt((vxOld * vxOld) + (vyOld * vyOld)) * vyOld + acceleration.y) * Time.getDelta();
        // ungenauer: position.x = velocity.x * Time.getDelta()
        position.x = position.x + ((vxOld + velocity.x) / 2) * Time.getDelta();
        position.y = position.y + ((vyOld + velocity.y) / 2) * Time.getDelta();
        if (position.y <= radius * 10) {
            position.y = radius * 10;
            velocity.y = -velocity.y;
        } else {
            velocity.y += acceleration.y * Time.getDelta();
        }
        ++framesLived;
    }

    public void draw(GL2 gl) {
        gl.glColor3d((double) color.getRed() / 255, (double) color.getGreen() / 255, (double) color.getBlue() / 255);

        int nPkte = 40;
        double timeStep = 2.0 * Math.PI / nPkte;
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < nPkte; i++) {
            gl.glVertex2d(position.x + (radius * 10) * Math.cos(i * timeStep),
                    position.y + (radius * 10) * Math.sin(i * timeStep));
        }
        gl.glEnd();
    }

    public double getX() {
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    public double getFramesLived() {
        return framesLived;
    }
}
