package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textViewRegistrazione = findViewById(R.id.textViewInizioRegistrazione);
        TextView bottoneRegistrazione = findViewById(R.id.textViewInizioRegistrazione);
        Button bottoneLogin = findViewById(R.id.bottoneLogin);

        SpannableStringBuilder str = new SpannableStringBuilder("Non hai un account? Registrati qui");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 20, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewRegistrazione.setText(str);

        bottoneRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegistrazioneActivity.class);
                startActivity(i);
            }
        });
    }
}