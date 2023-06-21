package com.example.robotstatesapplication.Models;

import android.widget.ImageView;

import java.io.Serializable;

public class Drink implements Serializable {

    private ImageView icona;
    private String nome;
    private CaratteristicaDrinkEnum caratteristica;
    private String alcol;
    private double prezzo;
    private int tempoPreparazione;

    public Drink(ImageView icona, String nome, CaratteristicaDrinkEnum caratteristica, String alcol) {
        this.icona = icona;
        this.nome = nome;
        this.caratteristica = caratteristica;
        this.alcol = alcol;
    }

    public Drink(String nome, double prezzo, int tempoPreparazione) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.tempoPreparazione = tempoPreparazione;
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

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getTempoPreparazione() {
        return tempoPreparazione;
    }

    public void setTempoPreparazione(int tempoPreparazione) {
        this.tempoPreparazione = tempoPreparazione;
    }
}
