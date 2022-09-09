package com.example.rateopolis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myAdapterRating extends RecyclerView.Adapter<myAdapterRating.MyViewHolder>  {
    Context context;
    ArrayList<Rating> list;

    // Constructor
    public myAdapterRating(Context context, ArrayList<Rating> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false);
        return new MyViewHolder(v);
    }

    // Method to bind the view in Card view with data in Rating class
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Add attribute values from model class to appropriate view in Card view
        Rating rating = list.get(position);
        holder.user.setText(rating.getUid());
        holder.comments.setText(rating.getComment());
        holder.score.setRating(Float.parseFloat(rating.getScore()));
        holder.nscore.setText(rating.getScore() + "/5.0");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Class to create references of the views in Card view
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView comments, user, nscore;
        RatingBar score;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            comments = itemView.findViewById(R.id.comments);
            user = itemView.findViewById(R.id.user);
            score = itemView.findViewById(R.id.ratingBar2);
            nscore = itemView.findViewById(R.id.nScore);
            mainLayout = itemView.findViewById(R.id.mainLayout1);
        }
    }
}
