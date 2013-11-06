package assignment_3_1;

import java.awt.Color;
import javax.media.opengl.GL2;

public class Car {

    public enum Direction {

        LEFT, RIGHT
    }
    public boolean debugging = false;
    private Vector3d position;
    private double velocity;
    private Color color;
    private double width;
    private double depth;
    private double height;
    private double rotation; // current rotation of the car
    private Wheel frontLeftWheel;
    private Wheel frontRightWheel;
    private Wheel rearLeftWheel;
    private Wheel rearRightWheel;
    private Wheel innerWheel; // current inner front wheel in a curve
    private Wheel outerWheel; // current outer front wheel in a curve
    private double wheelRotation; // current rotation of the wheel relative to the car
    private double maxWheelRotation = 36;
    private double track; // distance between the centerline of two wheels on the same axe
    private double wheelBase; // distance between the centers of the front and rear wheels
    private double turnRadius; // distance to the turn point
    double sign = 1;

    public Car() {
        this(new Vector3d(0, 0, 0), 10);
    }

    public Car(Vector3d position, double width) {
        this(position, width, Color.WHITE);
    }

    public Car(Vector3d position, double width, Color color) {
        this(position, 0, width, color);
    }

    public Car(Vector3d position, double velocity, double width, Color color) {
        this.position = new Vector3d(position);
        this.velocity = velocity;
        this.width = width;
        this.height = width / 4;
        this.depth = this.width / 2.5;
        this.color = color;
        rotation = 0;
        wheelRotation = 0;
        turnRadius = 0;

        // init wheels
        double wheelWidth = width / 10;
        double wheelDepth = depth / 10;

        track = 2 * (depth - wheelDepth);
        wheelBase = width - wheelWidth;
        frontLeftWheel = new Wheel(wheelWidth / 2 + wheelBase, track / 2 + wheelDepth * 2, 0, wheelWidth, wheelDepth, 0);
        frontRightWheel = new Wheel(wheelWidth / 2 + wheelBase, -wheelDepth, 0, wheelWidth, wheelDepth, 0);
        rearLeftWheel = new Wheel(wheelWidth / 2, track / 2 + wheelDepth * 2, 0, wheelWidth, wheelDepth, 0);
        rearRightWheel = new Wheel(wheelWidth / 2, -wheelDepth, 0, wheelWidth, wheelDepth, 0);
        innerWheel = frontLeftWheel;
    }

    public void turn(Direction direction, int step) {
        if (direction == Car.Direction.LEFT) {
            wheelRotation += step;
            if (wheelRotation > maxWheelRotation) {
                wheelRotation = maxWheelRotation;
            }
        } else if (direction == Car.Direction.RIGHT) {
            wheelRotation -= step;
            if (wheelRotation < -maxWheelRotation) {
                wheelRotation = -maxWheelRotation;
            }
        }

        sign = 1; // negative if innerwheel = left wheel
        if (wheelRotation > 0) {
            sign = -1;
            innerWheel = frontLeftWheel;
            outerWheel = frontRightWheel;
        } else if (wheelRotation < 0) {
            sign = 1;
            innerWheel = frontRightWheel;
            outerWheel = frontLeftWheel;
        } else {
            sign = 1;
            frontLeftWheel.rotation = 0;
            frontRightWheel.rotation = 0;
            turnRadius = 0;
        }

        innerWheel.rotation = wheelRotation;
        turnRadius = -0.5 * (1 / Math.tan(Math.toRadians(wheelRotation))) * (track * sign * Math.tan(Math.toRadians(wheelRotation)) - 2 * wheelBase);
        outerWheel.rotation = Math.toDegrees(Math.atan(wheelBase / (turnRadius - (track * sign))));
    }

    public void increaseVelocity(double amt) {
        velocity += amt;
    }

    public void decreaseVelocity(double amt) {
        velocity -= amt;
    }

    private double getYM() {
        return (track / 2) + sign * (wheelBase / Math.tan(Math.toRadians(innerWheel.rotation)));
    }

    public double getCentripetalForce() {
        return Math.abs((velocity * velocity) / getYM());
    }

    public double getVelocity() {
        return this.velocity;
    }

