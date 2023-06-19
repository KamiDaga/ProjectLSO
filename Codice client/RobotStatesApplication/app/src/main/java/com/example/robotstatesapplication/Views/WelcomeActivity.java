package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightWelcome);
        bottoneOrdina = findViewById(R.id.bottoneOrdinaWelcome);

            Thread threadSocket = new Thread(()-> {
                try {
                    while (SocketSingleton.getInstance().getSocketIn().ready()) {
                        menù.add(SocketSingleton.getInstance().getSocketIn().readLine());
                    }
                } catch (IOException e) {
                    AlertBuilder.buildAlertSingoloBottone(WelcomeActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                }
            });

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });

        bottoneOrdina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menù.isEmpty()) {
                    Intent i = new Intent(WelcomeActivity.this, WaitingActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(WelcomeActivity.this, SuggestedOrderingActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("MENU", menù);
                    startActivity(i);
                }
            }
        });



    }

    @Override
    public void onBackPressed()
    {

    }

}