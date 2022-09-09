package com.example.rateopolis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class MainCategories extends AppCompatActivity {
    ImageButton events,stores;
    SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_categories);
        events = findViewById(R.id.imageButton);
        stores = findViewById(R.id.imageButton2);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    // Listener to detect shake movement
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) { // If shake event is detected, choose a random number (1 or 2)
                Toast.makeText(getApplicationContext(), getString(R.string.shake_event_detected), Toast.LENGTH_SHORT).show();
                int a = 1, b = 2; // 1 represents events, 2 represents stores
                int randomOfTwoInts = new Random().nextBoolean() ? a : b;
                // Go to the randomly picked activity
                if (randomOfTwoInts == 1) {
                    events.performClick();
                } else {
                    stores.performClick();
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // We don't use this method...
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener if sensor is available on the device
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener if sensor is available on the device
        mSensorManager.unregisterListener(mSensorListener);
    }


    public void events(View view){
        // Go to next activity
        Intent intent = new Intent(MainCategories.this, Location.class);
        intent.putExtra("type","Events");
        startActivity(intent);
    }

    public void stores(View view){
        // Go to next activity
        Intent intent = new Intent(MainCategories.this, Location.class);
        intent.putExtra("type","Stores");
        startActivity(intent);
    }
}