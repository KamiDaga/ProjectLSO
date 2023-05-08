package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class RegistrazioneActivity extends AppCompatActivity {

    private Spinner spinnerSesso;
    private Button bottoneAvanti, bottoneIndietro;
    private ImageView bottoneCalendario;
    private DatePickerDialog datePicker;
    private TextView dateView;
    private Calendar calendario = Calendar.getInstance();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        spinnerSesso = findViewById(R.id.spinnerSessoRegistrazione);
        bottoneAvanti = findViewById(R.id.bottoneInizioQuestionario);
        bottoneIndietro = findViewById(R.id.bottoneAnnullaRegistrazione);
        bottoneCalendario = findViewById(R.id.iconaCalendarioRegistrazione);
        dateView = findViewById(R.id.editTextDataNascita);

        String[] sessi = new String[]{"M", "F"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistrazioneActivity.this, R.layout.spinner_layout, sessi);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        spinnerSesso.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        mostraData();
        datePicker = new DatePickerDialog(RegistrazioneActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendario.set(year, month, dayOfMonth);
                mostraData();
            }
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

        bottoneAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrazioneActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrazioneActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        bottoneCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

    }

    private void mostraData() {
        dateView.setText(new StringBuilder().append(calendario.get(Calendar.DAY_OF_MONTH)).append(" / ").append(calendario.get(Calendar.MONTH)).append(" / ").append(calendario.get(Calendar.YEAR)));
    }
}