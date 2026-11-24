package com.example.novella;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText;
    EditText currentPassword, newPassword, confirmPassword;
    Button saveBtn, cancelBtn;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Connect the XML fields
        nameEditText = findViewById(R.id.editProfileName);
        emailEditText = findViewById(R.id.editProfileEmail);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmNewPassword);

        saveBtn = findViewById(R.id.btnSaveProfile);
        cancelBtn = findViewById(R.id.btnCancelProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        // Display current user information
        nameEditText.setText(currentUser.getDisplayName());
        emailEditText.setText(currentUser.getEmail());

        saveBtn.setOnClickListener(v -> saveProfile());

        cancelBtn.setOnClickListener(v -> finish());
    }

    private void saveProfile() {

        String name = nameEditText.getText().toString().trim();
        String oldPassword = currentPassword.getText().toString();
        String password = newPassword.getText().toString();
        String confirm = confirmPassword.getText().toString();

        // Validate full name
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Please enter your name");
            return;
        }

        // Check if the user wants to change the password
        boolean changePassword = !oldPassword.isEmpty()
                || !password.isEmpty()
                || !confirm.isEmpty();

        if (changePassword) {

            if (oldPassword.isEmpty() || password.isEmpty()
                    || confirm.isEmpty()) {
                Toast.makeText(this,
                        "Please complete all password fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                newPassword.setError("At least 6 characters");
                return;
            }

            if (!password.equals(confirm)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

            // Verify current password before updating
            if (currentUser.getEmail() == null) {
                Toast.makeText(this,
                        "Email account required",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential =
                    EmailAuthProvider.getCredential(
                            currentUser.getEmail(), oldPassword);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // Update password
                            currentUser.updatePassword(password)
                                    .addOnCompleteListener(passwordTask -> {

                                        if (passwordTask.isSuccessful()) {
                                            updateName(name);
                                        } else {
                                            Toast.makeText(this,
                                                    "Unable to change password",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(this,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            // Update only the name
            updateName(name);
        }
    }

    private void updateName(String name) {

        // Save the new name in Firebase Authentication
        UserProfileChangeRequest profileUpdates =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Unable to update profile",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
