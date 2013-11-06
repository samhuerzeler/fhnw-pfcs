package assignment_3;

import javax.media.opengl.GL2;

public class Wheel {

    public double x;
    public double y;
    public double z;
    public double width;
    public double height;
    public double rotation;

    public Wheel(double x, double y, double z, double width, double height, double rotation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslated(x, y, z);
        gl.glRotated(rotation, 0, 0, 1);
        gl.glBegin(GL2.GL_LINE_LOOP);
        {
            gl.glVertex3d(-width / 2, -height / 2, 0);
            gl.glVertex3d(-width / 2, height / 2, 0);
            gl.glVertex3d(width / 2, height / 2, 0);
            gl.glVertex3d(width / 2, -height / 2, 0);
        }
        gl.glEnd();
        gl.glPopMatrix();
    }
}
