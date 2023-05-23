package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.example.robotstatesapplication.Models.Messaggio;
import com.example.robotstatesapplication.Models.UtenteEnum;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.ListaMessaggiAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ServingActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ArrayList<Messaggio> chat = new ArrayList<>();
    private Button bottonePositivo;
    private Button bottoneNegativo;
    private ListaMessaggiAdapter adapterChat;
    private View viewRoot;
    private TextView tvContatore;
    private int tempoDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serving);

        rvChat = findViewById(R.id.recyclerViewChat);
        bottonePositivo = findViewById(R.id.bottoneRispostaPositivaChat);
        bottoneNegativo = findViewById(R.id.bottoneRispostaNegativaChat);
        viewRoot = findViewById(R.id.rootServing);
        tvContatore = findViewById(R.id.contatoreServingInteracting);

        chat.add(new Messaggio("Ciao, sono il Robot", UtenteEnum.ROBOT));
        attivaGestioneContatore();

        adapterChat = new ListaMessaggiAdapter(ServingActivity.this, chat);
        rvChat.setAdapter(adapterChat);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvChat.setLayoutManager(manager);

        viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = viewRoot.getRootView().getHeight() - viewRoot.getHeight();
                if (heightDiff > 100) {
                    if (adapterChat.getItemCount()!=0)
                        rvChat.smoothScrollToPosition(adapterChat.getItemCount() - 1);
                }
            }
        });

        bottonePositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.add(new Messaggio("Risposta positiva", UtenteEnum.UTENTE));
                adapterChat.notifyItemInserted(chat.size()-1);
                Handler handler = new Handler();

                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        chat.add(new Messaggio("Il robot ti ascolta", UtenteEnum.ROBOT));
                        adapterChat.notifyItemInserted(chat.size()-1);
                    }
                }, 1000);
            }
        });

        bottoneNegativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.add(new Messaggio("Risposta negativa", UtenteEnum.UTENTE));
                adapterChat.notifyItemInserted(chat.size()-1);
                Handler handler = new Handler();

                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        chat.add(new Messaggio("Il robot ti ascolta", UtenteEnum.ROBOT));
                        adapterChat.notifyItemInserted(chat.size()-1);
                    }
                }, 1000);
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
                while (tempoDrink > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
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

    @Override
    public void onBackPressed()
    {

    }

}