package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is authenticated
        if (!isUserAuthenticated()) {
            navigateToLogin(); // If not authenticated, navigate to TumalonLanding
            return;
        }

        replaceFragment(new HomeFragment());

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
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Pass the user ID to the ServiceList activity
                Intent intent = new Intent(this, ServiceList.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

                Toast.makeText(this, "UserId: " + userId, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private boolean isUserAuthenticated() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null; // Only checks if the user is logged in
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, TumalonLanding.class);
        startActivity(intent);
        finish(); // Close the current activity to prevent coming back
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}