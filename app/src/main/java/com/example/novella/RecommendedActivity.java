package com.example.novella;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Intent;

import java.util.ArrayList;

public class RecommendedActivity extends AppCompatActivity {

    private ListView listViewRecommendedBooks;
    private TextView textViewRecommendedSubtitle;

    private ArrayList<Book> recommendedBooks;
    private BookAdapter bookAdapter;
    private DatabaseReference booksReference;

    // Our 10 popular classic books
    private final String[] popularTitles = {
            "Pride and Prejudice",
            "Dracula",
            "Frankenstein",
            "The Adventures of Sherlock Holmes",
            "The Wonderful Wizard of Oz",
            "Peter Pan",
            "The Time Machine",
            "The War of the Worlds",
            "Treasure Island",
            "Little Women"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended);
        // Return to the previous screen
        findViewById(R.id.btnBackRecommended)
                .setOnClickListener(v -> finish());

        // Connect the views
        listViewRecommendedBooks = findViewById(
                R.id.listViewRecommendedBooks);

        textViewRecommendedSubtitle = findViewById(
                R.id.textViewRecommendedSubtitle);

        // Create the book list and adapter
        recommendedBooks = new ArrayList<>();

        bookAdapter = new BookAdapter(
                this, recommendedBooks);

        listViewRecommendedBooks.setAdapter(bookAdapter);

        // Open selected book details
        listViewRecommendedBooks.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Book book = recommendedBooks.get(position);

                    Intent intent = new Intent(
                            RecommendedActivity.this,
                            BookDetailsActivity.class);

                    // Pass real Firebase book ID
                    intent.putExtra("bookId", book.getId());

                    // Pass book information
                    intent.putExtra("title", book.getTitle());
                    intent.putExtra("author", book.getAuthor());
                    intent.putExtra("genre", book.getGenre());
                    intent.putExtra("description", book.getDescription());
                    intent.putExtra("price", book.getPrice());
                    intent.putExtra("coverUrl", book.getCoverUrl());

                    startActivity(intent);
                });
        // Connect to the shared Firebase database
        booksReference = FirebaseDatabase.getInstance()
                .getReference("books");

        loadRecommendedBooks();
    }

    // Load popular books from Firebase
    private void loadRecommendedBooks() {

        booksReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        recommendedBooks.clear();

                        ArrayList<Book> allBooks = new ArrayList<>();

                        // Get all books from Firebase
                        for (DataSnapshot bookSnapshot
                                : snapshot.getChildren()) {

                            Book book = bookSnapshot.getValue(Book.class);

                            if (book != null) {
                                book.setId(bookSnapshot.getKey());
                                allBooks.add(book);
                            }
                        }

                        // Select the 10 popular classics in order
                        for (String title : popularTitles) {

                            for (Book book : allBooks) {

                                if (book.getTitle() != null
                                        && book.getTitle()
                                        .trim()
                                        .equalsIgnoreCase(title)) {

                                    recommendedBooks.add(book);
                                    break;
                                }
                            }
                        }

                        // Update the list
                        bookAdapter.notifyDataSetChanged();

                        if (recommendedBooks.isEmpty()) {
                            textViewRecommendedSubtitle.setText(
                                    "No popular books available yet");
                        } else {
                            textViewRecommendedSubtitle.setText(
                                    "Popular Classics");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        Toast.makeText(
                                RecommendedActivity.this,
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
