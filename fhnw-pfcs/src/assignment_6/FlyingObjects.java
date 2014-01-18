package assignment_6;

import Util.Time;
import Util.Vector2d;
import Util.Vector3d;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FlyingObjects extends JFrame implements GLEventListener, KeyListener, WindowListener {

    private static final String TITLE = "Übung 6: Frei fliegende Körper";
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 4 * 3;
    private static final int FPS = 60;

    private static enum Type {

        Quader, Torus
    }
    private final double viewportWidth = 200;
    private final float[] lightPos = {-10, 150, 100, 1};
    private static Type objectType = Type.Torus; // Object Type
    private double elev = 0; // Elevation Camera
    private double azim = 45; // Azimut Camera
    private double v0 = 50; // shooting start speed
    private double w = 45; // shooting angle
    private final Vector3d aV0 = new Vector3d(1, 1, 1); // starting angle speed
    private final int bulletDelay = 100; // delay of the bullets
    private final Vector3d startPos = new Vector3d(0, 0, 0); // start position
    private final Vector3d quaderSize = new Vector3d(12, 12, 12); // quader dimension
    private final Vector2d torusSize = new Vector2d(3, 7); // torus dimension
    private final List<Object> bullets = new ArrayList<>();
    private int bulletCount = 0;
    private boolean shift = false;
    private TextRenderer textRenderer;

    public static void main(String[] args) {
        FlyingObjects flyingObjects = new FlyingObjects();
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public FlyingObjects() {
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

    private void drawAxes(GL2 gl) {
        Vector3d start = new Vector3d(0, 0, 0);
        Vector3d end = new Vector3d(100, 0, 0);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(start.x, start.y, start.z);
        gl.glVertex3d(end.x, end.y, end.z);
        gl.glEnd();

        end = new Vector3d(0, 100, 0);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(start.x, start.y, start.z);
        gl.glVertex3d(end.x, end.y, end.z);
        gl.glEnd();

        end = new Vector3d(0, 0, 100);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(start.x, start.y, start.z);
        gl.glVertex3d(end.x, end.y, end.z);
        gl.glEnd();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 12));
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        Time.update();

        rotateCam(gl, elev, azim);

        gl.glTranslated(0, -10, 0);

        gl.glPushMatrix();
        gl.glColor3d(1, 1, 1);
        gl.glRotated(45, 0, 1, 0);
        gl.glDisable(GL2.GL_LIGHTING);
        drawAxes(gl);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopMatrix();
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        if (bulletCount++ % bulletDelay == 0) {
            if (objectType.equals(Type.Quader)) {
                bullets.add(new Cube(startPos.clone(), aV0.clone(), v0, w, quaderSize));
            } else {
                bullets.add(new Torus(startPos.clone(), aV0.clone(), v0, w, torusSize));
            }
        }

        Iterator<Object> it = bullets.iterator();
        while (it.hasNext()) {
            Object b = it.next();
            b.update(Time.getDelta());
            b.draw(gl);
            if (b.getLiveTime() == 0) {
                it.remove();
            }
        }

        // draw info
        textRenderer.setColor(1f, 1f, 1f, 0.7f);
        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("Escape: exit program", 4, drawable.getHeight() - 12);
        textRenderer.draw("up/down: increase/decrease camera elevation", 4, drawable.getHeight() - 24);
        textRenderer.draw("left/right: increase/decrease camera azimut", 4, drawable.getHeight() - 36);
        textRenderer.draw("V/v: increase/decrease velocity", 4, drawable.getHeight() - 48);
        textRenderer.draw("W/w: increase/decrease omega", 4, drawable.getHeight() - 60);
        textRenderer.draw("A/a, B/b, C/c: increase/decrease quader size (x, y, z)", 4, drawable.getHeight() - 72);
        textRenderer.draw("X/x, Y/y, Z/z: increase/decrease angular speed (x, y, z)", 4, drawable.getHeight() - 84);
        textRenderer.endRendering();
    }

    private void rotateCam(GL2 gl, double elev, double azim) {
        gl.glRotated(elev, 1, 0, 0);
        gl.glRotated(-azim, 0, 1, 0);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        GL2 gl = glad.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        double aspect = (double) height / width;
        double left = -viewportWidth / 2;
        double right = viewportWidth;
        double bottom = left * aspect;
        double top = right * aspect;
        double near = -(viewportWidth * 2), far = viewportWidth * 3;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SHIFT:
                shift = true;
                break;
            case KeyEvent.VK_1:
                objectType = Type.Quader;
                break;
            case KeyEvent.VK_2:
                objectType = Type.Torus;
                break;
            case KeyEvent.VK_UP:
                elev += 2;
                elev %= 360;
                break;
            case KeyEvent.VK_DOWN:
                elev -= 2;
                elev %= 360;
                break;
            case KeyEvent.VK_LEFT:
                azim += 2;
                azim %= 360;
                break;
            case KeyEvent.VK_RIGHT:
                azim -= 2;
                azim %= 360;
                break;
            case KeyEvent.VK_V:
                if (shift) {
                    v0 += 0.5;
                } else {
                    v0 -= 0.5;
                }
                if (v0 < 0) {
                    v0 = 0;
                }
                break;
            case KeyEvent.VK_W:
                if (shift) {
                    w += 2;
                } else {
                    w -= 2;
                }
                w %= 360;
                break;
            case KeyEvent.VK_A:
                if (shift) {
                    quaderSize.x += 1;
                } else {
                    quaderSize.x -= 1;
                }
                if (quaderSize.x < 0) {
                    quaderSize.x = 0;
                }
                break;
            case KeyEvent.VK_B:
                if (shift) {
                    quaderSize.y += 1;
                } else {
                    quaderSize.y -= 1;
                }
                if (quaderSize.y < 0) {
                    quaderSize.y = 0;
                }
                break;
            case KeyEvent.VK_C:
                if (shift) {
                    quaderSize.z += 1;
                } else {
                    quaderSize.z -= 1;
                }
                if (quaderSize.z < 0) {
                    quaderSize.z = 0;
                }
                break;
            case KeyEvent.VK_X:
                if (shift) {
                    aV0.x += 1;
                } else {
                    aV0.x -= 1;
                }
                if (aV0.x < 0) {
                    aV0.x = 0;
                }
                break;
            case KeyEvent.VK_Y:
                if (shift) {
                    aV0.y += 1;
                } else {
                    aV0.y -= 1;
                }
                if (aV0.y < 0) {
                    aV0.y = 0;
                }
                break;
            case KeyEvent.VK_Z:
                if (shift) {
                    aV0.z += 1;
                } else {
                    aV0.z -= 1;
                }
                if (aV0.z < 0) {
                    aV0.z = 0;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            shift = false;
        }
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
