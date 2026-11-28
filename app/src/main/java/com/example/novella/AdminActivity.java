package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    Button logoutbtn, btnManageBooks, btnAddBook, btnOrders, btnUsers;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logoutbtn = findViewById(R.id.adminlogout);
        btnManageBooks = findViewById(R.id.btnManageBooks);
        btnAddBook = findViewById(R.id.btnAddBook);
        btnOrders = findViewById(R.id.btnOrders);
        btnUsers = findViewById(R.id.btnUsers);

        firebaseAuth = FirebaseAuth.getInstance();

        // Open Manage Books
        btnManageBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageBooksActivity.class);
                startActivity(intent);
            }
        });

        // Open Add Book
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        // Open Orders
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminOrdersActivity.class);
                startActivity(intent);
            }
        });

        // Open Users
        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminUsersActivity.class);
                startActivity(intent);
            }
        });

        // Log out
        logoutbtn.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}