package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.AlertBuilder;

import java.io.IOException;
import java.util.ArrayList;

public class SuggestedOrderingActivity extends AppCompatActivity {

    private Button bottoneConferma;
    private Button bottoneAnnulla;
    private Button bottoneOutOfSight;
    private Drink drinkPreferito;
    private TextView tvNomeDrink;
    private ImageView iconaDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_suggested_ordering);

        bottoneAnnulla = findViewById(R.id.bottoneAnnullaDrinkSuggerito);
        bottoneConferma = findViewById(R.id.bottoneConfermaDrinkSuggerito);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightOrderingSuggested);
        tvNomeDrink = findViewById(R.id.nomeDrinkSuggerito);
        iconaDrink = findViewById(R.id.iconaDrinkPreferito);

        String nomeDrink = getIntent().getStringExtra("DRINKPREFERITO");
        ArrayList<Drink> menu = (ArrayList<Drink>)getIntent().getSerializableExtra("MENU");
        Log.i("DRINKS", menu.toString());
        for (Drink d : menu) {
            if (d.getNome().equals(nomeDrink)) {
                drinkPreferito = d;
                break;
            }
        }

        tvNomeDrink.setText(drinkPreferito.getNome());
        int idRisorsaDrink = 0;
        switch (drinkPreferito.getNome()) {
            case "Cosmopolitan":
                idRisorsaDrink = R.drawable.drinkcosmopolitan;
                break;
            case "Empress 75":
                idRisorsaDrink = R.drawable.drinkempress75;
                break;
            case "Gin fizz":
                idRisorsaDrink = R.drawable.drinkfizz;
                break;
            case "Rum Manhattan":
                idRisorsaDrink = R.drawable.drinkmanhattan;
                break;
            case "Martini Bianco":
                idRisorsaDrink = R.drawable.drinkmartini;
                break;
            case "Mojito":
                idRisorsaDrink = R.drawable.drinkmojito;
                break;
            case "Mudslide":
                idRisorsaDrink = R.drawable.drinkmudslide;
                break;
            case "Negroski":
                idRisorsaDrink = R.drawable.drinknegroski;
                break;
            case "Pina Colada":
                idRisorsaDrink = R.drawable.drinkpinacolada;

        }

        iconaDrink.setImageResource(idRisorsaDrink);

        bottoneAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestedOrderingActivity.this, OrderingActivity.class);
                i.putExtra("MENU", getIntent().getSerializableExtra("MENU"));
                i.putExtra("HOBBIES", getIntent().getStringExtra("HOBBIES"));
                startActivity(i);
            }
        });

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread threadOOS = new Thread(()-> {
                    try {
                        SocketSingleton.getInstance().getSocketOut().print("oos");
                        SocketSingleton.getInstance().getSocketOut().flush();
                    } catch (Exception e) {
                        AlertBuilder.buildAlertSingoloBottone(SuggestedOrderingActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                    }
                });
                threadOOS.start();
                try {
                    threadOOS.join();
                } catch (InterruptedException e) {
                    AlertBuilder.buildAlertSingoloBottone(SuggestedOrderingActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                }
                Intent i = new Intent(SuggestedOrderingActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });

        bottoneConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                if (drinkPreferito != null) {
                    Thread threadDrink = new Thread(() -> {
                        SocketSingleton.getInstance().getSocketOut().print(drinkPreferito.getNome());
                        SocketSingleton.getInstance().getSocketOut().flush();
                        try {
                            String messaggio1 = SocketSingleton.getInstance().getSocketIn().readLine();
                            String messaggio2 = SocketSingleton.getInstance().getSocketIn().readLine();
                            handler.post(() -> creaDialogSceltaConversazione(messaggio1, messaggio2));
                        } catch (IOException e) {
                        }
                    });
                    threadDrink.start();
                }
            }
        });
    }

    private void creaDialogSceltaConversazione(String titolo, String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuggestedOrderingActivity.this);
        builder.setTitle(titolo);
        builder.setMessage(messaggio)
                .setCancelable(false)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Thread thread = new Thread(()->{
                            SocketSingleton.getInstance().getSocketOut().print("si");
                            SocketSingleton.getInstance().getSocketOut().flush();
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            AlertBuilder.buildAlertSingoloBottone(SuggestedOrderingActivity.this, "Errore!", "C'è stato un errore, riprovare");
                        };
                        Intent i = new Intent(SuggestedOrderingActivity.this, ServingActivityInteracting.class);
                        i.putExtra("DRINK", drinkPreferito);
                        i.putExtra("HOBBIES", getIntent().getStringExtra("HOBBIES"));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Thread thread = new Thread(()->{
                            SocketSingleton.getInstance().getSocketOut().print("no");
                            SocketSingleton.getInstance().getSocketOut().flush();
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            AlertBuilder.buildAlertSingoloBottone(SuggestedOrderingActivity.this, "Errore!", "C'è stato un errore, riprovare");
                        };
                        Intent i = new Intent(SuggestedOrderingActivity.this, ServingActivityNotInteracting.class);
                        i.putExtra("DRINK", drinkPreferito);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button siButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        siButton.setBackgroundColor(Color.parseColor("#0E0D57"));
        siButton.setTextColor(Color.parseColor("#FFFFFF"));
        Button noButton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        noButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
        noButton.setTextColor(Color.parseColor("#0E0D57"));
    }

    @Override
    public void onBackPressed()
    {

    }

}