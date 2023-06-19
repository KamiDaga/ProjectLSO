package com.example.robotstatesapplication.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.robotstatesapplication.Views.QuestionarioDrinkActivity;
import com.example.robotstatesapplication.Views.QuestionarioHobbyActivity;

public class AlertBuilder {

    private static Dialog dialogAttesa;

    public static void buildAlertSingoloBottone(Context context, String titolo, String messaggio) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(titolo);
            builder.setMessage(messaggio)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static void mostraAlertAttesaCaricamento(Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Caricamento...");
            builder.setMessage("Attendere prego");
            builder.setCancelable(false);
            dialogAttesa = builder.create();
            dialogAttesa.show();
        }
    }

    public static void nascondiAlertAttesaCaricamento() {
        dialogAttesa.dismiss();
    }
}
