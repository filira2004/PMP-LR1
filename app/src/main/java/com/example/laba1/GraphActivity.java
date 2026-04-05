package com.example.laba1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        TextView tvInfo = findViewById(R.id.tvGraphInfo);
        String formula = getString(R.string.graph_function_formula);
        String xRange = String.format(
                Locale.US,
                getString(R.string.graph_x_range_template),
                GraphFunctionMath.X_MIN,
                GraphFunctionMath.X_MAX,
                Math.toDegrees(GraphFunctionMath.X_MIN),
                Math.toDegrees(GraphFunctionMath.X_MAX));
        String dbNote = getString(R.string.graph_lab4_db_note);
        tvInfo.setText(formula + "\n\n" + xRange + "\n\n" + dbNote);

        FunctionGraphView graphView = findViewById(R.id.graphView);

        GraphDbHelper dbHelper = new GraphDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            db.delete(GraphDbHelper.TABLE_POINTS, null, null);

            double dx = (GraphFunctionMath.X_MAX - GraphFunctionMath.X_MIN) / GraphFunctionMath.SAMPLES;
            for (int i = 0; i <= GraphFunctionMath.SAMPLES; i++) {
                double xw = GraphFunctionMath.X_MIN + i * dx;
                Double yw = GraphFunctionMath.evalY(xw);
                if (yw == null) {
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(GraphDbHelper.COL_X, xw);
                cv.put(GraphDbHelper.COL_Y, yw);
                db.insert(GraphDbHelper.TABLE_POINTS, null, cv);
            }

            cursor = db.rawQuery(
                    "SELECT " + GraphDbHelper.COL_X + ", " + GraphDbHelper.COL_Y
                            + " FROM " + GraphDbHelper.TABLE_POINTS
                            + " ORDER BY " + GraphDbHelper.COL_X + " ASC",
                    null);

            List<Double> xs = new ArrayList<>();
            List<Double> ys = new ArrayList<>();
            while (cursor.moveToNext()) {
                xs.add(cursor.getDouble(0));
                ys.add(cursor.getDouble(1));
            }

            graphView.setExtremaPoints(GraphExtremaHelper.findLocalExtrema(xs, ys));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void onGraphExitClick(View v) {
        finish();
    }
}
