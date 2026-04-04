package com.example.laba1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
                FunctionGraphView.X_MIN,
                FunctionGraphView.X_MAX,
                Math.toDegrees(FunctionGraphView.X_MIN),
                Math.toDegrees(FunctionGraphView.X_MAX));
        tvInfo.setText(formula + "\n\n" + xRange);
    }

    public void onGraphExitClick(View v) {
        finish();
    }
}
