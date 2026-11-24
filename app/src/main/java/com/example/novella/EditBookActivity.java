package com.example.novella;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.net.Uri;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.BitSet;

public class EditBookActivity extends AppCompatActivity {

    EditText edtTitle, edtAuthor, edtPrice, edtDescription;
    Spinner spinnerGenre;
    CheckBox checkRecommend, checkAvailable;
    ImageView imgViewCover;
    Button btnSelectCover, btnUpdateBook;

    DatabaseReference databaseBooks;
    StorageReference coversReference;

    Uri selectedImageUri;
    String bookId, oldCoverUrl;

    String[] genres = {"Romance", "Mystery", "Horror", "Fantasy", "Science Fiction", "Classics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_book);

        databaseBooks = FirebaseDatabase.getInstance().getReference("books");
        coversReference = FirebaseStorage.getInstance().getReference("book-covers");

        edtTitle = findViewById(R.id.edtTitle);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        checkRecommend = findViewById(R.id.checkRecommend);
        checkAvailable = findViewById(R.id.checkAvailable);
        imgViewCover = findViewById(R.id.imgViewCover);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnUpdateBook = findViewById(R.id.btnUpdateBook);

        //spiner
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        //get book from the list
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        edtTitle.setText(intent.getStringExtra("bookTitle"));
        edtAuthor.setText(intent.getStringExtra("bookAuthor"));
        edtDescription.setText(intent.getStringExtra("booDescription"));
        edtPrice.setText(String.valueOf(intent.getDoubleExtra("bookPrice", 0.0)));
        checkRecommend.setChecked(intent.getBooleanExtra("bookRecommended", false));
        checkAvailable.setChecked(intent.getBooleanExtra("bookAvailable", true));

        String genre = intent.getStringExtra("bookGenre");
        for (int i = 0; i < genres.length; i++) {
            if (genres[i].equals(genre)) {
                spinnerGenre.setSelection(i);
            }
        }

        oldCoverUrl = intent.getStringExtra("bookCoverUrl");
        if (oldCoverUrl != null && !oldCoverUrl.isEmpty())
        {
            new ImageLoadTask(oldCoverUrl, imgViewCover).execute();
        }

        btnSelectCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCoverLauncher.launch("image/*");
            }
        });

        btnUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBook();
            }
        });
    }

    private final ActivityResultLauncher<String> selectCoverLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgViewCover.setImageURI(uri);
                }
            });

    private void updateBook() {
        String title = edtTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String strPrice = edtPrice.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();
        boolean recommended = checkRecommend.isChecked();
        boolean available = checkAvailable.isChecked();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author)|| TextUtils.isEmpty(strPrice) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Fill all the field", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(strPrice);

        if (selectedImageUri != null) {
            StorageReference fileRef = coversReference.child (bookId + ".jpg");
            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                updateBookInDatabase(title, author, genre, description, price, uri.toString(), recommended, available);
                            }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(),Toast.LENGTH_SHORT).show());
        } else {
            updateBookInDatabase (title, author, genre, description, price, oldCoverUrl, recommended, available);
        }
    }

    private void updateBookInDatabase(String title, String author, String genre, String description, double price, String coverUrl, boolean recommended, boolean available) {
        Book updateBook = new Book(bookId, title, author, genre, description, price, coverUrl, recommended, available);

        databaseBooks.child(bookId).setValue(updateBook)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->  Toast.makeText(this, "Update Failed: " + e.getMessage(),Toast.LENGTH_LONG).show());
    }

    private static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private final String url;
        private final ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void...voids) {
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
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) imageView.setImageBitmap(bitmap);
        }
    }
}