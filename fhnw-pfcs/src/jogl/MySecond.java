package jogl;

//  -------------    JOGL SampleProgram  (Pyramide) ------------
//
//     Darstellung einer Pyramide mit Kamera- und Objekt-System
//
import com.jogamp.opengl.util.FPSAnimator;
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;

public class MySecond implements WindowListener, GLEventListener, KeyListener {

    GLCanvas canvas;                                           // OpenGl-Canvas
    double left = -10, right = 10;                            // Koordinatenbereich
    double bottom, top;                                        // werden in reshape gesetzt
    double near = -100, far = 100;                             // Clipping Bereich
    double elev = 10;                                          // Elevation Kamera-System
    double azim = 40;                                          // Azimut Kamera-System
    double dist = 4;                                           // Abstand Kamera von O (ohne Bedeutung
    double rot = 0;
    double rotStep = 1;

    //  ------------------  Methoden  --------------------
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

    void zeichneAchsen(GL2 gl, double a) // Koordinatenachsen zeichnen
    {
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(a, 0, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, a, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, 0, a);
        gl.glEnd();
    }

    void zeichnePyramide(GL2 gl, double a, double h) // Pyramide zeichnen
    {
        double[] A = {a, 0, a};
        double[] B = {a, 0, -a};
        double[] C = {-a, 0, -a};
        double[] D = {-a, 0, a};
        double[] S = {0, h, 0};


        // schwarz
        gl.glColor3d(0, 0, 0);
        gl.glBegin(GL2.GL_POLYGON);                               // Boden
        gl.glNormal3dv(normal(B, A, C), 0);
        gl.glVertex3dv(A, 0);
        gl.glVertex3dv(B, 0);
        gl.glVertex3dv(C, 0);
        gl.glVertex3dv(D, 0);
        gl.glVertex3dv(S, 0);
        gl.glEnd();

        // rot
        gl.glColor3d(1, 0, 0);
        gl.glBegin(GL2.GL_POLYGON);                                // Seitenflaechen
        gl.glNormal3dv(normal(A, B, S), 0);
        gl.glVertex3dv(A, 0);
        gl.glVertex3dv(B, 0);
        gl.glVertex3dv(S, 0);
        gl.glEnd();

        // gelb
        gl.glColor3d(0, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glNormal3dv(normal(B, C, S), 0);
        gl.glVertex3dv(B, 0);
        gl.glVertex3dv(C, 0);
        gl.glVertex3dv(S, 0);
        gl.glEnd();

        // grün
        gl.glColor3d(0, 1, 0);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glNormal3dv(normal(C, D, S), 0);
        gl.glVertex3dv(C, 0);
        gl.glVertex3dv(D, 0);
        gl.glVertex3dv(S, 0);
        gl.glEnd();

        // blau
        gl.glColor3d(0, 0, 1);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glNormal3dv(normal(D, A, S), 0);
        gl.glVertex3dv(D, 0);
        gl.glVertex3dv(A, 0);
        gl.glVertex3dv(S, 0);
        gl.glEnd();
    }

    public MySecond() // Konstruktor
    {
        Frame f = new Frame("MySecond");
        canvas = new GLCanvas();                                // OpenGL-Window
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
    }

    public static void main(String[] args) // main-Methode der Applikation
    {
        new MySecond();
    }

    //  ---------  OpenGL-Events  -----------------------
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);                // erasing color
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_LIGHT0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        float[] lightPos = {-10, 150, 100, 1};
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glColor3d(0, 1, 1);                                    // Zeichenfarbe
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);     // Polygon Zeichen-Modus
        gl.glColor3d(1, 1, 1);                                    // Zeichenfarbe

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // kamerasystem
        translateCam(gl, 0, 0, 2);
        rotateCam(gl, -elev, 1, 0, 0);
        rotateCam(gl, azim, 0, 1, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        // objektsystem
        gl.glDisable(GL2.GL_LIGHTING);
        zeichneAchsen(gl, 6);
        rot += rotStep;
        gl.glRotated(rot, 0, 1, 0);
        gl.glEnable(GL2.GL_LIGHTING);
        zeichnePyramide(gl, 1.5, 1.5);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, // Window resized
            int x, int y,
            int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        double aspect = (float) height / width;                   // aspect-ratio
        bottom = aspect * left;
        top = aspect * right;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);        // Viewing-Volume (im Raum)
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
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
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
            case KeyEvent.VK_UP:
                rotStep++;
                break;
            case KeyEvent.VK_DOWN:
                rotStep--;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}