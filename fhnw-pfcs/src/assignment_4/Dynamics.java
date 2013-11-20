package assignment_4;

public abstract class Dynamics {

    private double[] y1, y2, y3, y4, ym, xx;

    public double[] euler(double[] x, double dt) {
        xx = new double[x.length];
        y1 = f(x);
        for (int i = 0; i < x.length; ++i) {
            xx[i] = x[i] + y1[i] * dt;
        }
        return xx;
    }

    public abstract double[] f(final double[] x);

    public double[] runge(double[] x, double dt) {
        y1 = f(x);
        xx = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y1[i] * dt / 2; // xx = x+y1*dt/2
        }

        y2 = f(xx); // zweiter hilfsvektor
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y2[i] * dt / 2; // xx = x+y2*dt/2
        }

        y3 = f(xx); // dritter hilfsvektor
        for (int i = 0; i < x.length; i++) {
            xx[i] = x[i] + y3[i] * dt; // xx = x+y2*dt (ohne 1/2)
        }

        y4 = f(xx); // vierter hilfsvektor

        ym = new double[x.length]; // gemittelter vektor
        for (int i = 0; i < x.length; i++) {
            ym[i] = (y1[i] + 2 * y2[i] + 2 * y3[i] + y4[i]) / 6;
            xx[i] = x[i] + ym[i] * dt; // xx = x + ym * dt
        }

        return xx;
    }
}
