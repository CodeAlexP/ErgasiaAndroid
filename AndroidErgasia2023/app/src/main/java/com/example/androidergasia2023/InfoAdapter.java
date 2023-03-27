package com.example.androidergasia2023;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Context;

import java.util.ArrayList;


public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    android.content.Context context;
    private ArrayList<ArrayList<String>> list;

    public InfoAdapter(android.content.Context context, ArrayList<ArrayList<String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.ViewHolder holder, int position) {
        holder.InfoTime.setText(list.get(position).get(0));
        holder.InfoComments.setText(list.get(position).get(3));
        holder.InfoLati.setText(list.get(position).get(2));
        holder.InfoLongi.setText(list.get(position).get(1));
        Bitmap bitmap = decodeBase64(list.get(position).get(4));
        holder.InfoimageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView InfoTime, InfoLongi, InfoLati, InfoComments;
        ImageView InfoimageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            InfoTime=itemView.findViewById(R.id.InfoTime);
            InfoLongi=itemView.findViewById(R.id.InfoLongi);
            InfoLati=itemView.findViewById(R.id.InfoLati);
            InfoComments=itemView.findViewById(R.id.InfoComments);
            InfoimageView=itemView.findViewById(R.id.InfoimageView);
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
