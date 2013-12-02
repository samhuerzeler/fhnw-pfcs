package assignment_4;

import Util.Dynamics;
import Util.Time;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

public class Gyroscope implements WindowListener, KeyListener, GLEventListener {

    private static final String TITLE = "Übung 4: Gyroskop";
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 4 * 3;
    private static final int FPS = 60;
    private double viewportWidth = 40;
    TextRenderer textRenderer;
    private double elev = 10;
    private double azim = 40;
    private double dist = 4;
    private double cylRadius = 15;
    private double cylLength = 4;
    private double cylMass = 1.2d;
    private GLUT glut = new GLUT();
    private GyroDynamics gd = new GyroDynamics();

    private class GyroDynamics extends Dynamics {

        private final double Ix = (1.0 / 4.0) * cylMass * Math.pow(cylRadius, 2) + (1.0 / 12.0) * cylMass * Math.pow(cylLength, 2);
        private final double Iy = Ix;
        private final double Iz = (1.0 / 2.0) * cylMass * Math.pow(cylRadius, 2);
        private double[] omega = {10, 0, 0};
        private double[] q = {1, 0, 0, 0};
        private double[] x = new double[omega.length + q.length];
        private double phi;
        private double[] a = new double[3];
        private double dt = 0.03;

        public GyroDynamics() {
            // init x
            System.arraycopy(omega, 0, x, 0, omega.length);
            System.arraycopy(q, 0, x, omega.length, q.length);
        }

        private void calc() {
            x = runge(x, dt);
            // normalize q
            double sum = 0.0;
            for (int i = 3; i <= 6; i++) {
                sum += x[i] * x[i];
            }
            double qq = Math.sqrt(sum);
            for (int i = 3; i <= 6; i++) {
                x[i] = x[i] / qq;
            }

            phi = Math.acos(x[3]);
            sum = 0.0;
            for (int i = 4; i <= 6; i++) {
                sum += x[i] * x[i];
            }
            qq = Math.sqrt(sum);
            for (int i = 0; i <= 2; i++) {
                a[i] = x[i + 3] / qq;
            }
        }

        public void draw(GL2 gl) {
            gl.glPushMatrix();
            gl.glRotated(phi, a[0], a[1], a[2]);
            glut.glutWireCylinder(cylRadius, cylLength, 30, 30);
            gl.glPopMatrix();
            // calculate next step
            calc();

        }

        @Override
        public double[] f(double[] x) {

            if (x.length > 7) {
                throw new IndexOutOfBoundsException("System should be R7, not R" + x.length);
            }

            double[] xx = new double[x.length];

            xx[0] = ((Iy - Iz) / Ix) * x[1] * x[2]; // w1
            xx[1] = ((Iz - Ix) / Iy) * x[2] * x[0]; // w2
            xx[2] = ((Ix - Iy) / Iz) * x[0] * x[1]; // w3
            xx[3] = (-1.0 / 2.0) * (x[4] * x[0] + x[5] * x[1] + x[6] * x[2]); // q0
            xx[4] = (1.0 / 2.0) * (x[3] * x[0] + x[5] * x[2] + x[6] * x[1]); // q1
            xx[5] = (1.0 / 2.0) * (x[3] * x[1] + x[6] * x[1] + x[4] * x[2]); // q2
            xx[6] = (1.0 / 2.0) * (x[3] * x[2] + x[4] * x[1] + x[5] * x[0]); // q3

            return xx;
        }
    }

    public Gyroscope() {
        Frame f = new Frame(TITLE);
        f.setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        f.addWindowListener(this);
        f.addKeyListener(this);
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        f.add(canvas);
        f.setVisible(true);
        FPSAnimator anim = new FPSAnimator(canvas, FPS, true);
        anim.start();
        Time.init();
        reset();
    }

    public static void main(String[] args) {
        new Gyroscope();
    }

    private void reset() {
    }

    void rotateCam(GL2 gl, double phi, double nx, double ny, double nz) {
        gl.glRotated(-phi, nx, ny, nz);
    }

    void translateCam(GL2 gl, double dx, double dy, double dz) {
        gl.glTranslated(dx, dy, dz);
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

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 12));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        Time.update();

        // camera system
        translateCam(gl, 0, 0, dist);
        rotateCam(gl, -elev, 1, 0, 0);
        rotateCam(gl, azim, 0, 1, 0);

        drawAxes(gl, 10);

        // object system
        gl.glRotated(-30, azim, azim, azim);
        gd.draw(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        double aspect = (double) height / width;
        double left = -viewportWidth;
        double right = viewportWidth;
        double bottom = left * aspect;
        double top = right * aspect;
        double near = -100, far = 100;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SPACE:
                reset();
                break;
            case KeyEvent.VK_W:
                elev++;
                break;
            case KeyEvent.VK_S:
                elev--;
                break;
            case KeyEvent.VK_A:
                azim--;
                break;
            case KeyEvent.VK_D:
                azim++;
                break;
        }
    }
}
