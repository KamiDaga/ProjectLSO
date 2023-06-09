package com.example.robotstatesapplication.Models;

import android.widget.ImageView;

public class Hobby {

    private ImageView icona;
    private String descrizione;

    public Hobby(ImageView icona, String descrizione) {
        this.icona = icona;
        this.descrizione = descrizione;
    }

    public ImageView getIcona() {
        return icona;
    }

    public void setIcona(ImageView icona) {
        this.icona = icona;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String toString() {
        return descrizione;
    }
}
