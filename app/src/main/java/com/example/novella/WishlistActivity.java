package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {

    private ListView listViewWishlist;
    private TextView textViewEmptyWishlist;

    private ArrayList<Book> wishlistBooks;
    private BookAdapter bookAdapter;

    private DatabaseReference favoritesRef;
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Connect views
        listViewWishlist = findViewById(R.id.listViewWishlist);
        textViewEmptyWishlist = findViewById(R.id.textViewEmptyWishlist);

        // Return to previous screen
        findViewById(R.id.btnBackWishlist)
                .setOnClickListener(v -> finish());

        // Get logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please log in first",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create list and adapter
        wishlistBooks = new ArrayList<>();
        bookAdapter = new BookAdapter(this, wishlistBooks);
        listViewWishlist.setAdapter(bookAdapter);

        // Connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        favoritesRef = database.getReference("favorites")
                .child(user.getUid());

        booksRef = database.getReference("books");

        // Open selected book details
        listViewWishlist.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Book book = wishlistBooks.get(position);

                    Intent intent = new Intent(
                            WishlistActivity.this,
                            BookDetailsActivity.class);

                    intent.putExtra("bookId", book.getId());
                    intent.putExtra("title", book.getTitle());
                    intent.putExtra("author", book.getAuthor());
                    intent.putExtra("genre", book.getGenre());
                    intent.putExtra("description", book.getDescription());
                    intent.putExtra("price", book.getPrice());
                    intent.putExtra("coverUrl", book.getCoverUrl());

                    startActivity(intent);
                });

        // Long press to remove a book
        listViewWishlist.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    Book book = wishlistBooks.get(position);

                    new AlertDialog.Builder(WishlistActivity.this)
                            .setTitle("Remove Book")
                            .setMessage("Remove " + book.getTitle() + "?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Remove",
                                    (dialog, which) -> removeBook(book.getId()))
                            .show();

                    return true;
                });


        loadWishlist();
    }

    // Load the user's saved books
    private void loadWishlist() {

        favoritesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot favoritesSnapshot) {

                booksRef.get().addOnSuccessListener(booksSnapshot -> {

                    wishlistBooks.clear();

                    // Find each saved book in the book catalog
                    for (DataSnapshot favorite : favoritesSnapshot.getChildren()) {

                        String bookId = favorite.getKey();

                        if (bookId == null) {
                            continue;
                        }

                        DataSnapshot bookSnapshot = booksSnapshot.child(bookId);

                        Book book = bookSnapshot.getValue(Book.class);

                        if (book != null) {
                            book.setId(bookId);
                            wishlistBooks.add(book);
                        }
                    }

                    // Update saved books list
                    bookAdapter.notifyDataSetChanged();

                    textViewEmptyWishlist.setVisibility(
                            wishlistBooks.isEmpty() ? View.VISIBLE : View.GONE);

                }).addOnFailureListener(e ->
                        Toast.makeText(WishlistActivity.this,
                                "Error loading books: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(WishlistActivity.this,
                        "Error loading wishlist: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Remove selected book from Firebase wishlist
    private void removeBook(String bookId) {

        if (bookId == null) {
            return;
        }

        favoritesRef.child(bookId).removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                "Book removed from wishlist",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}
