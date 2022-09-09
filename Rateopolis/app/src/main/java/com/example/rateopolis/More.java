package com.example.rateopolis;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class More extends AppCompatActivity {

    ImageView mCover;
    TextView mTitle, mAddress, mArea, mHour, mComments;
    RatingBar ratingBar;
    String title, cover, area, hour;
    Boolean flag = true;
    public static String latitude, longitude, address;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference database;
    private FirebaseAuth mAuth;
    String c = ActivitiesList.category;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
    String date = sdf.format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Ratings");

        mTitle = findViewById(R.id.mTitle);
        mAddress = findViewById(R.id.mAddress);
        mArea = findViewById(R.id.mArea);
        mHour = findViewById(R.id.mHour);
        mCover = findViewById(R.id.mCover);
        mComments = findViewById(R.id.mComments);
        ratingBar = findViewById(R.id.ratingBar);

        // Read data from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // For every activity (child)
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Rating r = dataSnapshot.getValue(Rating.class);
                    // Check if user has already reviewed this activity
                    if (r.getTitle().equals(title) && r.getDate().equals(date) && r.getUid().equals(mAuth.getCurrentUser().getEmail())) {
                        flag = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(getString(R.string.error), error.getMessage());
            }
        });

        getData();
        setData();
    }

    // Method used to get the data from previous activity, using the getters of event or store object
    private void getData() {
        if (c.equals("Events")) {
            if(getIntent().hasExtra("object")) {
                Event event  = getIntent().getParcelableExtra("object");
                title = event.getTitle();
                address = event.getAddress();
                area = event.getArea();
                hour = event.getHour();
                latitude = event.getLatitude();
                longitude = event.getLongitude();
                cover = event.getCover();
            } else {
                Toast.makeText(this,"No data", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(getIntent().hasExtra("object")) {
                Store store  = getIntent().getParcelableExtra("object");
                title = store.getTitle();
                address = store.getAddress();
                area = store.getArea();
                hour = store.getHour();
                latitude = store.getLatitude();
                longitude = store.getLongitude();
                cover = store.getCover();
            } else {
                Toast.makeText(this,"No data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method used to set the data to the appropriate views
    private void setData() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mTitle.setText(title);
        mAddress.setText(getString(R.string.address) + " " + address);
        mHour.setText(getString(R.string.hour) + " " + hour);
        mArea.setText(getString(R.string.area) + " " + area);

        // Get images from Firebase Storage
        StorageReference imageRef = storageReference.child(cover);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            // Use Glide to get the image through url
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(More.this).load(uri).into(mCover);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getLocalizedMessage();
            }
        });
    }

    // Method for show map button
    public void showMap(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    // Method for submit review button
    public void submit(View view) {
        // Getting the rating
        String rt = String.valueOf(ratingBar.getRating());
        String comments = mComments.getText().toString();

        Rating rating = new Rating(date, rt, title, mAuth.getCurrentUser().getEmail(), comments);

        if (flag == true) {
            // Insert rating to database
            database.push().setValue(rating);
            showMessage(getString(R.string.success), getString(R.string.rating_saved_successfully));
        } else {
            showMessage(getString(R.string.error), getString(R.string.you_have_already_reviewed_this_activity));
        }
    }

    // Method for show ratings button
    public void showRatings(View view) {
        Intent intent = new Intent(this, RatingsList.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    // Method used for showing a message
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}