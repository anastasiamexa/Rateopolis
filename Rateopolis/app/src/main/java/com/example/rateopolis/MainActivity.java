package com.example.rateopolis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
    }

    // Method for user login
    public void login(View view){
        // If fields are complete
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                // Go to next activity
                                Intent intent = new Intent(MainActivity.this, MainCategories.class);
                                startActivity(intent);
                            } else {
                                showMessage(getString(R.string.error), task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else {
            showMessage(getString(R.string.error), getString(R.string.please_fill_all_fields));
        }
    }

    // Method for user sign up
    public void signup(View view) {
        // If fields are complete
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                // Go to next activity
                                Intent intent = new Intent(MainActivity.this, MainCategories.class);
                                startActivity(intent);
                            } else {
                                showMessage(getString(R.string.error), task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else {
            showMessage(getString(R.string.error), getString(R.string.please_fill_all_fields));
        }
    }

    // Method used for showing a message
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}