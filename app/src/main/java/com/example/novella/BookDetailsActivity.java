package com.example.novella;

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

        // Wishlist button (temporary)
        btnSaveBook.setOnClickListener(v ->
                Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()
        );

        // Add to cart button (temporary)
        btnAddToCart.setOnClickListener(v ->
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
        );
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
