package com.example.robotstatesapplication.Models;

import android.widget.ImageView;

import java.io.Serializable;

public class Drink implements Serializable {

    private ImageView icona;
    private String nome;
    private CaratteristicaDrinkEnum caratteristica;
    private String alcol;

    public Drink(ImageView icona, String nome, CaratteristicaDrinkEnum caratteristica, String alcol) {
        this.icona = icona;
        this.nome = nome;
        this.caratteristica = caratteristica;
        this.alcol = alcol;
    }

    public Drink(String nome) {
        this.nome = nome;
    }

    public ImageView getIcona() {
        return icona;
    }

    public void setIcona(ImageView icona) {
        this.icona = icona;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CaratteristicaDrinkEnum getCaratteristica() {
        return caratteristica;
    }

    public void setCaratteristica(CaratteristicaDrinkEnum caratteristica) {
        this.caratteristica = caratteristica;
    }

    public String getAlcol() {
        return alcol;
    }

    public void setAlcol(String alcol) {
        this.alcol = alcol;
    }

    @Override
    public String toString() {
        return nome;
    }
}
