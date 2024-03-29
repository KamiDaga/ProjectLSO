package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robotstatesapplication.Models.Hobby;
import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.AlertBuilder;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QuestionarioHobbyActivity extends AppCompatActivity {

    private Button bottoneAvanti;
    private Button bottoneIndietro;
    private Map<Hobby, Boolean> hobbies = new HashMap<>();

    String rispostaServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_questionario_hobby);

        hobbies.put(new Hobby(findViewById(R.id.iconaHobby1), "Sport"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby2), "Film"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby3), "Giardinaggio"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby4), "Musica"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby5), "Videogiochi"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby6), "Cucina"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby7), "Teatro"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby8), "Arte"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby9), "Lettura"), false);

        bottoneAvanti = findViewById(R.id.bottoneFineQuestionario);
        bottoneIndietro = findViewById(R.id.bottoneAnnullaQuestionarioHobby);

        for (Hobby hobby : hobbies.keySet()) {
            hobby.getIcona().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hobbies.get(hobby).equals(false)) {
                        hobby.getIcona().setColorFilter(0x552B3881);
                        hobbies.put(hobby, true);
                    }
                    else {
                        hobby.getIcona().clearColorFilter();
                        hobbies.put(hobby, false);
                    }
                }
            });
        }

        bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bottoneAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collection<Hobby> hobbyScelti = new ArrayList<>();
                for (Hobby hobby : hobbies.keySet()) {
                    if (hobbies.get(hobby)) {
                        hobbyScelti.add(hobby);
                    }
                }
                if (hobbyScelti.size() != 3) {
                    mostraDialogErroreSelezioneHobby();
                }
                else {
                    Log.i("Questionario", hobbyScelti.toString());

                    Intent i = new Intent(QuestionarioHobbyActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Handler handler = new Handler();
                    Thread threadSocket = new Thread(()->{
                        SocketSingleton.getInstance().getSocketOut().print(
                                "registerer-"+getIntent().getStringExtra("USERNAME")+"-"+getIntent().getStringExtra("PASSWORD")+
                                        "-"+getIntent().getStringExtra("DRINK")+"-"+formaStringaHobby(hobbyScelti));
                        SocketSingleton.getInstance().getSocketOut().flush();
                        try {
                            rispostaServer = SocketSingleton.getInstance().getSocketIn().readLine();
                        } catch (IOException e) {
                            handler.post(()->AlertBuilder.buildAlertSingoloBottone(QuestionarioHobbyActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!"));
                        }
                    });
                    threadSocket.start();
                    try {
                        threadSocket.join();
                    } catch (InterruptedException e) {
                        AlertBuilder.buildAlertSingoloBottone(QuestionarioHobbyActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                    }
                    if (rispostaServer.startsWith("Esiste"))
                        AlertBuilder.buildAlertSingoloBottone(QuestionarioHobbyActivity.this, "Attenzione!", "Esiste un altro utente con questo username!");
                    else
                        startActivity(i);
                }

            }
        });

    }

    private void mostraDialogErroreSelezioneHobby() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionarioHobbyActivity.this);
        builder.setTitle("Attenzione!");
        builder.setMessage("Non hai selezionato abbastanza hobby.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button OkButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        OkButton.setBackgroundColor(getResources().getColor(R.color.blu_scuro));
        OkButton.setTextColor(getResources().getColor(R.color.white));
    }

    private static String formaStringaHobby(Collection<Hobby> hobbies) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Hobby h : hobbies) {
            stringBuffer.append(h.getDescrizione()+"/");
        }
        stringBuffer.deleteCharAt(stringBuffer.length()-1);
        return stringBuffer.toString();
    }
}