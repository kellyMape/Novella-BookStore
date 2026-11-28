package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    Button logoutbtn, btnManageBooks, btnAddBooks, btnOrders, btnUsers;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logoutbtn = findViewById(R.id.adminlogout);
        btnManageBooks = findViewById(R.id.btnManageBooks);
        btnAddBooks = findViewById(R.id.btnAddBook);
        btnOrders = findViewById(R.id.btnOrders);
        btnUsers = findViewById(R.id.btnUsers);

        firebaseAuth = FirebaseAuth.getInstance();

        btnManageBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageBooksActivity.class);
                startActivity(intent);
            }
        });

        btnAddBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AdminActivity.this, AdminOrdersActivity.class);
                startActivity(intent);
            }
        });

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

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