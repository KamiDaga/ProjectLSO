package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.robotstatesapplication.Models.CaratteristicaDrinkEnum;
import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.AlertBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class OrderingActivity extends AppCompatActivity {

    private RadioGroup gruppoRadioDrink;
    private Button bottoneOutOfSight;
    private Button bottoneConferma;
    private Button bottoneEsci;
    private List<Drink> listaDrink = new ArrayList<>();

    private Drink drinkScelto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        gruppoRadioDrink = findViewById(R.id.radioGroupDrink);
        bottoneConferma = findViewById(R.id.bottoneConfermaDrinkSceltoOrdering);
        bottoneEsci = findViewById(R.id.bottoneTornaAlLoginOrdering);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightOrdering);

       listaDrink.addAll((ArrayList<Drink>)getIntent().getSerializableExtra("MENU"));

        for (Drink drink : listaDrink) {
            RadioButton radioDrinkCorrente = new RadioButton(OrderingActivity.this);
            radioDrinkCorrente.setText(drink.getNome() + " - " + drink.getPrezzo() + "€");
            radioDrinkCorrente.setTextColor(getResources().getColor(R.color.blu_scuro));
            radioDrinkCorrente.setBackgroundResource(R.drawable.radiobuttons_bg);
            radioDrinkCorrente.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.blu_scuro)));
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 20);
            gruppoRadioDrink.addView(radioDrinkCorrente, layoutParams);
        }

        gruppoRadioDrink.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                drinkScelto = listaDrink.get(checkedId-1);
            }
        });

        bottoneConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                if (drinkScelto != null) {
                    Thread threadDrink = new Thread(()->{
                        SocketSingleton.getInstance().getSocketOut().print(drinkScelto.getNome());
                        SocketSingleton.getInstance().getSocketOut().flush();
                        try {
                            String messaggio1 = SocketSingleton.getInstance().getSocketIn().readLine();
                            String messaggio2 = SocketSingleton.getInstance().getSocketIn().readLine();
                            handler.post(()->creaDialogSceltaConversazione(messaggio1, messaggio2));
                        } catch (IOException e) {
                        }
                    });
                    threadDrink.start();
                }
                else {
                    AlertBuilder.buildAlertSingoloBottone(OrderingActivity.this, "Attenzione!", "Non è stato selezionato alcun drink!");
                }
            }
        });

        bottoneEsci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderingActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Thread threadGone = new Thread(()->{
                    SocketSingleton.getInstance().getSocketOut().print("gone");
                    SocketSingleton.getInstance().getSocketOut().flush();
                });
                threadGone.start();
                try {
                    threadGone.join();
                } catch (InterruptedException e) {
                    AlertBuilder.buildAlertSingoloBottone(OrderingActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                }
                i.putExtra("RESTART", "");
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
                        AlertBuilder.buildAlertSingoloBottone(OrderingActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                    }
                });
                threadOOS.start();
                try {
                    threadOOS.join();
                } catch (InterruptedException e) {
                    AlertBuilder.buildAlertSingoloBottone(OrderingActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                }
                Intent i = new Intent(OrderingActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });
    }

    private void creaDialogSceltaConversazione(String titolo, String messaggio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderingActivity.this);
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
                            AlertBuilder.buildAlertSingoloBottone(OrderingActivity.this, "Errore!", "C'è stato un errore, riprovare");
                        };
                        Intent i = new Intent(OrderingActivity.this, ServingActivityInteracting.class);
                        i.putExtra("DRINK", drinkScelto);
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
                            AlertBuilder.buildAlertSingoloBottone(OrderingActivity.this, "Errore!", "C'è stato un errore, riprovare");
                        };
                        Intent i = new Intent(OrderingActivity.this, ServingActivityNotInteracting.class);
                        i.putExtra("DRINK", drinkScelto);
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