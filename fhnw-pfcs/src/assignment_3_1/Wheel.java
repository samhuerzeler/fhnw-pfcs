package assignment_3_1;

import javax.media.opengl.GL2;

public class Wheel {

    public double x;
    public double y;
    public double z;
    public double width;
    public double height;
    public double depth;
    public double rotation;

    public Wheel(double x, double y, double z, double width, double depth, double rotation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = width;
        this.depth = depth;
        this.rotation = rotation;
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(x, y, z);
        gl.glRotated(rotation, 0, 0, 1);
        gl.glBegin(GL2.GL_LINE_LOOP);
        int nPkte = 40;
        double radius = width;
        double timeStep = 2.0 * Math.PI / nPkte;
        for (int i = 0; i < nPkte; i++) {
            gl.glVertex3d(radius * Math.sin(i * timeStep),
                    0,
                    radius * Math.cos(i * timeStep));
        }
        gl.glEnd();
        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i < nPkte; i++) {
            gl.glVertex3d(radius * Math.sin(i * timeStep),
                    depth,
                    radius * Math.cos(i * timeStep));
        }
        gl.glEnd();
        gl.glPopMatrix();
    }
}
