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

import java.util.ArrayList;

public class AdminUsersActivity extends AppCompatActivity {

    ListView listViewAdminUsers;
    TextView txtUsersSummary;
    ArrayList<String> usersList;
    ArrayAdapter<String> usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        listViewAdminUsers = findViewById(R.id.listViewAdminUsers);
        txtUsersSummary = findViewById(R.id.txtUsersSummary);

        findViewById(R.id.btnBackAdminUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        usersList = new ArrayList<>();
        usersAdapter = new ArrayAdapter<>(this, R.layout.item_review, usersList);
        listViewAdminUsers.setAdapter(usersAdapter);

        loadUsers();
    }

    // Read ALL the users (only the admin can do this)
    private void loadUsers() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersList.clear();
                        int customers = 0;

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String name = userSnapshot.child("name").getValue(String.class);
                            String email = userSnapshot.child("email").getValue(String.class);
                            String role = userSnapshot.child("role").getValue(String.class);

                            if (name == null) {
                                name = "No name";
                            }
                            if (role == null) {
                                role = "customer";
                            }
                            if (role.equals("customer")) {
                                customers++;
                            }

                            usersList.add(name + "   (" + role + ")\n" + email);
                        }

                        txtUsersSummary.setText(usersList.size() + " users  ·  " + customers + " customers");
                        usersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminUsersActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}