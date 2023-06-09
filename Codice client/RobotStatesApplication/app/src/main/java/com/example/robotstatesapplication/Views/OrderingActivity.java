package com.example.robotstatesapplication.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.robotstatesapplication.Models.CaratteristicaDrinkEnum;
import com.example.robotstatesapplication.Models.Drink;
import com.example.robotstatesapplication.R;

import java.util.ArrayList;
import java.util.List;

public class OrderingActivity extends AppCompatActivity {

    private RadioGroup gruppoRadioDrink;
    private Button bottoneOutOfSight;
    private Button bottoneConferma;
    private Button bottoneEsci;
    private List<Drink> listaDrink = new ArrayList<>();

    private Drink drinkScelto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        gruppoRadioDrink = findViewById(R.id.radioGroupDrink);
        bottoneConferma = findViewById(R.id.bottoneConfermaDrinkSceltoOrdering);
        bottoneEsci = findViewById(R.id.bottoneTornaAlLoginOrdering);
        bottoneOutOfSight = findViewById(R.id.bottoneOutOfSightOrdering);

        listaDrink.add(new Drink(null, "Negroni", CaratteristicaDrinkEnum.AMARO, "Bella domanda"));
        listaDrink.add(new Drink(null, "Tequila Sunrise", CaratteristicaDrinkEnum.FRUTTATO, "Tequila"));
        listaDrink.add(new Drink(null, "Sex on the beach", CaratteristicaDrinkEnum.FRUTTATO, "Bella domanda"));
        listaDrink.add(new Drink(null, "Aperol Spritz", CaratteristicaDrinkEnum.FRUTTATO, "Aperol"));
        listaDrink.add(new Drink(null, "Pina colada", CaratteristicaDrinkEnum.DOLCE, "Rum"));
        listaDrink.add(new Drink(null, "Mudslide", CaratteristicaDrinkEnum.DOLCE, "Vodka"));
        listaDrink.add(new Drink(null, "Espress 75", CaratteristicaDrinkEnum.DOLCE, "Gin"));
        listaDrink.add(new Drink(null, "Rum Manhattan", CaratteristicaDrinkEnum.AMARO, "Rum"));
        listaDrink.add(new Drink(null, "Negroski", CaratteristicaDrinkEnum.AMARO, "Vodka"));
        listaDrink.add(new Drink(null, "Martini", CaratteristicaDrinkEnum.AMARO, "Gin"));
        listaDrink.add(new Drink(null, "Mojito", CaratteristicaDrinkEnum.FRUTTATO, "Rum"));
        listaDrink.add(new Drink(null, "Cosmopolitan", CaratteristicaDrinkEnum.FRUTTATO, "Vodka"));
        listaDrink.add(new Drink(null, "Gin fizz", CaratteristicaDrinkEnum.FRUTTATO, "Gin"));

        for (Drink drink : listaDrink) {
            RadioButton radioDrinkCorrente = new RadioButton(OrderingActivity.this);
            radioDrinkCorrente.setText(drink.getNome());
            radioDrinkCorrente.setTextColor(getResources().getColor(R.color.blu_scuro));
            radioDrinkCorrente.setBackgroundResource(R.drawable.radiobuttons_bg);
            radioDrinkCorrente.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.blu_scuro)));
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 20);
            gruppoRadioDrink.addView(radioDrinkCorrente, layoutParams);
        }

        bottoneConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drinkScelto != null) {
                    Intent i = new Intent(OrderingActivity.this, ServingActivityInteracting.class);
                    i.putExtra("Drink", drinkScelto.getNome());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });

        bottoneEsci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderingActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        bottoneOutOfSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderingActivity.this, OutOfSightActivity.class);
                startActivity(i);
            }
        });
    }

    public void setDrinkScelto (Drink drinkCliccato) {
        drinkScelto = drinkCliccato;
    }
}