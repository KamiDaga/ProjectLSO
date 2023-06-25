package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.AlertBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    Button bottoneLogin;
    TextView bottoneRegistrazione;
    TextView textViewRegistrazione;
    ImageView bottoneOcchioPassword;
    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        textViewRegistrazione = findViewById(R.id.textViewInizioRegistrazione);
        bottoneRegistrazione = findViewById(R.id.textViewInizioRegistrazione);
        bottoneLogin = findViewById(R.id.bottoneLogin);
        bottoneOcchioPassword = findViewById(R.id.occhioPasswordLogin);
        editTextUsername = findViewById(R.id.editTextUsernameLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);

        SpannableStringBuilder str = new SpannableStringBuilder("Non hai un account? Registrati qui");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 20, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewRegistrazione.setText(str);

        Handler handler = new Handler();

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
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()){
                    AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Attenzione!", "Riempire tutti i campi di inserimento");
                    return;
                }

                if (username.length() > 20 || password.length() > 12 || username.contains("-") || password.contains("-")) {
                    AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Attenzione!", "Lo username o la password inserite non rispettano i giusti criteri." +
                            "\nAssicurarsi che:\nLo username abbia massimo 20 caratteri e non contenga '-'\nLa password abbia massimo 12 caratteri e non contenga '-'");
                    return;
                }


                class ThreadControllo extends Thread {

                    private boolean successo = false;
                    private boolean utenteTrovato = false;

                    public void run() {
                        SocketSingleton.getInstance().getSocketOut().print("login-"+username+"-"+password);
                        SocketSingleton.getInstance().getSocketOut().flush();
                        try {
                            String rispostaServer = SocketSingleton.getInstance().getSocketIn().readLine();
                            Log.i("RISPOSTA", rispostaServer);
                            if (rispostaServer.startsWith("Benvenuto")) {
                                utenteTrovato = true;
                                successo = true;
                            }
                            else if (!rispostaServer.startsWith("Utente non trovato")) {
                                utenteTrovato = true;
                            } //Se l'utente non è trovato, i booleani sono già a posto, non lo mettiamo come caso in else
                        } catch (IOException e) {
                            handler.post(()->AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!"));
                        }
                    }

                    public boolean isUtenteTrovato() {
                        return utenteTrovato;
                    }

                    public boolean isSuccesso() {
                        return successo;
                    }

                }

                ThreadControllo threadSocket = new ThreadControllo();

                threadSocket.start();
                try {
                    threadSocket.join();
                } catch (InterruptedException e) {
                    AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                }
                if (!threadSocket.isUtenteTrovato()) {
                    AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Errore!", "Il nome utente inserito non è corretto!");
                }
                else if (!threadSocket.isSuccesso()) {
                    AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Errore!", "La password inserita non è corretta!");
                }
                else {
                    Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("USERNAME", username);
                    startActivity(i);
                }
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

    @Override
    public void onStart() {
        Handler handler = new Handler();
        Thread threadSocket = new Thread(()->{
            try {
                if (getIntent().getStringExtra("RESTART") != null)
                    SocketSingleton.rinnovaIstanza();
                SocketSingleton.getInstance();
            }
            catch (Exception e) {
                handler.post(()->AlertBuilder.buildAlertSingoloBottone(LoginActivity.this, "Errore!", "C'è stato un errore di comunicazione, chiudere l'applicazione."));
            }
        });
        threadSocket.start();
        super.onStart();
    }
}