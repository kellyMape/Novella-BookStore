package com.example.novella;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.firebase.storage.UploadTask;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddBookActivity extends AppCompatActivity {

    EditText edtTitle, edtAuthor, edtPrice, edtDescription;
    Spinner spinnerGenre;
    CheckBox checkRecommend, checkAvailable;
    ImageView imgViewCover;
    Button btnSelectCover, btnSaveBook;

    DatabaseReference databaseBooks;
    StorageReference coversReference;

    Uri coverUri;

    String[] genres = {"Romance", "Mystery", "Horror", "Fantasy", "Science Fiction", "Classics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book);

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
        btnSaveBook = findViewById(R.id.btnSaveBook);


        //spiner
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        btnSelectCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCoverLauncher.launch("image/*");
            }
        });

        btnSaveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
            }
        });
    }

    // Opens the gallery to choose the cover
    private final ActivityResultLauncher<String> selectCoverLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    coverUri = uri;
                    imgViewCover.setImageURI(uri);
                }
            });

    private void saveBook() {
        String title = edtTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String strPrice = edtPrice.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();
        boolean recommended = checkRecommend.isChecked();
        boolean available = checkAvailable.isChecked();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(strPrice)
                || TextUtils.isEmpty(description) || coverUri == null) {
            Toast.makeText(this, "Fill all the field", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(strPrice);
        //create my ID by firebase
        String id = databaseBooks.push().getKey();

        if (id == null) return;

        StorageReference fileRef = coversReference.child(id + ".jpg");

        UploadTask uploadTask = fileRef.putFile(coverUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {

            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                String coverUrl = uri.toString();

                Book book = new Book(
                        id, title, author, genre, description, price, coverUrl, recommended, available
                );

                databaseBooks.child(id).setValue(book)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Book saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Download URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });

    }
}