package com.example.robotstatesapplication.Models;

public class Messaggio {
    private String testo;
    private UtenteEnum autore;

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public UtenteEnum getAutore() {
        return autore;
    }

    public void setAutore(UtenteEnum autore) {
        this.autore = autore;
    }

    public Messaggio(String testo, UtenteEnum autore) {
        this.testo = testo;
        this.autore = autore;
    }

}
