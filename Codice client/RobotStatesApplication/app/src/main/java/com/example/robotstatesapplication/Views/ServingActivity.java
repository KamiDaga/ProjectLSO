package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.robotstatesapplication.Models.Messaggio;
import com.example.robotstatesapplication.Models.UtenteEnum;
import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Utils.ListaMessaggiAdapter;

import java.util.ArrayList;

public class ServingActivity extends AppCompatActivity {

    private RecyclerView rvChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serving);

        rvChat = findViewById(R.id.recyclerViewChat);

        ArrayList<Messaggio> chat = new ArrayList<>();

        chat.add(new Messaggio("Ciao, sono il Robot", UtenteEnum.ROBOT));
        chat.add(new Messaggio("Ciao, sono l'utente", UtenteEnum.UTENTE));
        chat.add(new Messaggio("Ti provo a rispondere", UtenteEnum.ROBOT));
        chat.add(new Messaggio("Anche io", UtenteEnum.UTENTE));


        rvChat.setAdapter(new ListaMessaggiAdapter(ServingActivity.this, chat));
        rvChat.setLayoutManager(new LinearLayoutManager(this));

    }
}