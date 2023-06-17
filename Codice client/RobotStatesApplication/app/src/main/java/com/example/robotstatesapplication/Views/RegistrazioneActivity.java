package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.robotstatesapplication.R;

import java.util.Calendar;

public class RegistrazioneActivity extends AppCompatActivity {

    private Spinner spinnerSesso;
    private Button bottoneAvanti, bottoneIndietro;
    private ImageView bottoneCalendario;
    private DatePickerDialog datePicker;
    private TextView dateView;
    private Calendar calendario = Calendar.getInstance();
    private ImageView bottoneOcchioPassword, bottoneOcchioConferma;
    private EditText editTextUsername, editTextDataNascita, editTextPassword, editTextConferma;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registrazione);

        spinnerSesso = findViewById(R.id.spinnerSessoRegistrazione);
        bottoneAvanti = findViewById(R.id.bottoneInizioQuestionario);
        bottoneIndietro = findViewById(R.id.bottoneAnnullaRegistrazione);
        bottoneCalendario = findViewById(R.id.iconaCalendarioRegistrazione);
        dateView = findViewById(R.id.editTextDataNascita);
        bottoneOcchioPassword = findViewById(R.id.occhioPasswordRegistrazione);
        bottoneOcchioConferma = findViewById(R.id.occhioConfermaPasswordRegistrazione);
        editTextDataNascita = findViewById(R.id.editTextDataNascita);
        editTextUsername = findViewById(R.id.editTextUsernameRegistrazione);
        editTextPassword = findViewById(R.id.editTextPasswordRegistrazione);
        editTextConferma = findViewById(R.id.editTextConfermaPasswordRegistrazione);

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
                String username = editTextUsername.getText().toString();
                String password = editTextConferma.getText().toString();
                Intent i = new Intent(RegistrazioneActivity.this, QuestionarioDrinkActivity.class);
                i.putExtra("USERNAME", username);
                i.putExtra("PASSWORD", password);
                startActivity(i);
            }
        });

        bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bottoneCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        bottoneOcchioPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraNascondiPassword(editTextPassword, bottoneOcchioPassword);
            }
        });

        bottoneOcchioConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraNascondiPassword(editTextConferma, bottoneOcchioConferma);
            }
        });

    }



    private void mostraData() {
        dateView.setText(new StringBuilder().append(calendario.get(Calendar.DAY_OF_MONTH)).append(" / ").append(calendario.get(Calendar.MONTH)).append(" / ").append(calendario.get(Calendar.YEAR)));
    }

    private void mostraNascondiPassword(EditText et, ImageView iv) {

        if (et.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            iv.setImageResource(R.drawable.icona_occhio_sbarrato);
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else {
            iv.setImageResource(R.drawable.icona_occhio);
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

    }

}