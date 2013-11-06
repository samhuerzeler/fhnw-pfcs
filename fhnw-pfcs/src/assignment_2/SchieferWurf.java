package assignment_2;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;
import javax.media.opengl.awt.GLCanvas;

public class SchieferWurf implements WindowListener, GLEventListener, KeyListener {

    private static final String TITLE = "Übung 2: Schiefer Wurf";
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 4 * 3;
    private static final int FPS = 60;
    private double viewportWidth;
    private Random random = new Random();
    private boolean enabled; // ball machine enabled
    private ArrayList<TennisBall> balls = new ArrayList<TennisBall>();
    private double frames; // number of time steps/frames
    private double startVelocity; // m/s
    private Vector3d startPosition;
    private double w; // throwing angle
    private int ballsThrown; // number of balls thrown
    private double delay; // time delay (frames)
    private double distribution; // distribution
    private double distrMultiplier;
    TextRenderer textRenderer;

    public SchieferWurf() {
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
        new SchieferWurf();
    }

    private void reset() {
        enabled = true;
        viewportWidth = 60;
        frames = 0;
        startVelocity = 20;
        startPosition = new Vector3d(-40, 8, 0);
        w = 45;
        ballsThrown = 0;
        delay = 10;
        distrMultiplier = 0;
        balls.clear();
    }

    private void showKeyBindings(GLAutoDrawable drawable) {
        textRenderer.setColor(1f, 1f, 1f, 0.7f);
        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("Escape: exit program", 4, drawable.getHeight() - 12);
        textRenderer.draw("Space: reset", 4, drawable.getHeight() - 24);
        textRenderer.draw("up/down: increase/decrease start velocity", 4, drawable.getHeight() - 36);
        textRenderer.draw("left/right: increase/decrease angle", 4, drawable.getHeight() - 48);
        textRenderer.draw("W/A/S/D: change start position", 4, drawable.getHeight() - 60);
        textRenderer.draw("F/C: increase/decrease number of balls thrown", 4, drawable.getHeight() - 72);
        textRenderer.endRendering();
    }

    private void drawCross(GL2 gl) {
        gl.glBegin(GL2.GL_LINES);
        {
            // horizontal
            gl.glVertex3d(startPosition.x - 2, startPosition.y, 0);
            gl.glVertex3d(startPosition.x + 2, startPosition.y, 0);
            // vertical
            gl.glVertex3d(startPosition.x, startPosition.y - 2, 0);
            gl.glVertex3d(startPosition.x, startPosition.y + 2, 0);
        }
        gl.glEnd();
    }

    private void drawFloor(GL2 gl) {
        gl.glBegin(GL2.GL_LINES);
        {
            gl.glVertex3d(-viewportWidth, 0, 0);
            gl.glVertex3d(viewportWidth, 0, 0);
        }
        gl.glEnd();
    }

    private double getRandomDistribution() {
        return (-distrMultiplier + (random.nextInt() % (distrMultiplier + 1)));
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

        ++frames;
        Time.update();

        gl.glPushMatrix();
        {
            gl.glTranslated(0, -15, 0);
            gl.glColor3d(0.5, 0.5, 0.5);

            drawCross(gl);
            drawFloor(gl);

            if (enabled) {
                // generate new ball and add to balls list if delay is over
                if (frames % delay == 0) {
                    distribution = getRandomDistribution();
                    double angle = w + distribution;
                    // TODO create float color
                    Color color = new Color(255, 237, 44);
                    TennisBall ball = new TennisBall(startPosition, startVelocity, angle, color);
                    balls.add(ball);
                    frames = 0;
                    System.out.println("Balls thrown:" + ++ballsThrown);
                }
            }

            // update balls
            for (int i = 0; i < balls.size(); ++i) {
                TennisBall ball = balls.get(i);
                ball.update();
                ball.draw(gl);
                // remove ball if not in horizontal viewport
                if (ball.getX() < -viewportWidth
                        || ball.getX() > viewportWidth
                        || ball.getFramesLived() > 2000) {
                    balls.remove(ball);
                    ball = null;
                }
            }
        }
        gl.glPopMatrix();

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
            case KeyEvent.VK_SPACE:
                reset();
                break;
            case KeyEvent.VK_RIGHT:
                w -= 2;
                w = w % 360;
                break;
            case KeyEvent.VK_LEFT:
                w += 2;
                w = w % 360;
                break;
            case KeyEvent.VK_DOWN:
                startVelocity -= 1;
                if (startVelocity < 3) {
                    startVelocity = 5;
                }
                break;
            case KeyEvent.VK_UP:
                startVelocity += 1;
                if (startVelocity > 300) {
                    startVelocity = 300;
                }
                break;
            case KeyEvent.VK_C:
                delay += 1;
                if (delay > 50) {
                    delay = 50;
                }
                break;
            case KeyEvent.VK_F:
                delay -= 1;
                if (delay < 2) {
                    delay = 2;
                }
                break;
            case KeyEvent.VK_Q:
                distrMultiplier -= 1;
                if (distrMultiplier < 0) {
                    distrMultiplier = 0;
                }
                break;
            case KeyEvent.VK_E:
                distrMultiplier += 1;
                if (distrMultiplier > 40) {
                    distrMultiplier = 40;
                }
                break;
            case KeyEvent.VK_W:
                startPosition.y += 0.4;
                break;
            case KeyEvent.VK_S:
                startPosition.y -= 0.4;
                if (startPosition.y <= 2) {
                    startPosition.y = 2;
                }
                break;
            case KeyEvent.VK_A:
                startPosition.x -= 0.4;
                break;
            case KeyEvent.VK_D:
                startPosition.x += 0.4;
                break;
            case KeyEvent.VK_T:
                enabled = !enabled;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
