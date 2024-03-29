package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class WelcomeActivity extends AppCompatActivity {

    private Button bottoneOutOfSight;
    private Button bottoneOrdina;

    private ArrayList<String> menù = new ArrayList<>();
    private String username;
    private TextView tvPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightWelcome);
        bottoneOrdina = findViewById(R.id.bottoneOrdinaWelcome);
        tvPrompt = findViewById(R.id.promptWaiting);

        username = getIntent().getStringExtra("USERNAME");

        tvPrompt.setText("Ciao, " + username);

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread threadOOS = new Thread(()-> {
                    try {
                        SocketSingleton.getInstance().getSocketOut().print("oos");
                        SocketSingleton.getInstance().getSocketOut().flush();
                    } catch (Exception e) {
                        AlertBuilder.buildAlertSingoloBottone(WelcomeActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                    }
                });
                threadOOS.start();
                try {
                    threadOOS.join();
                } catch (InterruptedException e) {
                    AlertBuilder.buildAlertSingoloBottone(WelcomeActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                }
                Intent i = new Intent(WelcomeActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });

        bottoneOrdina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread newThread = new Thread(()->{
                    SocketSingleton.getInstance().getSocketOut().print("ORDER");
                    SocketSingleton.getInstance().getSocketOut().flush();
                });
                newThread.start();
                Intent i = new Intent(WelcomeActivity.this, WaitingActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("USERNAME", username);
                i.putExtra("DRINKPREFERITO", getIntent().getStringExtra("DRINKPREFERITO"));
                i.putExtra("HOBBIES", getIntent().getStringExtra("HOBBIES"));
                startActivity(i);
            }
        });



    }

    @Override
    public void onBackPressed()
    {

    }

}