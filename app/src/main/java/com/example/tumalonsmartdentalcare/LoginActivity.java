package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView signupBtn;
    private ImageView showPasswordToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        loginButton = findViewById(R.id.login_button);
        signupBtn = findViewById(R.id.signup);
        showPasswordToggle = findViewById(R.id.passwordToggle);

        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RequestActivity.class);
            startActivity(intent);
        });

        // Handle login button click
        loginButton.setOnClickListener(v -> {

            validateField();

        });

        showPasswordToggle.setOnClickListener(v -> {
            // Check the current transformation method to toggle visibility
            if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // Show password as plain text
                passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordToggle.setImageResource(R.drawable.show_pass); // Open eye image
            } else {
                // Hide password with password stars
                passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordToggle.setImageResource(R.drawable.hide_pass); // Closed eye image
            }
            // Move the cursor to the end after toggling
            passwordField.setSelection(passwordField.getText().length());
        });
    }


    private void validateField(){
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return;
        }

        loginUser();
    }

    private void loginUser() {
        // Get email and password from input fields
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Check if the email exists in Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully logged in
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Get the user ID from Firebase Authentication
                        String userId = firebaseAuth.getCurrentUser().getUid();

                        // Update the 'isAuthenticated' field to true in Firebase Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clientAccount").child(userId);
                        userRef.child("isAuthenticated").setValue(true)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        // Successfully updated authentication status
                                        // Redirect to MainActivity or any other activity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("currentUser", userId);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Handle error while updating 'isAuthenticated'
                                        Toast.makeText(LoginActivity.this, "Failed to update authentication status.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Handle errors
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed. Please try again.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}