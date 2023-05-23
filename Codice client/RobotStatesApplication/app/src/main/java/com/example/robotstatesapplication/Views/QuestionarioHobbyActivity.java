package com.example.robotstatesapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.robotstatesapplication.Models.CaratteristicaDrinkEnum;
import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.Models.Hobby;
import com.example.robotstatesapplication.Views.LoginActivity;
import com.example.robotstatesapplication.Views.QuestionarioDrinkActivity;
import com.example.robotstatesapplication.Views.RegistrazioneActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QuestionarioHobbyActivity extends AppCompatActivity {

    private Button bottoneAvanti;
    private Button bottoneIndietro;
    private Map<Hobby, Boolean> hobbies = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario_hobby);

        hobbies.put(new Hobby(findViewById(R.id.iconaHobby1), "Sport"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby2), "Film"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby3), "Giardinaggio"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby4), "Musica"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby5), "Videogiochi"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby6), "Cucina"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby7), "Teatro"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby8), "Arte"), false);
        hobbies.put(new Hobby(findViewById(R.id.iconaHobby9), "Lettura"), false);

        bottoneAvanti = findViewById(R.id.bottoneFineQuestionario);
        bottoneIndietro = findViewById(R.id.bottoneAnnullaQuestionarioHobby);

        for (Hobby hobby : hobbies.keySet()) {
            hobby.getIcona().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hobbies.get(hobby).equals(false)) {
                        hobby.getIcona().setColorFilter(0x552B3881);
                        hobbies.put(hobby, true);
                    }
                    else {
                        hobby.getIcona().clearColorFilter();
                        hobbies.put(hobby, false);
                    }
                }
            });
        }

        bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bottoneAvanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collection<Hobby> hobbyScelti = new ArrayList<>();
                for (Hobby hobby : hobbies.keySet()) {
                    if (hobbies.get(hobby)) {
                        hobbyScelti.add(hobby);
                    }
                }
                if (hobbyScelti.size() != 3) {

                }
                else {
                    Log.i("Questionario", hobbyScelti.toString());

                    Intent i = new Intent(QuestionarioHobbyActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

            }
        });

    }
}