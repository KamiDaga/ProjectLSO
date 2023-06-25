package com.example.robotstatesapplication.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;

import com.example.robotstatesapplication.R;
import com.example.robotstatesapplication.Views.QuestionarioDrinkActivity;
import com.example.robotstatesapplication.Views.QuestionarioHobbyActivity;

import java.util.logging.Handler;

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
            Button OkButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            OkButton.setBackgroundColor(Color.parseColor("#0E0D57"));
            OkButton.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

}
