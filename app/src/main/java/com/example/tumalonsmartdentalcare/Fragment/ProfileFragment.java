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

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImageView logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("clientAccount");

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

        // Find the logout button
        logoutBtn = view.findViewById(R.id.logout_icon);

        // Set onClickListener to handle logout
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        checkAuthentication();

        return view;
    }

    // Function to log out the user
    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Set authentication to false (sign out the user)

        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(),  LoginActivity.class); // Get activity context
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);

        // Optional: if you need to close the fragment or activity, you can call:
        getActivity().finish(); // Finish the activity to prevent navigating back to this fragment
    }


    private void checkAuthentication() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("ProfileFragment", "Authenticated userId: " + userId);

            // Fetch and populate data
            fetchUserData(userId);
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserData(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ClientAccount client = snapshot.getValue(ClientAccount.class);

                    if (client != null) {
                        populateData(client);
                    }
                } else {
                    Log.d("ProfileFragment", "User data not found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Database error: " + error.getMessage());
            }
        });
    }

    private void populateData(ClientAccount client) {
        // Concatenate full name
        String fullName = client.getFirstName() + " " + client.getMiddleName() + " " + client.getLastName();
        profileNameText.setText(fullName);

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

        personNameText.setText(client.getPersonName());
        personNumberText.setText(client.getPersonNumber());
        personAddressText.setText(client.getPersonAddress());
    }
}
