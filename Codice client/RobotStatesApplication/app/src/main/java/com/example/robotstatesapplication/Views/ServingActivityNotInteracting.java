package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.robotstatesapplication.R;

public class ServingActivityNotInteracting extends AppCompatActivity {

    private TextView tvContatore;
    private int tempoDrink;
    private Button bottoneOutOfSight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_serving_not_interacting);

        tvContatore = findViewById(R.id.contatoreServingNotInteracting);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightServingNotInteracting);

        attivaGestioneContatore();

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServingActivityNotInteracting.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });

    }

    private void attivaGestioneContatore() {
        tempoDrink = 30;
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

            }
        });
        threadContatore.start();
    }
}