package assignment_2;

public class Physics {

    private static final double GRAVITY = 9.81;
    private static final double AIR_DENSITY = 1.2041; // luftdichte kg/m^3
    private static final double CW = 0.4; // stroemungswiderstandskoeffizient

    public static double getGravity() {
        return GRAVITY;
    }

    public static double getAirDensity() {
        return AIR_DENSITY;
    }

    public static double getCw() {
        return CW;
    }
}
