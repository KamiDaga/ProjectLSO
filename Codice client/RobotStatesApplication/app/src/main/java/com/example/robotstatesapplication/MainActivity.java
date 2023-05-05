package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textViewRegistrazione = findViewById(R.id.textViewInizioRegistrazione);

        SpannableStringBuilder str = new SpannableStringBuilder("Non hai un account? Registrati qui");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 20, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewRegistrazione.setText(str);
    }
}