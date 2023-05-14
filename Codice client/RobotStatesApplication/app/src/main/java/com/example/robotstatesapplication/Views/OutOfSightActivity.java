package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robotstatesapplication.R;

public class OutOfSightActivity extends AppCompatActivity {

    Button bottoneRitorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

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