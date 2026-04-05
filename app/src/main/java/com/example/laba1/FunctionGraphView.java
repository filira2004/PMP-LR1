package com.example.laba1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FunctionGraphView extends View {

    private static final int GRID_STEP_PX = 30;

    private static final double TICK_X_STEP = Math.PI / 2;
    private static final double TICK_Y_STEP = 3.0;

    private static final double MAX_Y_JUMP = 80.0;

    private static final float PAD_LEFT = 52f;
    private static final float PAD_RIGHT = 28f;
    private static final float PAD_TOP = 36f;
    private static final float PAD_BOTTOM = 52f;

    private static final float ARROW_SIZE = 14f;
    private static final float TICK_LEN = 10f;

    private static final float EXTREMA_RADIUS = 10f;

    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint extremaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint = new Paint();
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tickLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path arrowPath = new Path();

    private final List<PointF> extremaPoints = new ArrayList<>();

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
        axisPaint.setColor(0xFFCCCCCC);
        axisPaint.setStrokeWidth(3f);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisFillPaint.setColor(0xFFCCCCCC);
        axisFillPaint.setStyle(Paint.Style.FILL);
        graphPaint.setColor(0xFF4FC3F7);
        graphPaint.setStrokeWidth(3f);
        graphPaint.setStyle(Paint.Style.STROKE);
        extremaPaint.setColor(0xFFFFD600);
        extremaPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(0xFFE0E0E0);
        textPaint.setTextSize(32f);
        tickLabelPaint.setColor(0xFFB0B0B0);
        tickLabelPaint.setTextSize(28f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        float plotLeft = PAD_LEFT;
        float plotTop = PAD_TOP;
        float plotRight = w - PAD_RIGHT;
        float plotBottom = h - PAD_BOTTOM;
        float plotW = plotRight - plotLeft;
        float plotH = plotBottom - plotTop;

        canvas.drawRect(0, 0, w, h, bgPaint);

        for (float x = plotLeft; x <= plotRight; x += GRID_STEP_PX) {
            canvas.drawLine(x, plotTop, x, plotBottom, gridPaint);
        }
        for (float y = plotTop; y <= plotBottom; y += GRID_STEP_PX) {
            canvas.drawLine(plotLeft, y, plotRight, y, gridPaint);
        }

        float xAxisY = worldYToScreen(0, plotTop, plotH);
        float yAxisX = worldXToScreen(0, plotLeft, plotW);

        boolean showYaxis = yAxisX >= plotLeft && yAxisX <= plotRight;
        boolean showXaxis = xAxisY >= plotTop && xAxisY <= plotBottom;

        if (showYaxis) {
            canvas.drawLine(yAxisX, plotTop, yAxisX, plotBottom, axisPaint);
            drawArrowUp(canvas, yAxisX, plotTop);
            drawArrowDown(canvas, yAxisX, plotBottom);
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("y", yAxisX + 12f, plotTop + 4f + textPaint.getTextSize(), textPaint);
        }

        if (showXaxis) {
            canvas.drawLine(plotLeft, xAxisY, plotRight, xAxisY, axisPaint);
            drawArrowRight(canvas, plotRight, xAxisY);
            drawArrowLeft(canvas, plotLeft, xAxisY);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("x", plotRight - 8f, xAxisY + 40f, textPaint);
        }

        if (showXaxis) {
            tickLabelPaint.setTextAlign(Paint.Align.CENTER);
            for (double xw = Math.ceil(GraphFunctionMath.X_MIN / TICK_X_STEP) * TICK_X_STEP;
                    xw <= GraphFunctionMath.X_MAX + 1e-9; xw += TICK_X_STEP) {
                float sx = worldXToScreen(xw, plotLeft, plotW);
                if (sx < plotLeft || sx > plotRight) {
                    continue;
                }
                canvas.drawLine(sx, xAxisY - TICK_LEN, sx, xAxisY + TICK_LEN, axisPaint);
                String label = formatXTick(xw);
                canvas.drawText(label, sx, xAxisY + 32f, tickLabelPaint);
            }
        }

        if (showYaxis) {
            tickLabelPaint.setTextAlign(Paint.Align.RIGHT);
            for (double yw = Math.ceil(GraphFunctionMath.Y_MIN / TICK_Y_STEP) * TICK_Y_STEP;
                    yw <= GraphFunctionMath.Y_MAX + 1e-9; yw += TICK_Y_STEP) {
                if (Math.abs(yw) < 1e-6) {
                    continue;
                }
                float sy = worldYToScreen(yw, plotTop, plotH);
                if (sy < plotTop || sy > plotBottom) {
                    continue;
                }
                canvas.drawLine(yAxisX - TICK_LEN, sy, yAxisX + TICK_LEN, sy, axisPaint);
                canvas.drawText(String.format(Locale.US, "%.1f", yw), yAxisX - 14f, sy + 8f, tickLabelPaint);
            }
        }

        double dx = (GraphFunctionMath.X_MAX - GraphFunctionMath.X_MIN) / GraphFunctionMath.SAMPLES;
        float prevSx = 0;
        float prevSy = 0;
        boolean hasPrev = false;
        double prevYWorld = 0;

        for (int i = 0; i <= GraphFunctionMath.SAMPLES; i++) {
            double xw = GraphFunctionMath.X_MIN + i * dx;
            Double yw = GraphFunctionMath.evalY(xw);
            if (yw == null) {
                hasPrev = false;
                continue;
            }
            float sx = worldXToScreen(xw, plotLeft, plotW);
            float sy = worldYToScreen(yw, plotTop, plotH);

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

        for (PointF p : extremaPoints) {
            float sx = worldXToScreen(p.x, plotLeft, plotW);
            float sy = worldYToScreen(p.y, plotTop, plotH);
            canvas.drawCircle(sx, sy, EXTREMA_RADIUS, extremaPaint);
        }
    }

    public void setExtremaPoints(List<PointF> points) {
        extremaPoints.clear();
        if (points != null) {
            extremaPoints.addAll(points);
        }
        invalidate();
    }

    private String formatXTick(double xw) {
        double eps = 1e-6;
        if (Math.abs(xw) < eps) {
            return "0";
        }
        if (Math.abs(Math.abs(xw) - Math.PI / 2) < 0.08) {
            return xw > 0 ? "π/2" : "-π/2";
        }
        if (Math.abs(Math.abs(xw) - Math.PI) < 0.08) {
            return xw > 0 ? "π" : "-π";
        }
        if (Math.abs(Math.abs(xw) - 3 * Math.PI / 2) < 0.08) {
            return xw > 0 ? "3π/2" : "-3π/2";
        }
        if (Math.abs(Math.abs(xw) - 2 * Math.PI) < 0.08) {
            return xw > 0 ? "2π" : "-2π";
        }
        return String.format(Locale.US, "%.2f", xw);
    }

    private void drawArrowUp(Canvas canvas, float cx, float cy) {
        arrowPath.reset();
        arrowPath.moveTo(cx, cy);
        arrowPath.lineTo(cx - ARROW_SIZE * 0.55f, cy + ARROW_SIZE);
        arrowPath.lineTo(cx + ARROW_SIZE * 0.55f, cy + ARROW_SIZE);
        arrowPath.close();
        canvas.drawPath(arrowPath, axisFillPaint);
    }

    private void drawArrowDown(Canvas canvas, float cx, float cy) {
        arrowPath.reset();
        arrowPath.moveTo(cx, cy);
        arrowPath.lineTo(cx - ARROW_SIZE * 0.55f, cy - ARROW_SIZE);
        arrowPath.lineTo(cx + ARROW_SIZE * 0.55f, cy - ARROW_SIZE);
        arrowPath.close();
        canvas.drawPath(arrowPath, axisFillPaint);
    }

    private void drawArrowRight(Canvas canvas, float cx, float cy) {
        arrowPath.reset();
        arrowPath.moveTo(cx, cy);
        arrowPath.lineTo(cx - ARROW_SIZE, cy - ARROW_SIZE * 0.55f);
        arrowPath.lineTo(cx - ARROW_SIZE, cy + ARROW_SIZE * 0.55f);
        arrowPath.close();
        canvas.drawPath(arrowPath, axisFillPaint);
    }

    private void drawArrowLeft(Canvas canvas, float cx, float cy) {
        arrowPath.reset();
        arrowPath.moveTo(cx, cy);
        arrowPath.lineTo(cx + ARROW_SIZE, cy - ARROW_SIZE * 0.55f);
        arrowPath.lineTo(cx + ARROW_SIZE, cy + ARROW_SIZE * 0.55f);
        arrowPath.close();
        canvas.drawPath(arrowPath, axisFillPaint);
    }

    private float worldXToScreen(double xWorld, float plotLeft, float plotW) {
        return (float) (plotLeft + (xWorld - GraphFunctionMath.X_MIN)
                / (GraphFunctionMath.X_MAX - GraphFunctionMath.X_MIN) * plotW);
    }

    private float worldYToScreen(double yWorld, float plotTop, float plotH) {
        return (float) (plotTop + (GraphFunctionMath.Y_MAX - yWorld)
                / (GraphFunctionMath.Y_MAX - GraphFunctionMath.Y_MIN) * plotH);
    }
}
