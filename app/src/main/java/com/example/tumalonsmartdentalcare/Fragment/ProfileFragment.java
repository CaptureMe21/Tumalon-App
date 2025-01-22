package com.example.tumalonsmartdentalcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tumalonsmartdentalcare.LoginActivity;
import com.example.tumalonsmartdentalcare.Model.ClientAccount;
import com.example.tumalonsmartdentalcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView profileNameText, nicknameText, birthDateText, civilStatusText, sexText,
            occupationText, religionText, nationalityText, addressText,
            personNameText, personNumberText, personAddressText;

    private String userId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImageView logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize TextViews
        profileNameText = view.findViewById(R.id.profileName_text);
        nicknameText = view.findViewById(R.id.nickname_text);
        birthDateText = view.findViewById(R.id.birthDate_text);
        civilStatusText = view.findViewById(R.id.civilStatus_text);
        sexText = view.findViewById(R.id.sex_text);
        occupationText = view.findViewById(R.id.occupation_text);
        religionText = view.findViewById(R.id.religion_text);
        nationalityText = view.findViewById(R.id.nationality_text);
        addressText = view.findViewById(R.id.address_text);
        personNameText = view.findViewById(R.id.personName_text);
        personNumberText = view.findViewById(R.id.personNumber_text);
        personAddressText = view.findViewById(R.id.personAddress_text);


        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");
        }

        // Initialize the Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("clientAccount").child(userId);

        // Fetch and populate user data
        if (userId != null) {
            fetchUserData(userId);
        }

        // Find the logout button
        logoutBtn = view.findViewById(R.id.logout_icon);

        // Set onClickListener to handle logout
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    // Function to log out the user
    private void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the current user's UID
            String userId = currentUser.getUid();

            // Reference to the user's data in the database

            // Update the 'isAuthenticated' field to false before logging out
            databaseReference.child("isAuthenticated").setValue(false).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If the database update is successful, log the user out
                    firebaseAuth.signOut();

                    // Navigate back to LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                    startActivity(intent);

                    // Optional: finish the current activity/fragment to prevent navigation back
                    getActivity().finish();
                } else {
                    // Handle failure in updating isAuthenticated status
                    Log.e("Logout", "Failed to update authentication status", task.getException());
                    Toast.makeText(getActivity(), "Failed to log out. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle case when no user is logged in
            Toast.makeText(getActivity(), "No user is currently logged in.", Toast.LENGTH_SHORT).show();
        }
    }


    // Function to fetch the user data from Firebase
    private void fetchUserData(String userId) {
        // Correctly reference the clientAccount node using the userId
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the data as a ClientAccount object
                    ClientAccount client = snapshot.getValue(ClientAccount.class);

                    if (client != null) {
                        populateData(client);  // Populate the fragment with the fetched data
                    }
                } else {
                    // Handle case where user data doesn't exist
                    Toast.makeText(getActivity(), "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
                Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Function to populate the fragment with user data
    private void populateData(ClientAccount client) {
        // Concatenate full name
        String fullName = client.getFirstName() + " " + client.getMiddleName() + " " + client.getLastName();
        profileNameText.setText(fullName);

        // Set other fields
        nicknameText.setText(client.getNickname());
        birthDateText.setText(client.getDateBirth());
        civilStatusText.setText(client.getCivilStatus());
        sexText.setText(client.getSex());
        occupationText.setText(client.getOccupation());
        religionText.setText(client.getReligion());
        nationalityText.setText(client.getNationality());

        // Concatenate full address
        String fullAddress = client.getPurok() + ", " + client.getBarangay() + ", " + client.getMunicipality() + ", " + client.getProvince();
        addressText.setText(fullAddress);

        // Contact person information
        personNameText.setText(client.getPersonName());
        personNumberText.setText(client.getPersonNumber());
        personAddressText.setText(client.getPersonAddress());
    }
}