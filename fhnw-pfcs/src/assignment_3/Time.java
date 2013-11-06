package assignment_3;

public class Time {

    private static long currentTime;
    private static long lastTime;

    public static void update() {
        lastTime = currentTime;
        currentTime = getTime();
    }

    public static long getTime() {
        return System.nanoTime();
    }

    public static double getDelta() {
        return (currentTime - lastTime) / 1000000000.0;
    }

    public static void init() {
        lastTime = getTime();
        currentTime = getTime();
    }
}
