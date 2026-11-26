package com.example.novella;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookDetailsActivity extends AppCompatActivity {

    // Book details
    ImageView imageViewBookCoverDetail;
    TextView textViewDetailTitle, textViewDetailAuthor,
            textViewDetailGenre, textViewDetailPrice,
            textViewDetailDescription;

    Button btnSaveBook, btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Back to catalog
        findViewById(R.id.btnBackDetails).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        // Connect views
        imageViewBookCoverDetail = findViewById(R.id.imageViewBookCoverDetail);
        textViewDetailTitle = findViewById(R.id.textViewDetailTitle);
        textViewDetailAuthor = findViewById(R.id.textViewDetailAuthor);
        textViewDetailGenre = findViewById(R.id.textViewDetailGenre);
        textViewDetailPrice = findViewById(R.id.textViewDetailPrice);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);

        btnSaveBook = findViewById(R.id.btnSaveBook);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Get selected book information
        // Get the book ID from Firebase
        String bookId = getIntent().getStringExtra("bookId");
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String genre = getIntent().getStringExtra("genre");
        String description = getIntent().getStringExtra("description");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String coverUrl = getIntent().getStringExtra("coverUrl");

        // Display book details
        textViewDetailTitle.setText(title);
        textViewDetailAuthor.setText(author);
        textViewDetailGenre.setText(genre);
        textViewDetailDescription.setText(description);
        textViewDetailPrice.setText(String.format(Locale.US, "$ %.2f", price));

        // Load book cover
        if (coverUrl != null && !coverUrl.isEmpty()) {
            new ImageLoadTask(coverUrl, imageViewBookCoverDetail).execute();
        } else {
            imageViewBookCoverDetail.setImageResource(android.R.drawable.ic_menu_report_image);
        }


        // Save book to Wishlist
        btnSaveBook.setOnClickListener(v -> {

            FirebaseUser user = FirebaseAuth.getInstance()
                    .getCurrentUser();

            if (user == null) {
                Toast.makeText(this, "Please log in first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (bookId == null || bookId.isEmpty()) {
                Toast.makeText(this, "Book ID not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Save book ID in user's favorites
            DatabaseReference favoriteRef =
                    FirebaseDatabase.getInstance()
                            .getReference("favorites")
                            .child(user.getUid())
                            .child(bookId);

            favoriteRef.setValue(true)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this,
                                    "Added to Wishlist!",
                                    Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
        });


        // Add book to Shopping Cart
        btnAddToCart.setOnClickListener(v -> {

            FirebaseUser user = FirebaseAuth.getInstance()
                    .getCurrentUser();

            if (user == null) {
                Toast.makeText(this, "Please log in first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (bookId == null || bookId.isEmpty()) {
                Toast.makeText(this, "Book ID not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Save book in user's cart
            DatabaseReference cartRef =
                    FirebaseDatabase.getInstance()
                            .getReference("carts")
                            .child(user.getUid())
                            .child(bookId);

            cartRef.child("quantity").setValue(1)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this,
                                    "Added to Cart!",
                                    Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
        });

        // Open Ratings & Comments
        Button btnRatings = findViewById(R.id.btnRatings);
        btnRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this, ReviewsActivity.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("bookTitle", title);
                startActivity(intent);
            }
        });
    }

    // Download book cover image
    private static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private final String url;
        private final ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        // Download image in background
        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                return null;
            }
        }

        // Display downloaded image
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
    }
}
