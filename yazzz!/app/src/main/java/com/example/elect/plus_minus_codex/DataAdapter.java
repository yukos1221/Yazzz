package com.example.elect.plus_minus_codex;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<String> mesgs;
    LayoutInflater inflater;

    public DataAdapter(Context context, ArrayList<String> mesgs) {
        this.mesgs = mesgs;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.message_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String msg = mesgs.get(i);
        viewHolder.textview1.setText(msg);
    }

    @Override
    public int getItemCount() {
        return mesgs.size();
    }
}
