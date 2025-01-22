package com.example.tumalonsmartdentalcare;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
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

        // Set a listener to toggle the password visibility and the eye image
        showPasswordToggle.setOnClickListener(v -> {
            if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // Show password as plain text
                passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // Change the eye image to open eye
                showPasswordToggle.setImageResource(R.drawable.show_pass);
            } else {
                // Hide password with password stars
                passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                // Change the eye image to closed eye
                showPasswordToggle.setImageResource(R.drawable.hide_pass);
            }
            // Move the cursor to the end after toggling
            passwordField.setSelection(passwordField.getText().length());
        });


    // Handle login button click
        loginButton.setOnClickListener(v -> {
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

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        // Get the current Firebase user ID (assumed to be the user trying to log in)
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the 'clientAccount' node in your Firebase database
        DatabaseReference clientAccountRef = FirebaseDatabase.getInstance().getReference("clientAccount");

        // Fetch the data for the current userId from Firebase
        clientAccountRef.child(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                // Check if the user data exists for the given userId
                if (dataSnapshot.exists()) {
                    String storedEmail = dataSnapshot.child("email").getValue(String.class);
                    String storedPassword = dataSnapshot.child("password").getValue(String.class);

                    // Compare email and password
                    if (storedEmail != null && storedEmail.equals(email) && storedPassword != null && storedPassword.equals(password)) {
                        // Email and password match, login successful
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                        // Pass the userId to the next activity (e.g., for further usage)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userId", currentUserId);  // Pass the userId to the next activity
                        startActivity(intent);
                        finish();

                    } else {
                        // Email or password does not match
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User data does not exist
                    Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Failed to fetch data from Firebase
                Toast.makeText(LoginActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}