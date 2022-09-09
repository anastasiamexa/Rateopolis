package com.example.rateopolis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivitiesList extends AppCompatActivity {
    static final int req_code1 = 112;
    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<Object> list;
    ArrayList<String> matches;
    public static String category;
    String activity,city;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);
        txt = findViewById(R.id.textView12);
        category = getIntent().getExtras().getString("category");
        activity = getIntent().getExtras().getString("activity");
        city = getIntent().getExtras().getString("city");

        if (activity.equals("Cinema")) {
            txt.setText(R.string.please_select_a_movie);
        } else if (activity.equals("Concert")) {
            txt.setText(R.string.please_select_a_concert);
        } else if (activity.equals("Theater")) {
            txt.setText(R.string.please_select_a_show);
        } else if (activity.equals("Club")) {
            txt.setText(R.string.please_select_a_night_club);
        } else if (activity.equals("Coffee")) {
            txt.setText(R.string.please_select_a_coffee_shop);
        } else if (activity.equals("Restaurant")) {
            txt.setText(R.string.please_select_a_restaurant);
        }

        // Initialize the recyclerView and get the database reference of the right path (category/city/activity)
        recyclerView = findViewById(R.id.eventList);
        database = FirebaseDatabase.getInstance().getReference(category + "/" + city + "/" + activity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        matches = new ArrayList<>();
        list = new ArrayList<>(); // this list contains all the data from database (array list of type Event or Store)

        // Set the adapter
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        // Read data from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // For every activity (child)
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (category.equals("Events")) {
                        // Create a Event object and set it's attributes, according to database values
                        Event event = dataSnapshot.getValue(Event.class);
                        list.add(event);
                    }
                    else if (category.equals("Stores")) {
                        // Create a Store object and set it's attributes, according to database values
                        Store store = dataSnapshot.getValue(Store.class);
                        list.add(store);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(getString(R.string.error), error.getMessage());
            }
        });
    }

    // Search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        // Get reference to menu item
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Get reference to search menu
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Set keyboard icon
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // There is no use for this method because the search happens on real time
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the results according to the newText
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    // Voice recognition method
    public void talk(View view) {
        // Request for microphone permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, req_code1);
        } else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.say_a_title));
            startActivityForResult(intent,123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == req_code1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.say_a_title));
            startActivityForResult(intent,123);
        }
    }

    // Results of voice recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Search for the best match of voice recognition
            myAdapter.getFilter().filter(matches.get(0));
        }
    }

    // Method used for showing a message
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}