package com.example.novella;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReadBookActivity extends AppCompatActivity {

    TextView txtReadTitle, txtReadAuthor, txtReadContent;
    Button btnOpenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        txtReadTitle = findViewById(R.id.txtReadTitle);
        txtReadAuthor = findViewById(R.id.txtReadAuthor);
        txtReadContent = findViewById(R.id.txtReadContent);
        btnOpenFile = findViewById(R.id.btnOpenFile);

        findViewById(R.id.btnBackRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get the book from the Intent
        String title = getIntent().getStringExtra("bookTitle");
        String author = getIntent().getStringExtra("bookAuthor");
        String description = getIntent().getStringExtra("bookDescription");
        String fileUrl = getIntent().getStringExtra("bookFileUrl");

        txtReadTitle.setText(title);
        txtReadAuthor.setText(author);
        txtReadContent.setText(description);

        // If the book has a PDF, show the button to open it
        if (fileUrl != null && !fileUrl.isEmpty()) {
            btnOpenFile.setVisibility(View.VISIBLE);
            btnOpenFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    startActivity(intent);
                }
            });
        }
    }
}