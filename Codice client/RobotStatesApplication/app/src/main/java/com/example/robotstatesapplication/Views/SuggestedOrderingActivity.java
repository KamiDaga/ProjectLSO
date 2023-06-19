package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robotstatesapplication.R;

public class SuggestedOrderingActivity extends AppCompatActivity {

    private Button bottoneConferma;
    private Button bottoneAnnulla;
    private Button bottoneOutOfSight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_suggested_ordering);

        bottoneAnnulla = findViewById(R.id.bottoneAnnullaDrinkSuggerito);
        bottoneConferma = findViewById(R.id.bottoneConfermaDrinkSuggerito);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightOrderingSuggested);

        bottoneAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestedOrderingActivity.this, OrderingActivity.class);
                i.putExtra("MENU", getIntent().getSerializableExtra("MENU"));
                startActivity(i);
            }
        });

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestedOrderingActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });

        bottoneConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestedOrderingActivity.this, ServingActivityInteracting.class);
                i.putExtra("Drink", "Drinksuggerito");
                startActivity(i);
            }
        });
    }
}