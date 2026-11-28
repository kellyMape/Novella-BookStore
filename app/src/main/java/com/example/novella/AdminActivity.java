package com.example.novella;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    Button logoutbtn, btnManageBooks, btnAddBook, btnOrders, btnUsers;
    FirebaseAuth firebaseAuth;
    TextView txtRevenue, txtOrdersCount, txtBooksSold, txtMonthlySales, txtBestSellers;

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

        txtRevenue = findViewById(R.id.txtRevenue);
        txtOrdersCount = findViewById(R.id.txtOrdersCount);
        txtBooksSold = findViewById(R.id.txtBooksSold);
        txtMonthlySales = findViewById(R.id.txtMonthlySales);
        txtBestSellers = findViewById(R.id.txtBestSellers);

        loadStats();

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

    // Read all the orders and calculate the statistics
    private void loadStats() {
        FirebaseDatabase.getInstance().getReference("orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int ordersCount = 0;
                        int booksSold = 0;
                        double revenue = 0;

                        // title -> how many times it was sold
                        HashMap<String, Integer> bookCount = new HashMap<>();
                        // month -> money (TreeMap keeps the months in order)
                        TreeMap<String, Double> monthSales = new TreeMap<>();

                        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.US);

                        for (DataSnapshot order : snapshot.getChildren()) {
                            ordersCount++;

                            Double total = order.child("total").getValue(Double.class);
                            if (total == null) {
                                total = 0.0;
                            }
                            revenue = revenue + total;

                            // Sales by month
                            Long date = order.child("date").getValue(Long.class);
                            if (date != null) {
                                String month = monthFormat.format(new Date(date));
                                Double old = monthSales.get(month);
                                if (old == null) {
                                    old = 0.0;
                                }
                                monthSales.put(month, old + total);
                            }

                            // Count each book
                            for (DataSnapshot item : order.child("items").getChildren()) {
                                booksSold++;
                                String title = item.child("title").getValue(String.class);
                                Integer count = bookCount.get(title);
                                if (count == null) {
                                    count = 0;
                                }
                                bookCount.put(title, count + 1);
                            }
                        }

                        // 3 numbers
                        txtRevenue.setText(String.format(Locale.US, "$%.2f", revenue));
                        txtOrdersCount.setText(String.valueOf(ordersCount));
                        txtBooksSold.setText(String.valueOf(booksSold));

                        // Sales by month
                        String months = "";
                        for (String month : monthSales.keySet()) {
                            months = months + month + "   " + String.format(Locale.US, "$%.2f", monthSales.get(month)) + "\n";
                        }
                        txtMonthlySales.setText(months.isEmpty() ? "No sales yet" : months.trim());

                        // Top 3 best sellers
                        String best = "";
                        for (int i = 1; i <= 3 && !bookCount.isEmpty(); i++) {
                            String topTitle = null;
                            int topCount = 0;
                            for (String title : bookCount.keySet()) {
                                if (bookCount.get(title) > topCount) {
                                    topTitle = title;
                                    topCount = bookCount.get(title);
                                }
                            }
                            best = best + i + ". " + topTitle + "  (" + topCount + " sold)\n";
                            bookCount.remove(topTitle);
                        }
                        txtBestSellers.setText(best.isEmpty() ? "No sales yet" : best.trim());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}