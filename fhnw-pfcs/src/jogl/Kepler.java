package jogl;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;
import Util.Dynamics;
import Util.Time;

public class Kepler implements WindowListener, GLEventListener, KeyListener {

    private GLCanvas canvas;
    private double left = -100, right = 100;
    private double bottom, top;
    private double near = -150, far = 150;
    private double dist = 100;
    private double elev = 10;
    private double azim = 40;
    private GLUT glut = new GLUT();
    private KeplerDynamics kd = new KeplerDynamics();
    private double dt = 60;
    // längeneinheit E = 10e-6
    private double g = 9.81e-6;
    private double rE = 6.378; // erdradius
    private double rS = rE * 0.2; // radius satellit
    private double GM = g * rE * rE;
    private double h = 35.68;
    private double r = rE + h;
    private double v = Math.sqrt(GM / r);
    private double[] x = {r, 0, 0, 0.6 * v}; // x1, x2, v1, v2

    class KeplerDynamics extends Dynamics {

        public KeplerDynamics() {
        }

        @Override
        public double[] f(double[] x) {
            double x1 = x[0], x2 = x[1], v1 = x[2], v2 = x[3];
            double r = Math.sqrt(x1 * x1 + x2 * x2);
            double r3 = r * r * r;
            double[] y = {v1, v2, -GM / r3 * x1, -GM / r3 * x2};
            return y;
        }
    }

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

    void drawEarth(GL2 gl) {
        gl.glPushMatrix();
        gl.glRotated(-90, 1, 0, 0);
        glut.glutSolidSphere(rE, 30, 30);
        gl.glPopMatrix();
    }

    void drawAxes(GL2 gl, double a) {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(a, 0, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, a, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, 0, a);
        gl.glEnd();
    }

    public Kepler() {
        Frame f = new Frame("Kepler");
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
        Time.init();
    }

    public static void main(String[] args) {
        new Kepler();
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
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glColor3d(0, 1, 1);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glColor3d(1, 1, 1);

        Time.update();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // camera system
        translateCam(gl, 0, 0, dist);
        rotateCam(gl, -elev, 1, 0, 0);
        rotateCam(gl, azim, 0, 1, 0);
        float[] lightPos = {-10, 150, 100, 1};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        // object system
        gl.glDisable(GL2.GL_LIGHTING);
        drawAxes(gl, 60);
        gl.glEnable(GL2.GL_LIGHTING);

        // earth
        drawEarth(gl);

        // moon
        gl.glPushMatrix();
        gl.glTranslated(x[1], x[2], x[0]);
        glut.glutSolidSphere(rS, 10, 10);
        x = kd.runge(x, dt);
        gl.glPopMatrix();
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
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}