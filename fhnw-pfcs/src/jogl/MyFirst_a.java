package jogl;

//  -------------   JOGL SampleProgram  (Fadenkreuz) ------------
import com.jogamp.opengl.util.FPSAnimator;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;

public class MyFirst_a implements WindowListener, GLEventListener {

    private final double g = 9.81; // gravity
    double xm = 4; // x koord. ball
    double ym = 5; // y koord. ball
    double v = -0.5; // geschwindigkeit
    double a = -g; // beschleunigung
    double r = 0.5;
    double dt = 0.015; // s

    //  ------------------  Methoden  --------------------
    void zeichneAchsen(GL2 gl) // Koordinatenachsen zeichnen
    {
        gl.glBegin(gl.GL_LINES);
        gl.glVertex2d(-10, 0);        // x-Achse
        gl.glVertex2d(10, 0);
        gl.glVertex2d(0, -10);        // y-Achse
        gl.glVertex2d(0, 10);
        gl.glEnd();
    }

    void drawCircle(GL2 gl, double r, double xm, double ym) {
        int nPkte = 40;                                          // Anzahl Punkte
        double dt = 2.0 * Math.PI / nPkte;                         // Parameter-Schrittweite
        gl.glBegin(gl.GL_POLYGON);
        for (int i = 0; i < nPkte; i++) {
            gl.glVertex2d(xm + r * Math.cos(i * dt), // x = r*cos(i*dt)
                    ym + r * Math.sin(i * dt));                        // y = r*sin(i*dt-phi)
        }
        gl.glEnd();
    }

    public MyFirst_a() // Konstruktor
    {
        Frame f = new Frame("MyFirst_animation");
        f.setSize(800, 600);
        f.addWindowListener(this);
        GLCanvas canvas = new GLCanvas();                         // OpenGL-Window
        canvas.addGLEventListener(this);
        f.add(canvas);
        f.setVisible(true);
        FPSAnimator anim = new FPSAnimator(canvas, 60, true);
        anim.start();
    }

    public static void main(String[] args) // main-Methode der Applikation
    {
        new MyFirst_a();
    }

    //  ---------  OpenGL-Events  -----------------------
    public void init(GLAutoDrawable drawable) {
        GL gl0 = drawable.getGL();                               // OpenGL-Objekt
        GL2 gl = gl0.getGL2();
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);                // erasing color
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glClear(gl.GL_COLOR_BUFFER_BIT); // Bild loeschen
        gl.glColor3d(0.5, 0.5, 0.5);        // Zeichenfarbe
        zeichneAchsen(gl);
        gl.glColor3d(1, 1, 1);              // Zeichenfarbe

        drawCircle(gl, r, xm, ym);
        if (ym <= r) {
            v = -v;
        } else {
            v += a * dt;
        }
        ym += v * dt;

    }

    public void reshape(GLAutoDrawable drawable, // Window resized
            int x, int y,
            int width, int height) {
        GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glViewport(0, 0, width, height);                     // Window
        double aspect = (double) height / width;
        double left = -10, right = 10;
        double bottom = aspect * left, top = aspect * right;
        double near = -100, far = 100;
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);             // ViewingVolume
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    //  ---------  Window-Events  --------------------
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
}
