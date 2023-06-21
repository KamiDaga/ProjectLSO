package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.AlertBuilder;

import java.io.IOException;
import java.util.ArrayList;

public class WaitingActivity extends AppCompatActivity {

    private Button bottoneOutOfSight;
    private ArrayList<Drink> menù = new ArrayList<>();

    private volatile boolean outOfSight;

    private TextView tvPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_waiting);

        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightWaiting);

        tvPrompt = findViewById(R.id.promptWaiting);

        tvPrompt.setText("Ciao, " + getIntent().getStringExtra("USERNAME"));

        Handler threadHandler = new Handler();
        Thread threadSocket = new Thread(()-> {
            String formatoDrink = null;
            String[] splitDrink = null;
            try {
                formatoDrink = SocketSingleton.getInstance().getSocketIn().readLine();
                splitDrink = formatoDrink.split("-");
                menù.add(new Drink(splitDrink[0], Double.parseDouble(splitDrink[1]), Integer.parseInt(splitDrink[2])));
                while (SocketSingleton.getInstance().getSocketIn().ready()) {
                    formatoDrink = SocketSingleton.getInstance().getSocketIn().readLine();
                    splitDrink = formatoDrink.split("-");
                    menù.add(new Drink(splitDrink[0], Double.parseDouble(splitDrink[1]), Integer.parseInt(splitDrink[2])));
                }
                synchronized (menù) {
                    while (outOfSight)
                        menù.wait();
                    threadHandler.post(()->{
                        Intent i = new Intent(WaitingActivity.this, SuggestedOrderingActivity.class);
                        i.putExtra("MENU", menù);
                        startActivity(i);
                    });
                }
            } catch (Exception e) {
                threadHandler.post(()->AlertBuilder.buildAlertSingoloBottone(WaitingActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!" + e.getMessage()));
                Log.i("STRINGHE", splitDrink[0].toString());
            }
        });
        threadSocket.start();

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (menù) {
                    outOfSight = true;
                    Intent i = new Intent(WaitingActivity.this, OutOfSightActivity.class);
                    Thread threadOOS = new Thread(()-> {
                        try {
                            SocketSingleton.getInstance().getSocketOut().print("oos");
                            SocketSingleton.getInstance().getSocketOut().flush();
                        } catch (Exception e) {
                            AlertBuilder.buildAlertSingoloBottone(WaitingActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                        }
                    });
                    threadOOS.start();
                    try {
                        threadOOS.join();
                    } catch (InterruptedException e) {
                        AlertBuilder.buildAlertSingoloBottone(WaitingActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                    }
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onStart() {
        synchronized (menù) {
            outOfSight = false;
            menù.notifyAll();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {

    }

}