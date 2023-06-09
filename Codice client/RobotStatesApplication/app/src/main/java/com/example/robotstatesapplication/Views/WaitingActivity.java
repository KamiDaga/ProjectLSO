package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robotstatesapplication.R;

public class WaitingActivity extends AppCompatActivity {

    Button bottoneOutOfSight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightWaiting);

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WaitingActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });

        setContentView(R.layout.activity_waiting);
    }

    @Override
    public void onBackPressed()
    {

    }

}