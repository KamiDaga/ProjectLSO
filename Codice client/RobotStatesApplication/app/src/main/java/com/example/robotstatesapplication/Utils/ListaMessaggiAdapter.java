package com.example.robotstatesapplication.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robotstatesapplication.Models.Messaggio;
import com.example.robotstatesapplication.Models.UtenteEnum;
import com.example.robotstatesapplication.R;

import java.util.List;

public class ListaMessaggiAdapter extends RecyclerView.Adapter {

    private static final int CONST_ROBOT = 1;
    private static final int CONST_UTENTE = 2;
    private Context context;
    private List<Messaggio> listaMessaggi;

    public ListaMessaggiAdapter (Context context, List<Messaggio> lista) {
        this.context = context;
        listaMessaggi = lista;
    }

    @Override
    public int getItemViewType(int position) {
        Messaggio messaggioCorrente = listaMessaggi.get(position);

        if (messaggioCorrente.getAutore().equals(UtenteEnum.UTENTE)) {
            // If the current user is the sender of the message
            return CONST_UTENTE;
        } else {
            // If some other user sent the message
            return CONST_ROBOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == CONST_ROBOT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messaggi_robot_chat_layout, parent, false);
            return new MessaggioRicevutoHolder(view);
        } else if (viewType == CONST_UTENTE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messaggi_utente_chat_layout, parent, false);
            return new MessaggioInviatoHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messaggio messaggio = (Messaggio) listaMessaggi.get(position);

        switch (holder.getItemViewType()) {
            case CONST_UTENTE:
                ((MessaggioInviatoHolder) holder).bind(messaggio);
                break;
            case CONST_ROBOT:
                ((MessaggioRicevutoHolder) holder).bind(messaggio);
        }
    }

    private class MessaggioInviatoHolder extends RecyclerView.ViewHolder {
        TextView textViewTesto;

        MessaggioInviatoHolder(View itemView) {
            super(itemView);

            textViewTesto = (TextView) itemView.findViewById(R.id.textViewTestoMessaggioInviato);
        }

        void bind(Messaggio messaggio) {
            textViewTesto.setText(messaggio.getTesto());
        }
    }

    private class MessaggioRicevutoHolder extends RecyclerView.ViewHolder {
        TextView textViewTesto;

        MessaggioRicevutoHolder(View itemView) {
            super(itemView);

            textViewTesto = (TextView) itemView.findViewById(R.id.textViewTestoMessaggioRicevuto);

        }

        void bind(Messaggio messaggio) {
            textViewTesto.setText(messaggio.getTesto());
        }
    }

    @Override
    public int getItemCount() {
        return listaMessaggi.size();
    }
}
