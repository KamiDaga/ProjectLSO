package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

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

public class ServingActivityNotInteracting extends AppCompatActivity {

    private TextView tvContatore;
    private Button bottoneOutOfSight;
    private Drink drinkCorrente;
    private int tempoDrink;
    boolean outOfSight = false;

    private Object lockOOS = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_serving_not_interacting);

        tvContatore = findViewById(R.id.contatoreServingNotInteracting);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightServingNotInteracting);

        drinkCorrente = (Drink)getIntent().getSerializableExtra("DRINK");
        tempoDrink = drinkCorrente.getTempoPreparazione();

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (lockOOS) {
                    outOfSight = true;
                    Intent i = new Intent(ServingActivityNotInteracting.this, OutOfSightActivity.class);
                    Thread threadOOS = new Thread(() -> {
                        try {
                            SocketSingleton.getInstance().getSocketOut().print("oos");
                            SocketSingleton.getInstance().getSocketOut().flush();
                        } catch (Exception e) {
                            AlertBuilder.buildAlertSingoloBottone(ServingActivityNotInteracting.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                        }
                    });
                    threadOOS.start();
                    try {
                        threadOOS.join();
                    } catch (InterruptedException e) {
                        AlertBuilder.buildAlertSingoloBottone(ServingActivityNotInteracting.this, "Errore!", "C'è stato un errore, riprovare!");
                    }
                    startActivity(i);
                }
            }
        });

        attivaGestioneContatore();

    }

    private void attivaGestioneContatore() {
        tvContatore.setText(String.valueOf(tempoDrink));
        Handler handlerContatore = new Handler();
        Thread threadContatore = new Thread(new Runnable() {
            @Override
            public void run() {
                while (tempoDrink > 0 && !Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    tempoDrink--;
                    handlerContatore.post(new Runnable() {
                        @Override
                        public void run() {
                            tvContatore.setText(String.valueOf(tempoDrink));
                        }
                    });
                }

                synchronized (lockOOS) {
                    while (outOfSight) {
                        try {
                            lockOOS.wait();
                        } catch (InterruptedException e) {
                            handlerContatore.post(()->AlertBuilder.buildAlertSingoloBottone(ServingActivityNotInteracting.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!" + e.getMessage()));
                        }
                    }
                    SocketSingleton.getInstance().getSocketOut().print("Esci");
                    SocketSingleton.getInstance().getSocketOut().flush();
                    handlerContatore.post(()->{
                        Intent i = new Intent(ServingActivityNotInteracting.this, FarewellingActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    });
                }

            }
        });
        threadContatore.start();
    }

    @Override
    public void onStart() {
        synchronized (lockOOS) {
            outOfSight = false;
            lockOOS.notifyAll();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {

    }


}