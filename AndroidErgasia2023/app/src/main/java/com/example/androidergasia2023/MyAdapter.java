package com.example.androidergasia2023;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    public static  ArrayList<ArrayList<ArrayList<String>>> list;
    ArrayList<ArrayList<String>> GeneralData;

    public MyAdapter(Context context, ArrayList<ArrayList<ArrayList<String>>> list, ArrayList<ArrayList<String>> GeneralData) {
        this.context = context;
        this.list = list;
        this.GeneralData = GeneralData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userentry,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.longi.setText(GeneralData.get(position).get(1));
        holder.lati.setText(GeneralData.get(position).get(2));
        holder.spinvalue.setText(GeneralData.get(position).get(0));
        holder.usercomm.setText(GeneralData.get(position).get(3));

        holder.notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              try {
                  Intent intent = new Intent(v.getContext(), InfoPage.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  intent.putExtra("First:", String.valueOf(holder.getAdapterPosition()));
                  v.getContext().startActivity(intent);
              }catch (Exception e){
                  Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
              }
            }
        });
        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("data").child("events");
                for (int i = 0; i < list.get(holder.getAdapterPosition()).size(); i++) {
                    databaseReference.child(list.get(holder.getAdapterPosition()).get(i).get(5)).removeValue();
                }
                Intent intent = new Intent(v.getContext(), EmployeePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return GeneralData.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder{
        TextView spinvalue,usercomm,longi,lati;
        Button info,deny,notify;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            spinvalue=itemView.findViewById(R.id.textspinvalue);

            usercomm=itemView.findViewById(R.id.textusercomm);
            longi=itemView.findViewById(R.id.textlongi);
            lati=itemView.findViewById(R.id.textlati);
            info=itemView.findViewById(R.id.buttoninfo);
            deny=itemView.findViewById(R.id.buttonDeny);
            notify=itemView.findViewById(R.id.buttonnotify);
        }
    }


}
