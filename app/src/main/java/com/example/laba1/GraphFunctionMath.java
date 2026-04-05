package com.example.laba1;

public final class GraphFunctionMath {

    public static final double X_MIN = -Math.PI * 2;
    public static final double X_MAX = Math.PI * 2;
    public static final double Y_MIN = -12.0;
    public static final double Y_MAX = 12.0;
    public static final int SAMPLES = 800;

    private GraphFunctionMath() {
    }

    public static Double evalY(double x) {
        double half = x / 2.0;
        if (Math.abs(Math.cos(half)) < 1e-6) {
            return null;
        }
        double y = Math.tan(half) * Math.cos(3.0 * x);
        if (!Double.isFinite(y)) {
            return null;
        }
        if (y < Y_MIN || y > Y_MAX) {
            return null;
        }
        return y;
    }
}
