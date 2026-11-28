package com.example.novella;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import java.util.ArrayList;

public class PurchaseHistoryActivity extends AppCompatActivity {

    ListView listViewOrders;
    TextView txtNoOrders;
    ArrayList<String> ordersList;
    ArrayAdapter<String> ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_history);

        listViewOrders = findViewById(R.id.listViewOrders);
        txtNoOrders = findViewById(R.id.txtNoOrders);

        findViewById(R.id.btnBackHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Same white card design as the reviews
        ordersList = new ArrayList<>();
        ordersAdapter = new ArrayAdapter<>(this, R.layout.item_review, ordersList);
        listViewOrders.setAdapter(ordersAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        loadOrders(user.getUid());
    }

    // Read only the orders of this user
    private void loadOrders(String userId) {
        FirebaseDatabase.getInstance().getReference("orders")
                .orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ordersList.clear();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {

                            // Date
                            Long date = orderSnapshot.child("date").getValue(Long.class);
                            String dateText = (date != null) ? dateFormat.format(new Date(date)) : "";

                            // Book titles
                            String books = "";
                            for (DataSnapshot item : orderSnapshot.child("items").getChildren()) {
                                books = books + "• " + item.child("title").getValue(String.class) + "\n";
                            }

                            // Total and status
                            Double total = orderSnapshot.child("total").getValue(Double.class);
                            String status = orderSnapshot.child("status").getValue(String.class);

                            ordersList.add(0, dateText + "   " + status + "\n"
                                    + books
                                    + String.format(Locale.US, "Total: $%.2f", total));
                        }

                        if (ordersList.isEmpty()) {
                            txtNoOrders.setVisibility(View.VISIBLE);
                        } else {
                            txtNoOrders.setVisibility(View.GONE);
                        }

                        ordersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PurchaseHistoryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}