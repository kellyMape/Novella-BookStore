package com.example.novella;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPassEditText;
    Button registerbtn, logRegbtn;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailregister);
        passwordEditText = findViewById(R.id.passwordregister);
        confirmPassEditText = findViewById(R.id.confirmpasswordregister);

        registerbtn = findViewById(R.id.registerbtn);
        logRegbtn = findViewById(R.id.loginregbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Go to Login Page
        logRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Register Button
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPassEditText.getText().toString().trim();

        // Validate empty fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)) {

            Toast.makeText(this, "Please fill all the fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            emailEditText.requestFocus();
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            passwordEditText.setError(
                    "Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        // Confirm password
        if (!password.equals(confirmPassword)) {
            confirmPassEditText.setError("Passwords do not match");
            confirmPassEditText.requestFocus();
            return;
        }

        registerbtn.setEnabled(false);

        // Create account in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                saveUserToFirestore(
                                        user.getUid(), user.getEmail());
                            } else {
                                registerbtn.setEnabled(true);
                            }

                        } else {

                            registerbtn.setEnabled(true);

                            Toast.makeText(RegisterActivity.this,
                                    "Register failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Save user information in Cloud Firestore
    private void saveUserToFirestore(String uid, String email) {

        Map<String, Object> user = new HashMap<>();

        user.put("email", email);
        user.put("role", "customer");

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(RegisterActivity.this,
                            "Account created successfully",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(
                            RegisterActivity.this, MainActivity.class);

                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {

                    firebaseAuth.signOut();
                    registerbtn.setEnabled(true);

                    Toast.makeText(RegisterActivity.this,
                            "Account created, but profile could not be saved",
                            Toast.LENGTH_LONG).show();

                });
    }
}