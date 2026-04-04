package com.example.laba1;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private TextView expressionView;

    private String input = "";
    private double result = 0;
    private String operator = "";
    private boolean isNewInput = true;

    private double memory = 0;

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

        setContentView(R.layout.activity_main);

        display = findViewById(R.id.tvDisplay);
        expressionView = findViewById(R.id.tvExpression);

        display.setText("0");
        expressionView.setText("");
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

    private void showDivisionError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_division_by_zero)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showParseError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_invalid_number)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showSecondOperandRequired() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_second_operand_required)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public void onNumberClick(View v) {
        String val = ((Button) v).getText().toString();

        if (isNewInput) {
            input = "";
            isNewInput = false;
        }

        if (input.equals("0")) input = "";
        input += val;

        display.setText(input.isEmpty() ? "0" : input);
    }

    public void onDecimalClick(View v) {
        if (isNewInput) {
            input = "";
            isNewInput = false;
        }
        if (input.contains(",")) {
            return;
        }
        if (input.isEmpty() || input.equals("-")) {
            input += "0";
        }
        input += ",";
        display.setText(input);
    }

    public void onOperatorClick(View v) {
        String op = ((Button) v).getText().toString();

        if (!input.isEmpty()) {
            Double lhs = tryParseDouble(input);
            if (lhs == null) {
                showParseError();
                return;
            }
            if (operator.isEmpty()) {
                result = lhs;
                input = "";
                display.setText("0");
            } else {
                if (!calculateInternal()) {
                    return;
                }
                input = "";
                display.setText(formatResult(result));
            }
        }

        operator = op;
        expressionView.setText(formatResult(result) + " " + operator);
        isNewInput = true;
    }

    public void onEqualsClick(View v) {
        if (operator.isEmpty()) {
            return;
        }
        if (input.isEmpty()) {
            showSecondOperandRequired();
            return;
        }

        expressionView.setText(expressionView.getText() + " " + input);

        if (!calculateInternal()) {
            return;
        }

        operator = "";
        isNewInput = true;
    }

    private boolean calculateInternal() {
        Double value = tryParseDouble(input);
        if (value == null) {
            showParseError();
            return false;
        }

        switch (operator) {
            case "+":
                result += value;
                break;
            case "-":
                result -= value;
                break;
            case "×":
                result *= value;
                break;
            case "÷":
                if (value == 0) {
                    showDivisionError();
                    return false;
                }
                result /= value;
                break;
            default:
                result = value;
                break;
        }

        input = formatResult(result);
        display.setText(input);
        return true;
    }

    public void onMemoryClick(View v) {
        String cmd = ((Button) v).getText().toString();
        Double current = tryParseDouble(input.isEmpty() ? "0" : input);
        if (current == null) {
            showParseError();
            return;
        }

        switch (cmd) {
            case "MC":
                memory = 0;
                break;

            case "MR":
                input = formatResult(memory);
                display.setText(input);
                isNewInput = true;
                break;

            case "MS":
                memory = current;
                break;

            case "M+":
                memory += current;
                break;

            case "M-":
                memory -= current;
                break;
        }
    }

    public void onClearClick(View v) {
        input = "";
        result = 0;
        operator = "";
        isNewInput = true;

        display.setText("0");
        expressionView.setText("");
    }

    public void onBackspaceClick(View v) {
        if (!input.isEmpty() && !isNewInput) {
            input = input.substring(0, input.length() - 1);
            if (input.isEmpty()) {
                input = "0";
            }
            display.setText(input);
        }
    }

    private Double tryParseDouble(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String normalized = str.replace(",", ".").trim();
        if (normalized.isEmpty() || normalized.equals(".") || normalized.equals("-") || normalized.equals("-.")) {
            return null;
        }
        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatResult(double value) {
        String s = String.format(Locale.US, "%.10g", value);
        if (s.contains(".")) {
            s = s.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return s.replace(".", ",");
    }
}
