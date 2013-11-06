package jogl;

//  -------------   JOGL SampleProgram  (Fadenkreuz) ------------
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;

public class MyFirst_c implements WindowListener, GLEventListener, KeyListener {

    //  ------------------  Methoden  --------------------
    void zeichneAchsen(GL2 gl) // Koordinatenachsen zeichnen
    {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(-10, 0);        // x-Achse
        gl.glVertex2d(10, 0);
        gl.glVertex2d(0, -10);        // y-Achse
        gl.glVertex2d(0, 10);
        gl.glEnd();
    }

    void zeichneStrecke(GL2 gl, double s) {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(0, 0);
        gl.glVertex2d(s, 0);
        gl.glEnd();
    }

    public MyFirst_c() // Konstruktor
    {
        Frame f = new Frame("MyFirst_animation");
        f.setSize(800, 600);
        f.addWindowListener(this);
        f.addKeyListener(this);
        GLCanvas canvas = new GLCanvas();                         // OpenGL-Window
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        f.add(canvas);
        f.setVisible(true);
    }

    public static void main(String[] args) // main-Methode der Applikation
    {
        new MyFirst_c();
    }

    //  ---------  OpenGL-Events  -----------------------
    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl0 = drawable.getGL();                               // OpenGL-Objekt
        GL2 gl = gl0.getGL2();
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);                // erasing color
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT); // Bild loeschen
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glColor3d(0.5, 0.5, 0.5);        // Zeichenfarbe
        zeichneAchsen(gl);

        int lines = 10;
        gl.glColor3d(1, 0, 0);              // Zeichenfarbe
        gl.glTranslated(0, 2, 0);
        for (int i = 0; i < lines; ++i) {
            gl.glTranslated(1, 0, 0);
            gl.glRotated(-i, 0, 0, 1);
            zeichneStrecke(gl, 1);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, // Window resized
            int x, int y,
            int width, int height) {
        GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glViewport(0, 0, width, height);                     // Window
        double aspect = (double) height / width;
        double left = -15, right = 15;
        double bottom = aspect * left, top = aspect * right;
        double near = -100, far = 100;
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);             // ViewingVolume
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    //  ---------  Window-Events  --------------------
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
