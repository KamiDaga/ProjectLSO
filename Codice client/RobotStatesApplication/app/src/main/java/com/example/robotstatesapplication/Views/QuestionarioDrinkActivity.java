package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.robotstatesapplication.Models.CaratteristicaDrinkEnum;
import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QuestionarioDrinkActivity extends AppCompatActivity {

    private Map<Drink, Boolean> drinks = new HashMap<>();
    private Button bottoneAvanti;
    private Button bottoneIndietro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_questionario_drink);


        drinks.put(new Drink(findViewById(R.id.iconaDrink1), "Pina colada", CaratteristicaDrinkEnum.DOLCE, "Rum"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink2), "Mudslide", CaratteristicaDrinkEnum.DOLCE, "Vodka"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink3), "Espress 75", CaratteristicaDrinkEnum.DOLCE, "Gin"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink4), "Rum Manhattan", CaratteristicaDrinkEnum.AMARO, "Rum"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink5), "Negroski", CaratteristicaDrinkEnum.AMARO, "Vodka"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink6), "Martini", CaratteristicaDrinkEnum.AMARO, "Gin"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink7), "Mojito", CaratteristicaDrinkEnum.FRUTTATO, "Rum"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink8), "Cosmopolitan", CaratteristicaDrinkEnum.FRUTTATO, "Vodka"), false);
        drinks.put(new Drink(findViewById(R.id.iconaDrink9), "Gin fizz", CaratteristicaDrinkEnum.FRUTTATO, "Gin"), false);

        bottoneAvanti = findViewById(R.id.bottoneInizioQuestionarioHobby);
        bottoneIndietro = findViewById(R.id.bottoneAnnullaQuestionarioDrink);

        for (Drink drink : drinks.keySet()) {
            drink.getIcona().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drinks.get(drink).equals(false)) {
                        drink.getIcona().setColorFilter(0x552B3881);
                        drinks.put(drink, true);
                    }
                    else {
                        drink.getIcona().clearColorFilter();
                        drinks.put(drink, false);
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
                Collection<Drink> drinkScelti = new ArrayList<>();
                for (Drink drink : drinks.keySet()) {
                    if (drinks.get(drink)) {
                        drinkScelti.add(drink);
                    }
                }
                if (drinkScelti.size() != 3) {

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QuestionarioDrinkActivity.this);
                    builder.setMessage("Ti consigliamo questo drink: "+ estraiValoriQuestionario(drinkScelti) + "!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(QuestionarioDrinkActivity.this, QuestionarioHobbyActivity.class);
                                    startActivity(i);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    private Drink estraiValoriQuestionario (Collection<Drink> drinkScelti) {
        Map<String, Integer> valoriAlcolQuestionario = new HashMap<>();
        Map<String, Integer> valoriCaratteristicaQuestionario = new HashMap<>();
        for (Drink drink : drinkScelti) {
            String alcol = drink.getAlcol();
            String caratteristica = drink.getCaratteristica().name();
            if (valoriAlcolQuestionario.containsKey(alcol))
                valoriAlcolQuestionario.put(alcol, valoriAlcolQuestionario.get(alcol)+1);
            else
                valoriAlcolQuestionario.put(alcol, 1);

            if (valoriCaratteristicaQuestionario.containsKey(caratteristica))
                valoriCaratteristicaQuestionario.put(caratteristica, valoriCaratteristicaQuestionario.get(caratteristica)+1);
            else
                valoriCaratteristicaQuestionario.put(caratteristica, 1);
        }


        String caratteristicaMax = null;
        String alcolMax = null;
        int caratteristicaMaxValore = 0;
        int alcolMaxValore = 0;

        for (String s :valoriAlcolQuestionario.keySet()) {
            if (valoriAlcolQuestionario.get(s) > alcolMaxValore) {
                alcolMax = s;
                alcolMaxValore = valoriAlcolQuestionario.get(s);
            }
        }
        for (String s :valoriCaratteristicaQuestionario.keySet()) {
            if (valoriCaratteristicaQuestionario.get(s) > caratteristicaMaxValore) {
                caratteristicaMax = s;
                caratteristicaMaxValore = valoriCaratteristicaQuestionario.get(s);
            }
        }


        for (Drink d : drinks.keySet()) {
            if (d.getAlcol().equals(alcolMax) && d.getCaratteristica().name().equals(caratteristicaMax))
                return d;
        }

        return null;
    }
}