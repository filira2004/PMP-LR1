package com.example.laba1;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public final class GraphExtremaHelper {

    private GraphExtremaHelper() {
    }

    public static List<PointF> findLocalExtrema(List<Double> xs, List<Double> ys) {
        List<PointF> out = new ArrayList<>();
        int n = xs.size();
        if (n != ys.size() || n < 3) {
            return out;
        }
        for (int i = 1; i < n - 1; i++) {
            double y0 = ys.get(i - 1);
            double y1 = ys.get(i);
            double y2 = ys.get(i + 1);
            if (y1 > y0 && y1 > y2) {
                out.add(new PointF(xs.get(i).floatValue(), (float) y1));
            } else if (y1 < y0 && y1 < y2) {
                out.add(new PointF(xs.get(i).floatValue(), (float) y1));
            }
        }
        return out;
    }
}
