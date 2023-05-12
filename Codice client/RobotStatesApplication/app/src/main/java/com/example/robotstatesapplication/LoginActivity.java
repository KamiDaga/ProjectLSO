package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button bottoneLogin;
    TextView bottoneRegistrazione;
    TextView textViewRegistrazione;
    ImageView bottoneOcchioPassword;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewRegistrazione = findViewById(R.id.textViewInizioRegistrazione);
        bottoneRegistrazione = findViewById(R.id.textViewInizioRegistrazione);
        bottoneLogin = findViewById(R.id.bottoneLogin);
        bottoneOcchioPassword = findViewById(R.id.occhioPasswordLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);

        SpannableStringBuilder str = new SpannableStringBuilder("Non hai un account? Registrati qui");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 20, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewRegistrazione.setText(str);

        bottoneRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrazioneActivity.class);
                startActivity(i);
            }
        });

        bottoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(i);
            }
        });

        bottoneOcchioPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    bottoneOcchioPassword.setImageResource(R.drawable.icona_occhio_sbarrato);
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    bottoneOcchioPassword.setImageResource(R.drawable.icona_occhio);
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
}