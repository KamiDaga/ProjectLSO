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
import com.example.robotstatesapplication.Utils.AlertBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RegistrazioneActivity extends AppCompatActivity {

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
                String password = editTextPassword.getText().toString();
                String conferma = editTextConferma.getText().toString();
                String dataNascita = editTextDataNascita.getText().toString();
                long differenzaDaNascita = Calendar.getInstance().get(Calendar.YEAR) - calendario.get(Calendar.YEAR);
                if (Calendar.getInstance().get(Calendar.MONTH) < calendario.get(Calendar.MONTH) ||
                        (Calendar.getInstance().get(Calendar.MONTH) == calendario.get(Calendar.MONTH) && Calendar.getInstance().get(Calendar.DATE) < calendario.get(Calendar.DATE))) {
                    differenzaDaNascita--;
                }
                if (!conferma.equals(password)) {
                    AlertBuilder.buildAlertSingoloBottone(RegistrazioneActivity.this, "Attenzione!", "La password non coincide con la sua conferma");
                    return;
                }
                if (username.isEmpty() || password.isEmpty() || dataNascita. isEmpty()){
                    AlertBuilder.buildAlertSingoloBottone(RegistrazioneActivity.this, "Attenzione!", "Riempire tutti i campi di inserimento");
                    return;
                }
                if (username.length() > 20 || password.length() > 12 || username.contains("-") || password.contains("-")) {
                    AlertBuilder.buildAlertSingoloBottone(RegistrazioneActivity.this, "Attenzione!", "Lo username o la password inserite non rispettano i giusti criteri." +
                            "\nAssicurarsi che:\nLo username abbia massimo 20 caratteri e non contenga '-'\nLa password abbia massimo 12 caratteri e non contenga '-'");
                    return;
                }
                if (differenzaDaNascita < 18) {
                    AlertBuilder.buildAlertSingoloBottone(RegistrazioneActivity.this, "Attenzione!", "Qui vendiamo solo alcolici, non sono ammessi minorenni!");
                    return;
                }
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
        dateView.setText(new StringBuilder().append(calendario.get(Calendar.DAY_OF_MONTH)).append(" / ").append((calendario.get(Calendar.MONTH))+1).append(" / ").append(calendario.get(Calendar.YEAR)));
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