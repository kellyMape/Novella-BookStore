package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    TextView email, showName, pageTitle, temporarySection;

    View homeSection, profileSection, fragmentContainer;

    Button btnHome, btnCatalog, btnWishlist;
    Button btnCart, btnProfile, btnExplore;
    Button signout, deleteAccount;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.showEmail);
        showName = findViewById(R.id.showName);
        pageTitle = findViewById(R.id.pageTitle);

        homeSection = findViewById(R.id.homeSection);
        profileSection = findViewById(R.id.profileSection);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        temporarySection = findViewById(R.id.temporarySection);

        btnHome = findViewById(R.id.btnHome);
        btnCatalog = findViewById(R.id.btnCatalog);
        btnWishlist = findViewById(R.id.btnWishlist);
        btnCart = findViewById(R.id.btnCart);
        btnProfile = findViewById(R.id.btnProfile);
        btnExplore = findViewById(R.id.btnExplore);

        signout = findViewById(R.id.signout);
        deleteAccount = findViewById(R.id.delete);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        email.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null) {
            showName.setText(currentUser.getDisplayName());
        }

        // Home
        btnHome.setOnClickListener(v -> showHome());

        // Book Catalog
        btnCatalog.setOnClickListener(v -> showCatalog());

        // Open Recommended Books screen
        btnExplore.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        RecommendedActivity.class)));

        // Open saved books
        btnWishlist.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        WishlistActivity.class)));


        // Open Shopping Cart
        btnCart.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        CartActivity.class)));


        // Profile
        btnProfile.setOnClickListener(v -> showProfile());

        findViewById(R.id.btnLibrary).setOnClickListener(v ->
                showMessage("My Library - Coming soon"));

        findViewById(R.id.btnPurchaseHistory).setOnClickListener(v ->
                showMessage("Purchase History - Coming soon"));
        // Open Edit Profile screen
        findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class)));



        // Sign out
        signout.setOnClickListener(v -> {

            firebaseAuth.signOut();

            startActivity(new Intent(
                    MainActivity.this, LoginActivity.class));

            finish();
        });

        // Delete account
        deleteAccount.setOnClickListener(v -> {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {

                        currentUser.delete()
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {

                                        startActivity(new Intent(
                                                MainActivity.this,
                                                RegisterActivity.class));

                                        finish();

                                    } else {
                                        showMessage("Unable to delete account");
                                    }
                                });
                    })
                    .show();
        });

        String screen = savedInstanceState == null
                ? "Home"
                : savedInstanceState.getString("screen", "Home");

        switch (screen) {
            case "Book Catalog":
                showCatalog();
                break;
            case "My Profile":
                showProfile();
                break;
            case "My Wishlist":
                showTemporary("My Wishlist", "No saved books yet.");
                break;
            case "Shopping Cart":
                showTemporary("Shopping Cart", "Your cart is empty.");
                break;
            default:
                showHome();
        }
    }

    private void hideSections() {

        homeSection.setVisibility(View.GONE);
        profileSection.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.GONE);
        temporarySection.setVisibility(View.GONE);
    }

    private void showHome() {

        hideSections();

        pageTitle.setText("Novella");
        homeSection.setVisibility(View.VISIBLE);
    }

    private void showCatalog() {

        hideSections();

        pageTitle.setText("Book Catalog");

        fragmentContainer.setVisibility(View.VISIBLE);

        if (getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer) == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer,
                            new CatalogFragment())
                    .commitNow();
        }
    }

    private void showProfile() {

        hideSections();

        pageTitle.setText("My Profile");
        profileSection.setVisibility(View.VISIBLE);
    }

    private void showTemporary(String title, String message) {

        hideSections();

        pageTitle.setText(title);

        temporarySection.setText(message);
        temporarySection.setVisibility(View.VISIBLE);
    }

    private void showMessage(String message) {

        Toast.makeText(this, message,
                Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("screen",
                pageTitle.getText().toString());
        super.onSaveInstanceState(outState);
    }

    // Update profile name
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null && showName != null) {
            String name = user.getDisplayName();

            showName.setText(name == null || name.isEmpty()
                    ? "Customer" : name);
        }
    }
}