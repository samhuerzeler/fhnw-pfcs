package jogl;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;
import Util.Dynamics;
import Util.Time;
import Util.Vector3d;
import java.util.ArrayList;

public class Lorenz implements WindowListener, GLEventListener, KeyListener {

    private GLCanvas canvas;
    private double left = -50, right = 50;
    private double bottom, top;
    private double near = -100, far = 100;
    private double elev = 10;
    private double azim = 40;
    private GLUT glut = new GLUT();
    private LorenzDynamics lorenzDynamics = new LorenzDynamics();
    private double[] x = {-20, -10, 10};
    private ArrayList<Vector3d> points = new ArrayList();

    class LorenzDynamics extends Dynamics {

        public LorenzDynamics() {
        }

        @Override
        public double[] f(double[] x) {
            double[] y = new double[3];
            y[0] = 10 * x[1] - 10 * x[0];
            y[1] = 28 * x[0] - x[1] - x[0] * x[2];
            y[2] = x[0] * x[1] - (8.0 / 3.0) * x[2];
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

    void drawPoints(GL2 gl, ArrayList<Vector3d> points) {
        gl.glColor3d(1, 1, 1);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for (int i = 0; i < points.size(); i++) {
            Vector3d p = points.get(i);
            gl.glVertex3d(p.x, p.y, p.z);
        }
        gl.glEnd();
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

    private void drawShpere(GL2 gl, double x, double y, double z, double radius, int slices, int stacks) {
        gl.glTranslated(x, y, z);
        glut.glutSolidSphere(radius, slices, stacks);
    }

    public Lorenz() {
        Frame f = new Frame("Kugel");
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
        new Lorenz();
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

        Time.update();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // camera system
        translateCam(gl, 0, 0, 2);
        rotateCam(gl, -elev, 1, 0, 0);
        rotateCam(gl, azim, 0, 1, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        // object system
        gl.glDisable(GL2.GL_LIGHTING);
        drawAxes(gl, 20);
        gl.glEnable(GL2.GL_LIGHTING);

        // earth
        gl.glPushMatrix();
        gl.glTranslated(x[0], x[1], x[2]);
        drawShpere(gl, 0, 0, 0, 0.3, 20, 20);
        double dt = Time.getDelta() / 2;
        x = lorenzDynamics.runge(x, dt);
        gl.glPopMatrix();

        // draw CHAOS
        points.add(new Vector3d(x[0], x[1], x[2]));
        gl.glDisable(GL2.GL_LIGHTING);
        drawPoints(gl, points);
        gl.glEnable(GL2.GL_LIGHTING);
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