    public double getRotation() {
        return this.rotation;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void update() {
        double carRotation = Math.toRadians(rotation);
        if (innerWheel.rotation == 0) {
            position.x += velocity * Math.cos(carRotation) * Time.getDelta();
            position.y += velocity * Math.sin(carRotation) * Time.getDelta();
        } else {
            double dPhi = velocity * Time.getDelta() / turnRadius;
            position.x += turnRadius * (Math.sin(carRotation + dPhi) - Math.sin(carRotation));
            position.y -= turnRadius * (Math.cos(carRotation + dPhi) - Math.cos(carRotation));

            rotation = (rotation + Math.toDegrees(dPhi)) % 360;
        }
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();
        {
            gl.glColor3d(color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255);
            gl.glTranslated(position.x, position.y, position.z);
            gl.glRotated(rotation, 0, 0, 1);

            double carOffsetX = -innerWheel.width / 2;
            gl.glPushMatrix();
            {
                gl.glTranslated(carOffsetX, 0, 0);

                // chassis
                gl.glBegin(GL2.GL_LINE_LOOP);
                {
                    gl.glVertex3d(0, 0, 0);
                    gl.glVertex3d(0, depth, 0);
                    gl.glVertex3d(0 + width, depth, 0);
                    gl.glVertex3d(0 + width, 0, 0);
                }
                gl.glEnd();
                gl.glBegin(GL2.GL_LINE_LOOP);
                {
                    gl.glVertex3d(0, 0, height);
                    gl.glVertex3d(0, depth, height);
                    gl.glVertex3d(0 + width, depth, height);
                    gl.glVertex3d(0 + width, 0, height);
                }
                gl.glEnd();

                gl.glBegin(GL2.GL_LINES);
                {
                    gl.glVertex3d(0, 0, 0);
                    gl.glVertex3d(0, 0, height);
                    gl.glVertex3d(0, depth, 0);
                    gl.glVertex3d(0, depth, height);
                    gl.glVertex3d(0 + width, depth, 0);
                    gl.glVertex3d(0 + width, depth, height);
                    gl.glVertex3d(0 + width, 0, 0);
                    gl.glVertex3d(0 + width, 0, height);
                }
                gl.glEnd();

                // wheels
                frontLeftWheel.draw(gl);
                frontRightWheel.draw(gl);
                rearLeftWheel.draw(gl);
                rearRightWheel.draw(gl);

                // axis front
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(frontLeftWheel.width / 2 + wheelBase, track / 2 + frontLeftWheel.depth * 2, 0);
                gl.glVertex3d(frontLeftWheel.width / 2 + wheelBase, -frontLeftWheel.depth, 0);
                gl.glPopMatrix();

                // axis rear
                gl.glVertex3d(rearLeftWheel.width / 2, track / 2 + rearLeftWheel.depth * 2, 0);
                gl.glVertex3d(rearLeftWheel.width / 2, -rearLeftWheel.depth, 0);
                gl.glEnd();
            }
            gl.glPopMatrix();

            /**
             * debug
             */
            if (debugging) {
                gl.glColor3d(1, 1, 1);

                // turn point
                gl.glBegin(GL2.GL_LINE_LOOP);
                int nPkte = 40;
                double radius = 0.3;
                double timeStep = 2.0 * Math.PI / nPkte;
                for (int i = 0; i < nPkte; i++) {
                    gl.glVertex2d(radius * Math.cos(i * timeStep),
                            turnRadius + radius * Math.sin(i * timeStep));
                }
                gl.glEnd();

                // wheel base
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3d(carOffsetX + rearLeftWheel.width / 2, depth / 2, 0);
                gl.glVertex3d(carOffsetX + rearLeftWheel.width / 2 + wheelBase, depth / 2, 0);

                // rear to turn point
                gl.glVertex3d(0, depth / 2, 0);
                gl.glVertex3d(0, turnRadius, 0);

                // front left to turn point
                gl.glVertex3d(carOffsetX + frontLeftWheel.width / 2 + wheelBase, track / 2 + frontLeftWheel.depth * 2, 0);
                gl.glVertex3d(0, turnRadius, 0);

                // front right to turn point
                gl.glVertex3d(carOffsetX + frontLeftWheel.width / 2 + wheelBase, -frontLeftWheel.depth, 0);
                gl.glVertex3d(0, turnRadius, 0);
                gl.glEnd();
            }
        }
        gl.glPopMatrix();
    }
}
