package jogl;

//  -------------   JOGL SampleProgram  (Fadenkreuz) ------------
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;

public class MyFirst implements WindowListener, GLEventListener {

    //  ------------------  Methoden  --------------------
    void zeichneAchsen(GL2 gl) // Koordinatenachsen zeichnen
    {
        gl.glBegin(gl.GL_LINES);
        gl.glVertex2d(-1, 0);        // x-Achse
        gl.glVertex2d(1, 0);
        gl.glVertex2d(0, -1);        // y-Achse
        gl.glVertex2d(0, 1);
        gl.glEnd();
    }

    void zeichneKreis(GL2 gl, double r) // Kreis um den Nullpunkt
    {
        int nPkte = 40;                                          // Anzahl Punkte
        double dt = 2.0 * Math.PI / nPkte;                         // Parameter-Schrittweite
        gl.glBegin(gl.GL_LINE_LOOP);
        for (int i = 0; i < nPkte; i++) {
            gl.glVertex2d(r * Math.cos(i * dt), // x = r*cos(i*dt)
                    r * Math.sin(i * dt));                        // y = r*sin(i*dt-phi)
        }
        gl.glEnd();
    }

    public MyFirst() // Konstruktor
    {
        Frame f = new Frame("MyFirst");
        f.setSize(800, 600);
        f.addWindowListener(this);
        GLCanvas canvas = new GLCanvas();                         // OpenGL-Window
        canvas.addGLEventListener(this);
        f.add(canvas);
        f.setVisible(true);
    }

    public static void main(String[] args) // main-Methode der Applikation
    {
        new MyFirst();
    }

    //  ---------  OpenGL-Events  -----------------------
    public void init(GLAutoDrawable drawable) {
        GL gl0 = drawable.getGL();                               // OpenGL-Objekt
        GL2 gl = gl0.getGL2();
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);                // erasing color
    }

    public void display(GLAutoDrawable drawable) {
        GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);                     // Bild loeschen
        gl.glColor3d(0.5, 0.5, 0.5);                                    // Zeichenfarbe
        zeichneAchsen(gl);
        gl.glColor3d(1, 1, 1);                                  // Zeichenfarbe
        zeichneKreis(gl, 0.4);
        zeichneKreis(gl, 0.2);
    }

    public void reshape(GLAutoDrawable drawable, // Window resized
            int x, int y,
            int width, int height) {
        GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glViewport(0, 0, width, height);                     // Window
        double aspect = (double) height / width;
        double left = -1, right = 1;
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
