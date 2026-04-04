package com.example.laba1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Вариант 5: y = tan(x/2) * cos(3*x). Сетка 30×30 px, диапазон x задаётся константами.
 */
public class FunctionGraphView extends View {

    private static final int GRID_STEP_PX = 30;

    /** Диапазон аргумента x (радианы), задаётся в коде */
    private static final double X_MIN = -Math.PI * 2;
    private static final double X_MAX = Math.PI * 2;

    /** Видимый диапазон по Y (мир), чтобы график помещался на экран */
    private static final double Y_MIN = -12.0;
    private static final double Y_MAX = 12.0;

    private static final int SAMPLES = 800;

    /** Макс. скачок |Δy| между соседними точками выборки — разрыв у полюса tan */
    private static final double MAX_Y_JUMP = 80.0;

    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint = new Paint();

    public FunctionGraphView(Context context) {
        super(context);
        init();
    }

    public FunctionGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FunctionGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint.setColor(Color.parseColor("#121212"));
        gridPaint.setColor(0xFF444444);
        gridPaint.setStrokeWidth(1f);
        axisPaint.setColor(0xFF888888);
        axisPaint.setStrokeWidth(2f);
        graphPaint.setColor(0xFF4FC3F7);
        graphPaint.setStrokeWidth(3f);
        graphPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        canvas.drawRect(0, 0, w, h, bgPaint);

        for (int x = 0; x <= w; x += GRID_STEP_PX) {
            canvas.drawLine(x, 0, x, h, gridPaint);
        }
        for (int y = 0; y <= h; y += GRID_STEP_PX) {
            canvas.drawLine(0, y, w, y, gridPaint);
        }

        float x0 = worldXToScreen(0, w);
        float y0 = worldYToScreen(0, h);
        if (x0 >= 0 && x0 <= w) {
            canvas.drawLine(x0, 0, x0, h, axisPaint);
        }
        if (y0 >= 0 && y0 <= h) {
            canvas.drawLine(0, y0, w, y0, axisPaint);
        }

        double dx = (X_MAX - X_MIN) / SAMPLES;
        float prevSx = 0;
        float prevSy = 0;
        boolean hasPrev = false;
        double prevYWorld = 0;

        for (int i = 0; i <= SAMPLES; i++) {
            double xw = X_MIN + i * dx;
            Double yw = evalY(xw);
            if (yw == null) {
                hasPrev = false;
                continue;
            }
            float sx = worldXToScreen(xw, w);
            float sy = worldYToScreen(yw, h);

            if (hasPrev) {
                if (Math.abs(yw - prevYWorld) <= MAX_Y_JUMP) {
                    canvas.drawLine(prevSx, prevSy, sx, sy, graphPaint);
                }
            }
            prevSx = sx;
            prevSy = sy;
            prevYWorld = yw;
            hasPrev = true;
        }
    }

    /**
     * @return значение y или null, если точка у полюса / не число
     */
    private Double evalY(double x) {
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

    private float worldXToScreen(double xWorld, int w) {
        return (float) ((xWorld - X_MIN) / (X_MAX - X_MIN) * w);
    }

    /** Ось Y вверх — инверсия для экрана */
    private float worldYToScreen(double yWorld, int h) {
        return (float) ((Y_MAX - yWorld) / (Y_MAX - Y_MIN) * h);
    }
}
