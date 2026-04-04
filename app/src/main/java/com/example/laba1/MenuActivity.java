package com.example.laba1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        boolean isValid = (metrics.heightPixels >= 1920 && metrics.widthPixels >= 1080)
                || (metrics.heightPixels >= 1080 && metrics.widthPixels >= 1920);

        if (!isValid) {
            showErrorDialog(getString(R.string.error_incompatible_device));
            return;
        }

        setContentView(R.layout.activity_menu);
    }

    private void showErrorDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(R.string.exit, (d, i) -> {
                    finishAffinity();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                })
                .show();
    }

    public void onOpenCalculator(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onOpenGraph(View v) {
        startActivity(new Intent(this, GraphActivity.class));
    }

    public void onExitApp(View v) {
        finishAffinity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
