package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button bottoneOutOfSight;
    private Button bottoneOrdina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightWelcome);
        bottoneOrdina = findViewById(R.id.bottoneOrdinaWelcome);

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, OutOfSightActivity.class);
                i.putExtra("Origine", "Welcome");
                startActivity(i);
            }
        });

        bottoneOrdina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, WaitingActivity.class);
                startActivity(i);
            }
        });



    }
}