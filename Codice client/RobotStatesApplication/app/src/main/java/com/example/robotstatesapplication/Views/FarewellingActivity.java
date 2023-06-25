package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.R;

import java.io.IOException;

public class FarewellingActivity extends AppCompatActivity {

    private Button bottoneUscita;
    private Button bottoneOOS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_farewelling);

        bottoneOOS = findViewById(R.id.bottoneOutOfSightFarewelling);
        bottoneUscita = findViewById(R.id.bottoneTornaAlLoginFarewelling);

        bottoneUscita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread threadUscita = new Thread(()->{
                    try {
                        SocketSingleton.getInstance().getSocketIn().readLine();
                    } catch (IOException e) {
                    }
                });
                threadUscita.start();
                try {
                    threadUscita.join();
                } catch (InterruptedException e) {
                }
                Intent i = new Intent(FarewellingActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("RESTART", "");
                startActivity(i);
            }
        });

        bottoneOOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FarewellingActivity.this, OutOfSightActivity.class);
                i.putExtra("NOCOMM", "");
                startActivity(i);
            }
        });
    }


    @Override
    public void onBackPressed()
    {

    }
}