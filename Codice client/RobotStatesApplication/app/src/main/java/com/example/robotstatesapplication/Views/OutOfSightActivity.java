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
import com.example.robotstatesapplication.Utils.AlertBuilder;

public class OutOfSightActivity extends AppCompatActivity {

    Button bottoneRitorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_out_of_sight);

        bottoneRitorno = findViewById(R.id.bottoneTornaAllApp);

        bottoneRitorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (getIntent().getStringExtra("NOCOMM") != null)
                    finish();*/

                Thread threadOOS = new Thread(()-> {
                    try {
                        SocketSingleton.getInstance().getSocketOut().print("oos");
                        SocketSingleton.getInstance().getSocketOut().flush();
                    } catch (Exception e) {
                        AlertBuilder.buildAlertSingoloBottone(OutOfSightActivity.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!");
                    }
                });
                threadOOS.start();
                try {
                    threadOOS.join();
                } catch (InterruptedException e) {
                    AlertBuilder.buildAlertSingoloBottone(OutOfSightActivity.this, "Errore!", "C'è stato un errore, riprovare!");
                }
                finish();

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        bottoneRitorno.callOnClick();
    }
}