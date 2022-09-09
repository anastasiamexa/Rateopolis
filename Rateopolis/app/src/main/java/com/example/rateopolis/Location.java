package com.example.rateopolis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity implements LocationListener, SensorEventListener {

    static final int req_code = 111;
    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor tempSensor;
    DatabaseReference database;
    TextView t1, t2, t3, t4, t5;
    ImageButton img1, img2, img3;
    String category, city = "", temp;
    Boolean isTemp;
    Spinner dropdown;
    ArrayList<String> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        dropdown = findViewById(R.id.spinner);
        dropdown.setEnabled(false);
        img1 = findViewById(R.id.imageButton4);
        img1.setEnabled(false);
        img2 = findViewById(R.id.imageButton5);
        img2.setEnabled(false);
        img3 = findViewById(R.id.imageButton6);
        img3.setEnabled(false);
        t1 = findViewById(R.id.textView5);
        t2 = findViewById(R.id.textView6);
        t3 = findViewById(R.id.textView9);
        t4 = findViewById(R.id.textView10);
        t5 = findViewById(R.id.textView11);
        category = getIntent().getExtras().getString("type");

        // Check if temperature sensor is available
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTemp = true;
        } else {
            showMessage(getString(R.string.attention), getString(R.string.temperature_sensor_is_not_available));
            isTemp = false;
        }

        // Request for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, req_code);
        } else {
            // Check if GPS setting is disabled
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showMessage(getString(R.string.attention), getString(R.string.your_GPS_is_disabled_please_enable_it));
            } else { // If GPS setting is enabled
                // Get location updates every 2 seconds and only if distance change is more than 1000 meters
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1000, Location.this);
            }
        }

        // Get different reference path of database according to user's choice
        if (category.equals("Events")) {
           database = FirebaseDatabase.getInstance().getReference("Events");
           img1.setImageResource(R.drawable.cinema);
           img2.setImageResource(R.drawable.concert);
           img3.setImageResource(R.drawable.theater);
           t3.setText(R.string.cinema);
           t4.setText(R.string.concert);
           t5.setText(R.string.theater);
        } else if (category.equals("Stores")) {
            database = FirebaseDatabase.getInstance().getReference("Stores");
            img1.setImageResource(R.drawable.coffee);
            img2.setImageResource(R.drawable.restaurant);
            img3.setImageResource(R.drawable.club);
            t3.setText(R.string.coffee);
            t4.setText(R.string.restaurant);
            t5.setText(R.string.club);
        }

        // Read data from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // For every city (child)
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    // Create a list of cities for the spinner.
                    cities.add(dataSnapshot.getKey());
                }
                // Enable the dropdown and the image buttons, only after the data have been loaded from database
                dropdown.setEnabled(true);
                img1.setEnabled(true);
                img2.setEnabled(true);
                img3.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(getString(R.string.error), error.getMessage());
            }
        });

        // Dropdown
        cities.add(getString(R.string.select_a_city));
        // Create an adapter to describe how the items are displayed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        // Set the spinners adapter to the previously created one
        dropdown.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == req_code && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // Check if GPS setting is disabled
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showMessage(getString(R.string.attention), getString(R.string.your_GPS_is_disabled_please_enable_it));
            } else { // If GPS setting is enabled
                // Get location updates every 2 seconds and only if distance change is more than 1000 meters
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1000, Location.this);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       temp = event.values[0] + " °C";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // We don't use this method...
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener if sensor is available on the device
        if (isTemp == true) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener if sensor is available on the device
        if (isTemp == true) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        // Using Geocoder to capture current address information
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            // Here 1 represents max location result to be returned
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        t1.setText(getString(R.string.your_current_location_is) + " " + city + " " + temp);
    }

    // For Cinema or Coffee
    public void leftImgPressed(View view) {
        if (category.equals("Events")) {
            selectActivity(category, "Cinema");
        } else if (category.equals("Stores")) {
            selectActivity(category, "Coffee");
        }
    }

    // For Concert or Restaurant
    public void middleImgPressed(View view) {
        if (category.equals("Events")) {
            selectActivity(category, "Concert");
        } else if (category.equals("Stores")) {
            selectActivity(category, "Restaurant");
        }
    }

    // For Theater or Club
    public void rightImgPressed(View view) {
        if (category.equals("Events")) {
            selectActivity(category, "Theater");
        } else if (category.equals("Stores")) {
            selectActivity(category, "Club");
        }
    }

    // Navigate to next activity according to user's choice
    public void selectActivity(String cat, String act) {
        if (dropdown.getSelectedItem().toString().equals(getString(R.string.select_a_city))){
            if (cities.contains(city)){
                Intent intent = new Intent(this, ActivitiesList.class);
                intent.putExtra("category", cat);
                intent.putExtra("activity", act);
                intent.putExtra("city", city);
                startActivity(intent);
            } else {
                showMessage(getString(R.string.error), getString(R.string.this_location_is_not_supported_yet));
            }
        } else {
            Intent intent = new Intent(this, ActivitiesList.class);
            intent.putExtra("category", cat);
            intent.putExtra("activity", act);
            intent.putExtra("city", dropdown.getSelectedItem().toString());
            startActivity(intent);
        }
    }

    // Method used for showing a message
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}