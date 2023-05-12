package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OutOfSightActivity extends AppCompatActivity {

    Button bottoneRitorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_of_sight);

        String fragmentOrigine = getIntent().getStringExtra("Origine");

        bottoneRitorno = findViewById(R.id.bottoneTornaAllApp);

        bottoneRitorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = null;

                switch (fragmentOrigine) {
                    case "Welcome":
                        i = new Intent(OutOfSightActivity.this, WelcomeActivity.class);
                       break;
                }

                startActivity(i);

            }
        });
    }
}