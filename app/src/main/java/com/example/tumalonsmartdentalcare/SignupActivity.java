package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.tumalonsmartdentalcare.Fragment.InformationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;  // Declare FirebaseAuth globally

    private EditText emailField, passwordField, confirmPasswordField;
    private Button nextButton;
    private String phone;
    private ImageView showPasswordToggle, showPasswordToggle1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Replace with your layout file

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize fields
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        nextButton = findViewById(R.id.next_button);
        showPasswordToggle = findViewById(R.id.showPasswordToggle);
        showPasswordToggle1 = findViewById(R.id.showPasswordToggle1);

            try {
                Intent intent = getIntent();
                phone = intent.getStringExtra("phone");

                if (phone == null) {
                    Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            } catch (Exception e) {
                Log.e("SignupActivity", "Error retrieving user ID", e);
                Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
                finish();
            }


        // Set click listener for the button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
            }
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

        showPasswordToggle1.setOnClickListener(v -> {
            // Check the current transformation method to toggle visibility
            if (confirmPasswordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // Show password as plain text
                confirmPasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordToggle1.setImageResource(R.drawable.show_pass); // Open eye image
            } else {
                // Hide password with password stars
                confirmPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordToggle1.setImageResource(R.drawable.hide_pass); // Closed eye image
            }
            // Move the cursor to the end after toggling
            confirmPasswordField.setSelection(confirmPasswordField.getText().length());
        });

    }

    private void validateAndProceed() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return;
        }
        if (!isPasswordValid(password)) {
            passwordField.setError("Password must be at least 6 characters long");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordField.setError("Confirm Password is required");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            return;
        }
        checkIfEmailIsTaken(email);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void openRequestFragment(  String phone, String email, String password) {
        // Create a bundle and add data
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);
        bundle.putString("email", email);
        bundle.putString("password", password);

        // Create and set arguments for the fragment
        InformationFragment fragment = new InformationFragment();
        fragment.setArguments(bundle);

        // Perform the fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, fragment); // Replace the content with the fragment
        transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
        transaction.commit();
    }

    private void checkIfEmailIsTaken(String email) {
        DatabaseReference clientAccountRef = FirebaseDatabase.getInstance().getReference("clientAccount");
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference("userAccount");

        // Capture password from input field
        String password = passwordField.getText().toString().trim(); // Assuming passwordField is an EditText

        // Check if the email exists in clientAccount (for each userId)
        Query clientAccountQuery = clientAccountRef.orderByChild("email").equalTo(email);
        clientAccountQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists in clientAccount
                    Toast.makeText(SignupActivity.this, "Email already taken. Please use a different email.", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the email exists in userAccount (for each dentistId)
                    Query userAccountQuery = userAccountRef.orderByChild("email").equalTo(email);
                    userAccountQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Email already exists in userAccount
                                Toast.makeText(SignupActivity.this, "Email already taken. Please use a different email.", Toast.LENGTH_SHORT).show();
                            } else {
                                openRequestFragment(phone, email, password);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle any errors that occur during the query
                            Toast.makeText(SignupActivity.this, "Error checking email in userAccount: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the query
                Toast.makeText(SignupActivity.this, "Error checking email in clientAccount: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
