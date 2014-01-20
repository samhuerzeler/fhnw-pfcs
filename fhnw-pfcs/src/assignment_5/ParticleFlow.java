package assignment_5;

import Util.Dynamics;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ParticleFlow implements GLEventListener, KeyListener, WindowListener {

    private static final String TITLE = "Übung 5: Partikelfluss";
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 4 * 3;
    private static final int FPS = 60;
    private double left = -25, right = 25;
    private double near = -100, far = 100;
    private static double r = 7.0;
    private ArrayList<Particle> particles = new ArrayList<>();
    private int waveCount = 0;
    private int waveDistance = 5;
    private double particleDistance = 0.4;
    private double particleCount = 10;
    private double startY = (-particleDistance * particleCount / 2) + (particleDistance / 2);
    private double startX = -20;
    private double speed = 0.1;
    private FlowDynamics flowDynamics = new FlowDynamics(r);
    private TextRenderer textRenderer;

    @SuppressWarnings("LeakingThisInConstructor")
    public ParticleFlow() {
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
    }

    public final class FlowDynamics extends Dynamics {

        private double r2;
        private double r4;

        public FlowDynamics(double r) {
            setRadius(r);
        }

        public void setRadius(double r) {
            r2 = r * r;
            r4 = r2 * 2;
        }

        @Override
        public double[] f(double[] x) {
            assert x.length == 2 : "Vektor muss 2 Komponenten enthalten";
            double xy2 = x[0] * x[0] + x[1] * x[1];
            double xy22 = xy2 * xy2;
            double X = 1 + r2 / xy2 - (r4 * x[0] * x[0]) / xy22;
            double Y = -(r4 * x[0] * x[1]) / xy22;
            double[] result = {X, Y};
            return result;
        }
    }

    public static void main(String[] args) {
        ParticleFlow flow = new ParticleFlow();
    }

    private void reset() {
        particles.clear();
        r = 7;
        flowDynamics.setRadius(r);
        particleDistance = 0.4;
        particleCount = 10;
        startY = (-particleDistance * particleCount / 2) + (particleDistance / 2);
        startX = -20;
        speed = 0.1;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT + GL.GL_DEPTH_BUFFER_BIT);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        if (waveCount++ % waveDistance == 0) {
            for (int i = 0; i < particleCount; i++) {
                particles.add(new Particle(startX, (i * particleDistance + startY), speed));
            }
        }
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle particle = it.next();
            particle.move(flowDynamics);
            particle.draw(gl);
            if (particle.getX() > right || particle.getFramesLived() > 1000) {
                it.remove();
            }
        }

        // draw circle
        gl.glColor3d(1, 1, 1);
        int circlePoints = 40;
        double circleStepSize = 2.0 * Math.PI / circlePoints;
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < circlePoints; i++) {
            gl.glVertex2d(0 + r * Math.cos(i * circleStepSize), 0 + r * Math.sin(i * circleStepSize));
        }
        gl.glEnd();

        // draw hotkeys
        textRenderer.setColor(1f, 1f, 1f, 0.7f);
        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("Escape: exit program", 4, drawable.getHeight() - 12);
        textRenderer.draw("Space: reset program", 4, drawable.getHeight() - 24);
        textRenderer.draw("up/down: increase/decrease speed", 4, drawable.getHeight() - 36);
        textRenderer.draw("right/left: increase/decrease particle amount", 4, drawable.getHeight() - 48);
        textRenderer.draw("Q/E: increase/decrease circle radius", 4, drawable.getHeight() - 60);
        textRenderer.draw("A/D: increase/decrease distance between particles", 4, drawable.getHeight() - 72);
        textRenderer.endRendering();
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 12));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        double aspect = (double) height / width;
        double bottom = aspect * left, top = aspect * right;
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
            case KeyEvent.VK_SPACE:
                reset();
                break;
            case KeyEvent.VK_UP:
                if (speed < 0.3) {
                    speed += 0.01;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (speed > 0.08) {
                    speed -= 0.01;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (particleCount > 2) {
                    startY += particleDistance;
                    particleCount -= 2;
                }
                break;
            case KeyEvent.VK_RIGHT:
                startY -= particleDistance;
                particleCount += 2;
                break;
            case KeyEvent.VK_Q:
                if (r < 10) {
                    r += 0.5;
                    flowDynamics.setRadius(r);
                }
                break;
            case KeyEvent.VK_E:
                if (r > 1) {
                    r -= 0.5;
                    flowDynamics.setRadius(r);
                }
                break;
            case KeyEvent.VK_A:
                if (particleDistance > 0.2) {
                    particleDistance -= 0.1;
                    startY = (-particleDistance * particleCount / 2) + (particleDistance / 2);
                }
                break;
            case KeyEvent.VK_D:
                particleDistance += 0.1;
                startY = (-particleDistance * particleCount / 2) + (particleDistance / 2);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
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