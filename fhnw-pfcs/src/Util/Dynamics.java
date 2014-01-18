package Util;

public abstract class Dynamics {

    public static enum Type {

        Euler, Runge
    }

    public double[] move(double[] x, double dt) {
        return move(x, dt, Type.Runge);
    }

    public double[] move(double[] x, double dt, Type t) {
        switch (t) {
            case Euler:
                return euler(x, dt);
            case Runge:
            default:
                return runge(x, dt);
        }
    }

    public double[] euler(double[] x, double dt) {
        double[] result = new double[x.length];
        double[] y = f(x);
        for (int i = 0; i < x.length; ++i) {
            result[i] = x[i] + y[i] * dt;
        }
        return result;
    }

    public abstract double[] f(final double[] x);

    public double[] runge(double[] x, double dt) {
        double[] result = new double[x.length];

        double[] y1 = f(x); // erster hilfsvektor

        for (int i = 0; i < x.length; i++) {
            result[i] = x[i] + y1[i] * dt / 2; // xx = x+y1*dt/2
        }
        double[] y2 = f(result); // zweiter hilfsvektor

        for (int i = 0; i < x.length; i++) {
            result[i] = x[i] + y2[i] * dt / 2; // xx = x+y2*dt/2
        }
        double[] y3 = f(result); // dritter hilfsvektor

        for (int i = 0; i < x.length; i++) {
            result[i] = x[i] + y3[i] * dt; // xx = x+y2*dt (ohne 1/2)
        }
        double[] y4 = f(result); // vierter hilfsvektor

        double[] ym = new double[x.length]; // gemittelter vektor
        for (int i = 0; i < x.length; i++) {
            ym[i] = (y1[i] + 2 * y2[i] + 2 * y3[i] + y4[i]) / 6;
            result[i] = x[i] + ym[i] * dt; // xx = x + ym * dt
        }

        return result;
    }
}
