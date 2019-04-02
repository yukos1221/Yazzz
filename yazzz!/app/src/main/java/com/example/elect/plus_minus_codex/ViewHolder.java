package com.example.elect.plus_minus_codex;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView textviMesg;
    TextView textviName;
    CardView card;

    public ViewHolder(View itemView) {
        super(itemView);
        textviMesg = itemView.findViewById(R.id.text_item);
        textviName = itemView.findViewById(R.id.nameid);
    }
}
