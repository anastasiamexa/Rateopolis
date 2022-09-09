package com.example.rateopolis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RatingsList extends AppCompatActivity {
    TextView average;
    RecyclerView recyclerView;
    DatabaseReference database;
    myAdapterRating myAdapterRating;
    ArrayList<Rating> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_list);
        average = findViewById(R.id.avgRatings);
        String title = getIntent().getStringExtra("title");

        // Initialize the recyclerView and get the database reference of the right path (Ratings)
        recyclerView = findViewById(R.id.ratingsList);
        database = FirebaseDatabase.getInstance().getReference("Ratings");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>(); // this list contains all the data from database (array list of type Rating)

        // Set the adapter
        myAdapterRating = new myAdapterRating(this, list);
        recyclerView.setAdapter(myAdapterRating);

        // Read data from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // For every activity (child)
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    // Create a Rating object and set it's attributes, according to database values
                    Rating rating = dataSnapshot.getValue(Rating.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                    String date = sdf.format(Calendar.getInstance().getTime());
                    // Show only the ratings of current date
                    if (rating.getDate().equals(date) && rating.getTitle().equals(title)) {
                        list.add(rating);
                    }
                }
                if (list.size() == 0) {
                    average.setText(R.string.there_are_no_reviews);
                } else {
                    // Compute average score
                    double sum = 0;
                    for (Rating i: list) {
                        sum = sum + Double.parseDouble(i.getScore());
                    }
                    average.setText(getString(R.string.today_s_average_rating)+ " " + String.format("%.2f",sum/list.size()));
                }
                myAdapterRating.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(getString(R.string.error), error.getMessage());
            }
        });
    }

    // Method used for showing a message
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}