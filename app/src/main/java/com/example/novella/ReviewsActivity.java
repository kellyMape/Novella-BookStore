package com.example.novella;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ReviewsActivity extends AppCompatActivity {

    TextView txtReviewBookTitle, txtAverage;
    Spinner spinnerRating;
    EditText edtComment;
    Button btnSubmitReview;
    ListView listViewReviews;

    DatabaseReference reviewsReference;
    ArrayList<String> reviewsList;
    ArrayAdapter<String> reviewsAdapter;

    String bookId, bookTitle;
    String[] ratings = {"5 ★★★★★", "4 ★★★★", "3 ★★★", "2 ★★", "1 ★"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        txtReviewBookTitle = findViewById(R.id.txtReviewBookTitle);
        txtAverage = findViewById(R.id.txtAverage);
        spinnerRating = findViewById(R.id.spinnerRating);
        edtComment = findViewById(R.id.edtComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        listViewReviews = findViewById(R.id.listViewReviews);

        //get book
        bookId = getIntent().getStringExtra("bookId");
        bookTitle = getIntent().getStringExtra("bookTitle");

        // TEMPORARY: only for testing (Alice)
        if (bookId == null) {
            bookId = "-P756oMm34Y0sbEJmD4_";
            bookTitle = "Alice's Adventures in Wonderland";
        }

        txtReviewBookTitle.setText(bookTitle);

        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ratings);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(ratingAdapter);

        //list de review
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, reviewsList);
        listViewReviews.setAdapter(reviewsAdapter);

        reviewsReference = FirebaseDatabase.getInstance()
                .getReference("reviews").child(bookId);

        loadReviews();

        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReview();
            }
        });
    }

    // all reviews
    private void loadReviews() {
        reviewsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewsList.clear();
                double total = 0;
                int count = 0;

                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);

                    if (review != null) {
                        // Make the stars (example: 4 -> ★★★★)
                        String stars = "";
                        for (int i = 0; i < review.getRating(); i++) {
                            stars = stars + "★";
                        }

                        reviewsList.add(review.getUserName() + "   " + stars
                                + "\n" + review.getComment());

                        total = total + review.getRating();
                        count++;
                    }
                }

                if (count > 0) {
                    txtAverage.setText(String.format(Locale.US,
                            "★ %.1f / 5 (%d reviews)", total / count, count));
                } else {
                    txtAverage.setText("No reviews yet");
                }

                reviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReviewsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Save the review
    private void saveReview() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please log in to write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = edtComment.getText().toString().trim();

        if (TextUtils.isEmpty(comment)) {
            edtComment.setError("Please write a comment");
            edtComment.requestFocus();
            return;
        }

        int rating = 5 - spinnerRating.getSelectedItemPosition();

        String userName = user.getDisplayName();
        if (userName == null || userName.isEmpty()) {
            userName = "Customer";
        }

        long timestamp = System.currentTimeMillis();

        Review review = new Review(rating, comment, userName, timestamp);

        reviewsReference.child(user.getUid()).setValue(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ReviewsActivity.this, "Review saved", Toast.LENGTH_SHORT).show();
                        edtComment.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}