package assignment_3_1;

import Util.Time;
import Util.Vector3d;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Color;
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

public class AutoInKurve implements WindowListener, KeyListener, GLEventListener {

    private static final String TITLE = "Übung 3: Auto in Kurve";
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 4 * 3;
    private static final int FPS = 60;
    private double viewportWidth = 40;
    private Car car;
    private double maxCentripetalForce = 2 * 9.81;
    TextRenderer textRenderer;
    double carWidth = 10;
    double elevation = -60;
    double azimut = 20;

    public AutoInKurve() {
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

    public static void main(String args[]) {
        new AutoInKurve();
    }

    void rotateCam(GL2 gl, double phi, double nx, double ny, double nz) {
        gl.glRotated(-phi, nx, ny, nz);
    }

    void translateCam(GL2 gl, double dx, double dy, double dz) {
        gl.glTranslated(dx, dy, dz);
    }

    private void showKeyBindings(GLAutoDrawable drawable) {
        textRenderer.setColor(1f, 1f, 1f, 0.7f);
        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("Escape: exit program", 4, drawable.getHeight() - 12);
        textRenderer.draw("Space: reset", 4, drawable.getHeight() - 24);
        textRenderer.draw("up/down: increase/decrease velocity", 4, drawable.getHeight() - 36);
        textRenderer.draw("left/right: change wheel rotation", 4, drawable.getHeight() - 48);
        textRenderer.draw("R: show helper lines", 4, drawable.getHeight() - 60);
        textRenderer.endRendering();
    }

    private void drawLine(GL2 gl, double a) {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(0, 0);
        gl.glVertex2d(a, 0);
        gl.glEnd();
        gl.glTranslated(0, -10, 0);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(0, 0);
        gl.glVertex2d(a, 0);
        gl.glEnd();
        gl.glTranslated(0, 10, 0);
    }

    private void drawStreet(GL2 gl) {
        gl.glPushMatrix();
        gl.glColor3d(1.0, 1.0, 1.0);
        gl.glTranslated(-viewportWidth, 20, 0);
        double a = 0.8;
        for (int i = 0; i < 50; i++) {
            drawLine(gl, a);
            gl.glTranslated(a, 0, 0);
        }
        double phi = 0, deltaPhi = 0.09;
        for (int i = 0; i < 30; i++) {
            drawLine(gl, a);
            gl.glTranslated(a, 0, 0);
            gl.glRotated(-phi, 0, 0, 1);
            phi += deltaPhi;
        }
        for (int i = 0; i < 30; i++) {
            drawLine(gl, a);
            gl.glTranslated(a, 0, 0);
            gl.glRotated(-phi, 0, 0, 1);
        }
        for (int i = 0; i < 30; i++) {
            drawLine(gl, a);
            gl.glTranslated(a, 0, 0);
            gl.glRotated(-phi, 0, 0, 1);
            phi -= deltaPhi;
        }
        for (int i = 0; i < 70; i++) {
            drawLine(gl, a);
            gl.glTranslated(a, 0, 0);
        }
        gl.glPopMatrix();
    }

    private void reset() {
        car = new Car(new Vector3d(-viewportWidth + carWidth, 16, 0), carWidth, Color.RED);
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

        // camera system
        gl.glPushMatrix();
        Vector3d carPos = car.getPosition();
        translateCam(gl, -2, 0, 0);
        rotateCam(gl, -elevation, 1, 0, 0);
        rotateCam(gl, azimut, 0, 0, 1);

        // object system
        Time.update();

        drawStreet(gl);
        car.update();
        car.draw(gl);
        gl.glPopMatrix();
        if (car.getCentripetalForce() > maxCentripetalForce) {
            gl.glColor3d(1, 0, 0);
        } else {
            gl.glColor3d(0, 1, 0);
        }
        gl.glBegin(GL2.GL_POLYGON);
        {
            double viewportHeight = viewportWidth * DISPLAY_HEIGHT / DISPLAY_WIDTH;
            gl.glVertex3d(-viewportWidth, -viewportHeight, 0);
            gl.glVertex3d(-viewportWidth + car.getCentripetalForce() / 2, -viewportHeight, 0);
            gl.glVertex3d(-viewportWidth + car.getCentripetalForce() / 2, -viewportHeight + 2, 0);
            gl.glVertex3d(-viewportWidth, -viewportHeight + 2, 0);
        }
        gl.glEnd();

        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("rotation angle: " + car.getRotation(), 10, 44);
        textRenderer.draw("velocity: " + car.getVelocity(), 10, 32);
        textRenderer.draw("centripetal force: " + car.getCentripetalForce(), 10, 20);
        textRenderer.endRendering();

        showKeyBindings(drawable);
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
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
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
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SPACE:
                reset();
                break;
            case KeyEvent.VK_UP:
                car.increaseVelocity(1);
                break;
            case KeyEvent.VK_DOWN:
                car.decreaseVelocity(1);
                break;
            case KeyEvent.VK_LEFT:
                car.turn(Car.Direction.LEFT, 2);
                break;
            case KeyEvent.VK_RIGHT:
                car.turn(Car.Direction.RIGHT, 2);
                break;
            case KeyEvent.VK_R:
                car.debugging = !car.debugging;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
