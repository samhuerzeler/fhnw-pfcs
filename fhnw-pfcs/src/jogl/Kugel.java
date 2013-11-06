package jogl;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;

public class Kugel implements WindowListener, GLEventListener, KeyListener {

    GLCanvas canvas;
    double left = -10, right = 10;
    double bottom, top;
    double near = -100, far = 100;
    double elev = 10;
    double azim = 40;
    double dist = 4;
    double rot = 0;
    double rot2 = 0;
    double rotStep = 1;
    private GLUT glut = new GLUT();

    void rotateCam(GL2 gl, double phi, double nx, double ny, double nz) {
        gl.glRotated(-phi, nx, ny, nz);
    }

    void translateCam(GL2 gl, double dx, double dy, double dz) {
        gl.glTranslated(dx, dy, dz);
    }

    double[] cross(double[] u, double[] v) {
        double[] n = {u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
        return n;
    }

    double[] normal(double[] A, double[] B, double[] C) {
        double[] u = {B[0] - A[0], B[1] - A[1], B[2] - A[2]};
        double[] v = {C[0] - A[0], C[1] - A[1], C[2] - A[2]};
        return cross(u, v);
    }

    void zeichneAchsen(GL2 gl, double a) {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(a, 0, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, a, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, 0, a);
        gl.glEnd();
    }

    private void drawShpere(GL2 gl, double x, double y, double z, double radius, int slices, int stacks) {
        gl.glTranslated(x, y, z);
        glut.glutSolidSphere(radius, slices, stacks);
    }

    public Kugel() {
        Frame f = new Frame("MySecond");
        canvas = new GLCanvas();
        f.setSize(800, 600);
        f.setBackground(Color.gray);
        f.addWindowListener(this);
        f.addKeyListener(this);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        f.add(canvas);
        f.setVisible(true);
        FPSAnimator anim = new FPSAnimator(canvas, 60, true);
        anim.start();
    }

    public static void main(String[] args) {
        new Kugel();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glShadeModel(GL2.GL_FLAT); // FLAT or SMOOTH
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        float[] lightPos = {-10, 150, 100, 1};
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glColor3d(0, 1, 1);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glColor3d(1, 1, 1);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // kamerasystem
        translateCam(gl, 0, 0, 2);
        rotateCam(gl, -elev, 1, 0, 0);
        rotateCam(gl, azim, 0, 1, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        // objektsystem
        gl.glDisable(GL2.GL_LIGHTING);
        zeichneAchsen(gl, 6);
        gl.glEnable(GL2.GL_LIGHTING);

        // earth
        gl.glPushMatrix();
        gl.glRotated(rot2, 0, 1, 0);
        gl.glTranslated(6, 0, 0);
        gl.glRotated(rot, 0, 1, 0);
        gl.glRotated(-90, 1, 0, 0);
        drawShpere(gl, 0, 0, 0, 0.3, 50, 50);
        gl.glPopMatrix();

        // sun
        gl.glRotated(-90, 1, 0, 0);
        drawShpere(gl, 0, 0, 0, 3, 50, 50);

        rot += rotStep;
        ++rot2;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        double aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_W:
                elev += 3;
                break;
            case KeyEvent.VK_S:
                elev -= 3;
                break;
            case KeyEvent.VK_A:
                azim -= 3;
                break;
            case KeyEvent.VK_D:
                azim += 3;
                break;
            case KeyEvent.VK_UP:
                rotStep++;
                break;
            case KeyEvent.VK_DOWN:
                rotStep--;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}