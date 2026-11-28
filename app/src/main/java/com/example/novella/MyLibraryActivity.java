package com.example.novella;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyLibraryActivity extends AppCompatActivity {

    ListView listViewLibrary;
    TextView txtEmptyLibrary;
    ArrayList<Book> libraryBooks;
    LibraryAdapter libraryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        listViewLibrary = findViewById(R.id.listViewLibrary);
        txtEmptyLibrary = findViewById(R.id.txtEmptyLibrary);

        findViewById(R.id.btnBackLibrary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        libraryBooks = new ArrayList<>();
        libraryAdapter = new LibraryAdapter(this, libraryBooks);
        listViewLibrary.setAdapter(libraryAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        loadLibrary(user.getUid());
    }

    // 1) Read the orders of this user and collect the book IDs
    private void loadLibrary(String userId) {
        FirebaseDatabase.getInstance().getReference("orders")
                .orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> bookIds = new ArrayList<>();

                        for (DataSnapshot order : snapshot.getChildren()) {
                            for (DataSnapshot item : order.child("items").getChildren()) {
                                // Don't add the same book twice
                                if (!bookIds.contains(item.getKey())) {
                                    bookIds.add(item.getKey());
                                }
                            }
                        }

                        loadBooks(bookIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyLibraryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // 2) Read the books and keep only the ones the user bought
    private void loadBooks(ArrayList<String> bookIds) {
        FirebaseDatabase.getInstance().getReference("books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        libraryBooks.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            if (bookIds.contains(child.getKey())) {
                                Book book = child.getValue(Book.class);
                                if (book != null) {
                                    book.setId(child.getKey());
                                    libraryBooks.add(book);
                                }
                            }
                        }

                        if (libraryBooks.isEmpty()) {
                            txtEmptyLibrary.setVisibility(View.VISIBLE);
                        } else {
                            txtEmptyLibrary.setVisibility(View.GONE);
                        }

                        libraryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyLibraryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}