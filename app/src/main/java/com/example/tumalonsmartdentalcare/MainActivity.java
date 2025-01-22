package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tumalonsmartdentalcare.Fragment.HomeFragment;
import com.example.tumalonsmartdentalcare.Fragment.MessagesFragment;
import com.example.tumalonsmartdentalcare.Fragment.NotificationFragment;
import com.example.tumalonsmartdentalcare.Fragment.ProfileFragment;
import com.example.tumalonsmartdentalcare.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Check user authentication when the app starts
        checkUserAuthentication();

        // Set default fragment to HomeFragment
        replaceFragment(new HomeFragment());

        // Set up bottom navigation
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.message) {
                replaceFragment(new MessagesFragment());
            } else if (itemId == R.id.notif) {
                replaceFragment(new NotificationFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.list) {
                // Pass the currentUserId to the ServiceList activity
                Intent serviceListIntent = new Intent(this, ServiceList.class);
                serviceListIntent.putExtra("userId", userId);
                startActivity(serviceListIntent);
            }
            return true;
        });
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in
            userId = currentUser.getUid();
            Log.d("AuthCheck", "User is logged in with UID: " + userId);
            validateAuthenticationStatus(userId);
        } else {
            // User is not logged in; redirect to login screen
            Log.d("AuthCheck", "No user is logged in.");
            redirectToLogin();
        }
    }

    private void validateAuthenticationStatus(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("clientAccount")
                .child(userId);

        userRef.child("isAuthenticated").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Boolean isAuthenticated = task.getResult().getValue(Boolean.class);
                Log.d("AuthCheck", "isAuthenticated: " + isAuthenticated);

                if (isAuthenticated != null && isAuthenticated) {
                    // User is authenticated, allow access
                } else {
                    // User is not authenticated, log out
                    Log.d("AuthCheck", "User is not authenticated, logging out.");
                    logOutUser(userRef);
                }
            } else {
                // Handle database error
                Toast.makeText(this, "Error checking authentication: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });
    }

    private void logOutUser(DatabaseReference userRef) {
        userRef.child("isAuthenticated").setValue(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseAuth.signOut();
                Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            } else {
                Toast.makeText(this, "Failed to update authentication status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, TumalonLanding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void replaceFragment(Fragment fragment) {
        // Create a Bundle to hold the userId
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        // Set the arguments for the fragment
        fragment.setArguments(bundle);

        // Replace the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
