package com.example.novella;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageBooksActivity extends AppCompatActivity {

    ListView listViewBooks;
    DatabaseReference databaseBooks;
    ArrayList<Book> booksList;
    ManageBookAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_books);

        listViewBooks = findViewById(R.id.lstBooks);
        databaseBooks = FirebaseDatabase.getInstance().getReference("books");
        booksList = new ArrayList<>();
        adapter = new ManageBookAdapter(this, booksList);
        listViewBooks.setAdapter(adapter);

        databaseBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksList.clear();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    Book book = postSnapShot.getValue(Book.class);
                    if (book != null) {
                        booksList.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageBooksActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }
}