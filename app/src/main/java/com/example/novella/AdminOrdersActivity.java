package com.example.novella;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminOrdersActivity extends AppCompatActivity {

    ListView listViewAdminOrders;
    TextView txtOrdersSummary;
    ArrayList<String> ordersList;
    ArrayAdapter<String> ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        listViewAdminOrders = findViewById(R.id.listViewAdminOrders);
        txtOrdersSummary = findViewById(R.id.txtOrdersSummary);

        findViewById(R.id.btnBackAdminOrders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ordersList = new ArrayList<>();
        ordersAdapter = new ArrayAdapter<>(this, R.layout.item_review, ordersList);
        listViewAdminOrders.setAdapter(ordersAdapter);

        loadAllOrders();
    }

    // Read all the orders
    private void loadAllOrders() {
        FirebaseDatabase.getInstance().getReference("orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ordersList.clear();
                        double totalSales = 0;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {

                            Long date = orderSnapshot.child("date").getValue(Long.class);
                            String dateText = (date != null) ? dateFormat.format(new Date(date)) : "";

                            String email = orderSnapshot.child("userEmail").getValue(String.class);
                            String status = orderSnapshot.child("status").getValue(String.class);
                            Double total = orderSnapshot.child("total").getValue(Double.class);
                            if (total == null) {
                                total = 0.0;
                            }

                            // Books of this order
                            String books = "";
                            for (DataSnapshot item : orderSnapshot.child("items").getChildren()) {
                                books = books + "• " + item.child("title").getValue(String.class) + "\n";
                            }

                            ordersList.add(0, dateText + "   " + status + "\n"
                                    + "Customer: " + email + "\n"
                                    + books
                                    + String.format(Locale.US, "Total: $%.2f", total));

                            totalSales = totalSales + total;
                        }

                        txtOrdersSummary.setText(String.format(Locale.US,
                                "%d orders  ·  $%.2f sold", ordersList.size(), totalSales));

                        ordersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminOrdersActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}