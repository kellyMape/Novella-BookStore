package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    Button logoutbtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logoutbtn = findViewById(R.id.adminlogout);
        firebaseAuth = FirebaseAuth.getInstance();

        logoutbtn.setOnClickListener(view -> {

            firebaseAuth.signOut();

            Intent intent = new Intent(
                    AdminActivity.this,
                    LoginActivity.class);

            startActivity(intent);
            finish();
        });
    }
}