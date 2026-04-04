package com.example.laba1;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        ImageButton btnExit = findViewById(R.id.btnGraphExit);
        btnExit.setOnClickListener(v -> finish());
    }
}
