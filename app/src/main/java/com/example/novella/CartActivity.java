package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private TextView textViewEmptyCart, textViewCartTotal;
    private Button btnCheckout;

    private ArrayList<Book> cartBooks;
    private BookAdapter bookAdapter;

    private DatabaseReference cartRef;
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Connect views
        listViewCart = findViewById(R.id.listViewCart);
        textViewEmptyCart = findViewById(R.id.textViewEmptyCart);
        textViewCartTotal = findViewById(R.id.textViewCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Back to the previous screen
        findViewById(R.id.btnBackCart)
                .setOnClickListener(v -> finish());

        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance()
                .getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please log in first",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create book list and adapter
        cartBooks = new ArrayList<>();
        bookAdapter = new BookAdapter(this, cartBooks);
        listViewCart.setAdapter(bookAdapter);

        // Connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        cartRef = database.getReference("carts")
                .child(user.getUid());

        booksRef = database.getReference("books");

        // Open book details when selected
        listViewCart.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Book book = cartBooks.get(position);

                    Intent intent = new Intent(
                            CartActivity.this,
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

        // Long press a book to remove it
        listViewCart.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    Book book = cartBooks.get(position);

                    new AlertDialog.Builder(CartActivity.this)
                            .setTitle("Remove Book")
                            .setMessage("Remove " + book.getTitle()
                                    + " from your cart?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Remove", (dialog, which) ->
                                    removeBook(book.getId()))
                            .show();

                    return true;
                });

        // Checkout will be connected to Stripe later
        btnCheckout.setOnClickListener(v ->
                Toast.makeText(this,
                        "Checkout coming soon!",
                        Toast.LENGTH_SHORT).show());

        btnCheckout.setEnabled(false);

        loadCart();
    }

    // Load books saved in the user's cart
    private void loadCart() {

        cartRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot cartSnapshot) {

                booksRef.get().addOnSuccessListener(booksSnapshot -> {

                    cartBooks.clear();
                    double total = 0;

                    // Match cart IDs with books in Firebase
                    for (DataSnapshot cartItem : cartSnapshot.getChildren()) {

                        String bookId = cartItem.getKey();

                        if (bookId == null) {
                            continue;
                        }

                        Book book = booksSnapshot.child(bookId)
                                .getValue(Book.class);

                        if (book != null) {
                            book.setId(bookId);
                            cartBooks.add(book);
                            total += book.getPrice();
                        }
                    }

                    // Update the book list
                    bookAdapter.notifyDataSetChanged();

                    // Show empty message if needed
                    textViewEmptyCart.setVisibility(
                            cartBooks.isEmpty() ? View.VISIBLE : View.GONE);

                    // Display the total
                    textViewCartTotal.setText(String.format(
                            Locale.US, "Total: $ %.2f", total));

                    // Enable checkout only with books
                    btnCheckout.setEnabled(!cartBooks.isEmpty());

                }).addOnFailureListener(e ->
                        Toast.makeText(CartActivity.this,
                                "Error loading books: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CartActivity.this,
                        "Error loading cart: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Remove selected book from Firebase cart
    private void removeBook(String bookId) {

        if (bookId == null) {
            return;
        }

        cartRef.child(bookId).removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                "Book removed from cart",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}
