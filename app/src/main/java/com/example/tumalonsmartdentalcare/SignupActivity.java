package com.example.tumalonsmartdentalcare;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.tumalonsmartdentalcare.Fragment.RequestFragment;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Replace with your layout file

        // Initialize fields
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        nextButton = findViewById(R.id.next_button);

        // Set click listener for the button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
            }
        });
    }

    private void validateAndProceed() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        // Check if any field is empty
        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
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

        // All validations passed; proceed to the next fragment
        openRequestFragment();
    }

    private void openRequestFragment() {
        RequestFragment requestFragment = new RequestFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, requestFragment); // Replace the content with the fragment
        transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
        transaction.commit();
    }
}
