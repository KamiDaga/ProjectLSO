package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.Models.Messaggio;
import com.example.robotstatesapplication.Models.SocketSingleton;
import com.example.robotstatesapplication.Models.UtenteEnum;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.AlertBuilder;
import com.example.robotstatesapplication.Utils.ListaMessaggiAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class ServingActivityInteracting extends AppCompatActivity {

    private RecyclerView rvChat;
    private ArrayList<Messaggio> chat = new ArrayList<>();
    private Button bottonePositivo;
    private Button bottoneNegativo;
    private ListaMessaggiAdapter adapterChat;
    private View viewRoot;
    private TextView tvContatore;
    private Drink drinkCorrente;
    private int tempoDrink;
    private Button bottoneOutOfSight;
    private boolean outOfSight;
    private Object lockOOS = new Object();
    private boolean serveNuovoMessaggio;
    private String hobbies [];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_serving_interacting);

        rvChat = findViewById(R.id.recyclerViewChat);
        bottonePositivo = findViewById(R.id.bottoneRispostaPositivaChat);
        bottoneNegativo = findViewById(R.id.bottoneRispostaNegativaChat);
        viewRoot = findViewById(R.id.rootServing);
        tvContatore = findViewById(R.id.contatoreServingInteracting);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightServingInteracting);

        drinkCorrente = (Drink)getIntent().getSerializableExtra("DRINK");
        hobbies = getIntent().getStringExtra("HOBBIES").split("/");
        tempoDrink = drinkCorrente.getTempoPreparazione();

        adapterChat = new ListaMessaggiAdapter(ServingActivityInteracting.this, chat);
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


        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServingActivityInteracting.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });


        attivaGestioneComunicazione();

    }

    private void attivaGestioneComunicazione() {
        tvContatore.setText(String.valueOf(tempoDrink));
        Handler handlerContatore = new Handler();
        Handler handlerMessaggi = new Handler();
        Handler handler = new Handler();
        Thread threadMessaggi = new Thread(()->{
            int counter = 1;
            String richiesta = null;
            handlerMessaggi.post(() -> {
                bottonePositivo.setText("");
                bottoneNegativo.setText("");
                bottonePositivo.setClickable(false);
                bottoneNegativo.setClickable(false);
            });
            while (!Thread.currentThread().isInterrupted()) {
                switch (counter) {
                    case 1: richiesta = "inizio";
                            break;
                    case 2: richiesta = hobbies[0];
                        break;
                    case 3: richiesta = hobbies[1];
                        break;
                    case 4: richiesta = hobbies[2];
                        break;
                    default: richiesta = "caso";
                }

                SocketSingleton.getInstance().getSocketOut().print(richiesta);
                SocketSingleton.getInstance().getSocketOut().flush();
                Log.i("CIAO", "CIAO");
                try {
                    String cicloConversazione = SocketSingleton.getInstance().getSocketIn().readLine();
                    String setCiclo [] = cicloConversazione.split("/");
                    Log.i("CONV", cicloConversazione);
                    synchronized (chat) {
                        handlerMessaggi.post(() -> cicloUIMessaggi(setCiclo[0], setCiclo[1], setCiclo[2], setCiclo[3], setCiclo[4]));
                        while (!serveNuovoMessaggio)
                            chat.wait();
                        serveNuovoMessaggio = false;
                        Thread.sleep(2000);
                    }

                } catch (IOException e) {
                    handler.post(()->AlertBuilder.buildAlertSingoloBottone(ServingActivityInteracting.this, "Errore!", "C'è stato un errore di comunicazione, chiudere l'applicazione."));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                counter++;
            }
            SocketSingleton.getInstance().getSocketOut().print("fine");
            SocketSingleton.getInstance().getSocketOut().flush();
            try {
                String cicloConversazione = SocketSingleton.getInstance().getSocketIn().readLine();
                String setCiclo [] = cicloConversazione.split("/");
                handlerMessaggi.post(()->{
                    synchronized (chat) {
                        chat.add(new Messaggio(setCiclo[0], UtenteEnum.ROBOT));
                        adapterChat.notifyItemInserted(chat.size() - 1);
                        bottonePositivo.setClickable(true);
                        bottonePositivo.setText("Avanti");
                        bottoneNegativo.setText("");
                        bottoneNegativo.setClickable(false);
                        bottonePositivo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ServingActivityInteracting.this, FarewellingActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        });
                    }
                });
            } catch (IOException e) {
                handler.post(()->AlertBuilder.buildAlertSingoloBottone(ServingActivityInteracting.this, "Errore!", "C'è stato un errore di comunicazione, chiudere l'applicazione."));
            }
        });
        Thread threadContatore = new Thread(new Runnable() {
            @Override
            public void run() {
                while (tempoDrink > 0 && !Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    tempoDrink--;
                    handlerContatore.post(new Runnable() {
                        @Override
                        public void run() {
                            tvContatore.setText(String.valueOf(tempoDrink));
                        }
                    });
                }

                synchronized (lockOOS) {
                    while (outOfSight) {
                        try {
                            lockOOS.wait();
                        } catch (InterruptedException e) {
                            handlerContatore.post(()->AlertBuilder.buildAlertSingoloBottone(ServingActivityInteracting.this, "Errore!", "C'è stato un errore di comunicazione, riprovare!" + e.getMessage()));
                        }
                    }
                    threadMessaggi.interrupt();
                }

            }
        });
        threadContatore.start();
        threadMessaggi.start();
    }

    @Override
    public void onStart() {
        synchronized (lockOOS) {
            outOfSight = false;
            lockOOS.notifyAll();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {

    }

    private void cicloUIMessaggi(String domanda, String rispostaPositiva, String rispostaNegativa, String endPos, String endNeg) {

        synchronized (chat) {

            chat.add(new Messaggio(domanda, UtenteEnum.ROBOT));
            adapterChat.notifyItemInserted(chat.size() - 1);
            bottonePositivo.setText(rispostaPositiva);
            bottoneNegativo.setText(rispostaNegativa);
            bottonePositivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized(chat) {
                        chat.add(new Messaggio(rispostaPositiva, UtenteEnum.UTENTE));
                        adapterChat.notifyItemInserted(chat.size() - 1);
                        Handler handler = new Handler();
                        bottonePositivo.setText("");
                        bottoneNegativo.setText("");
                        bottonePositivo.setClickable(false);
                        bottoneNegativo.setClickable(false);

                        handler.postDelayed(new Runnable() {
                            public void run() {
                                synchronized (chat) {
                                    if (!chat.get(chat.size() - 1).getTesto().startsWith("Il tuo drink e' pronto.")) {
                                        chat.add(new Messaggio(endPos, UtenteEnum.ROBOT));
                                        adapterChat.notifyItemInserted(chat.size() - 1);
                                    }
                                    serveNuovoMessaggio = true;
                                    chat.notifyAll();
                                }
                            }
                        }, 1000);
                    }
                }
            });

            bottoneNegativo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized (chat) {
                        chat.add(new Messaggio(rispostaNegativa, UtenteEnum.UTENTE));
                        adapterChat.notifyItemInserted(chat.size() - 1);
                        Handler handler = new Handler();
                        bottonePositivo.setText("");
                        bottoneNegativo.setText("");
                        bottonePositivo.setClickable(false);
                        bottoneNegativo.setClickable(false);

                        handler.postDelayed(new Runnable() {
                            public void run() {
                                synchronized (chat) {
                                    if (!chat.get(chat.size() - 1).getTesto().startsWith("Il tuo drink e' pronto.")) {
                                        chat.add(new Messaggio(endNeg, UtenteEnum.ROBOT));
                                        adapterChat.notifyItemInserted(chat.size() - 1);
                                    }
                                    serveNuovoMessaggio = true;
                                    chat.notifyAll();
                                }
                            }
                        }, 1000);
                    }
                }
            });
            bottonePositivo.setClickable(true);
            bottoneNegativo.setClickable(true);
        }
    }

}