package assignment_4;

import Util.Time;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Gyroscope implements GLEventListener, KeyListener, WindowListener {

    private static final String TITLE = "Übung 4: Gyroskop";
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 4 * 3;
    private static final int FPS = 60;
    double left = -8, right = 8;
    double bottom, top;
    double near = -100, far = 100;
    double elevation = -90;
    double azimut = 45;
    double distance = 10;
    double phi = 60;
    double omega = 3;
    private TextRenderer textRenderer;

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
    }

    public static void main(String[] args) {
        Gyroscope gyros = new Gyroscope();
    }

    private void rotateCam(GL2 gl, double elev, double azim) {
        gl.glRotated(elev, 1, 0, 0);
        gl.glRotated(-azim, 0, 1, 0);
    }

    private void drawBase(GL2 gl, GLUT glut, double height) {
        gl.glPushMatrix();
        {
            gl.glColor3d(1, 1, 1);
            glut.glutSolidCylinder(0.08, -height, 64, 64);
        }
        gl.glPopMatrix();
    }

    private void drawGyroscope(GL2 gl, GLUT glut, double phi, double size) {
        gl.glPushMatrix();
        {
            gl.glTranslated(-0.05, 0, -(size / 2));

            // stab
            gl.glColor3d(1, 1, 1);
            glut.glutSolidCylinder(0.08, size, 64, 64);

            // gewicht
            gl.glColor3d(1, 1, 1);
            drawWeight(gl, glut, 0.3, 0.2, size * 56 / 60);
            gl.glColor3d(1, 1, 1);
            drawWeight(gl, glut, 0.2, 0.2, size * 50 / 60);
            gl.glRotated(phi * 180 / Math.PI, 0, 0, 1);

            // gyroscope
            gl.glColor3d(1, 1, 1);
            glut.glutSolidCylinder(1.2, 0.25, 20, 20);
        }
        gl.glPopMatrix();
    }

    private void drawWeight(GL2 gl, GLUT glut, double size, double thickness, double pos) {
        gl.glPushMatrix();
        {
            gl.glTranslated(0, 0, pos);
            glut.glutSolidCylinder(size, thickness, 64, 64);
        }
        gl.glPopMatrix();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GLLightingFunc.GL_NORMALIZE);
        gl.glEnable(GLLightingFunc.GL_LIGHT0);
        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 12));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLUT glut = new GLUT();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);

        gl.glLoadIdentity();

        Time.update();

        gl.glRotated(-90, 1, 0, 0);
        phi = (phi + omega * Time.getDelta()) % 360;

        gl.glPushMatrix();
        {
            drawBase(gl, glut, 3);
            rotateCam(gl, elevation, azimut);
            drawGyroscope(gl, glut, phi, 6);
        }
        gl.glPopMatrix();

        // draw hotkeys
        textRenderer.setColor(1f, 1f, 1f, 0.7f);
        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("Escape: exit program", 4, drawable.getHeight() - 12);
        textRenderer.draw("up/down: increase/decrease elevation", 4, drawable.getHeight() - 24);
        textRenderer.draw("left/right: increase/decrease azimut", 4, drawable.getHeight() - 36);
        textRenderer.draw("W/w: increase/decrease omega", 4, drawable.getHeight() - 48);
        textRenderer.endRendering();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        double aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_UP:
                elevation = elevation - 3;
                break;
            case KeyEvent.VK_DOWN:
                elevation = elevation + 3;
                break;
            case KeyEvent.VK_LEFT:
                azimut = azimut - 3;
                break;
            case KeyEvent.VK_RIGHT:
                azimut = azimut + 3;
                break;
        }
        switch (e.getKeyChar()) {
            case 'w':
                omega = omega - 2;
                break;
            case 'W':
                omega = omega + 2;
                break;
        }

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
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
}