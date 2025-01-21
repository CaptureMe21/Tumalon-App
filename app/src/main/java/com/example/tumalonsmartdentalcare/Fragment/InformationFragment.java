package com.example.tumalonsmartdentalcare.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tumalonsmartdentalcare.LoginActivity;
import com.example.tumalonsmartdentalcare.Model.ClientAccount;
import com.example.tumalonsmartdentalcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class InformationFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private EditText lnameInput, fnameInput, middleInput, nicknameInput, occupationInput,
            nationalityInput, religionInput, purokInput, fullnameInput,
            phoneNumInput, addressInput, birthdateInput;

    private Spinner statusSpinner, sexSpinner, provinceSpinner, municipalitySpinner, barangaySpinner;

    private String userId, email, password;

    private Button finishButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public InformationFragment() {
    }

    public static InformationFragment newInstance(String param1, String param2) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        // Initialize UI components
        lnameInput = view.findViewById(R.id.lname_input);
        fnameInput = view.findViewById(R.id.fname_input);
        middleInput = view.findViewById(R.id.middle_input);
        nicknameInput = view.findViewById(R.id.nickname_input);
        occupationInput = view.findViewById(R.id.occupation_input);
        nationalityInput = view.findViewById(R.id.nationality_input);
        religionInput = view.findViewById(R.id.religion_input);
        purokInput = view.findViewById(R.id.purok_input);
        fullnameInput = view.findViewById(R.id.fullname_input);
        phoneNumInput = view.findViewById(R.id.phoneNum_input);
        addressInput = view.findViewById(R.id.address_input);
        birthdateInput = view.findViewById(R.id.birthdate_input);

        birthdateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");
            email = arguments.getString("email");
            password = arguments.getString("password");
        }

        finishButton = view.findViewById(R.id.finish_button);

    // Status spinner
        statusSpinner = view.findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.status_options,
                R.layout.spinner_item
        );
        statusAdapter.setDropDownViewResource(R.layout.spinner_item);

        List<String> statusList = new ArrayList<>();
        statusList.add("Select Status"); // Default selection
        for (int i = 0; i < statusAdapter.getCount(); i++) {
            statusList.add(statusAdapter.getItem(i).toString());
        }
        ArrayAdapter<String> newStatusAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                statusList
        );
        newStatusAdapter.setDropDownViewResource(R.layout.spinner_item);
        statusSpinner.setAdapter(newStatusAdapter);

    //Sex spinner
        sexSpinner = view.findViewById(R.id.sex_spinner);
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sex_options,
                R.layout.spinner_item
        );
        sexAdapter.setDropDownViewResource(R.layout.spinner_item);

        List<String> sexList = new ArrayList<>();
        sexList.add("Select Sex"); // Default selection
        for (int i = 0; i < sexAdapter.getCount(); i++) {
            sexList.add(sexAdapter.getItem(i).toString());
        }
        ArrayAdapter<String> newSexAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                sexList
        );
        newSexAdapter.setDropDownViewResource(R.layout.spinner_item);
        sexSpinner.setAdapter(newSexAdapter);

//Province Spinner
        provinceSpinner = view.findViewById(R.id.province_spinner);
        List<String> provinceList = new ArrayList<>();
        provinceList.add("Select Province"); // Default selection
        provinceList.addAll(Arrays.asList(getResources().getStringArray(R.array.province_options)));

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                provinceList
        );
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        provinceSpinner.setAdapter(provinceAdapter);

//Municipality Spinner
        municipalitySpinner = view.findViewById(R.id.municipality_spinner);
        ArrayAdapter<String> municipalityAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                new ArrayList<>() // Initially empty
        );
        municipalityAdapter.setDropDownViewResource(R.layout.spinner_item);
        municipalitySpinner.setAdapter(municipalityAdapter);

// Province Spinner Default Selection
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = provinceSpinner.getSelectedItem().toString();

                // Update municipality list based on selected province
                List<String> municipalityList = new ArrayList<>();
                municipalityList.add("Select Municipality"); // Default option

                if (selectedProvince.equals("Bohol")) {
                    municipalityList.addAll(Arrays.asList(getResources().getStringArray(R.array.municipality_options)));
                } else {
                    municipalityList.add("No Municipalities Available");
                }

                // Update Municipality spinner adapter
                municipalityAdapter.clear();
                municipalityAdapter.addAll(municipalityList);
                municipalityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

//Barangay Spinner
        barangaySpinner = view.findViewById(R.id.barangay_spinner);
        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                new ArrayList<>()
        );
        barangayAdapter.setDropDownViewResource(R.layout.spinner_item);
        barangaySpinner.setAdapter(barangayAdapter);

        municipalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMunicipality = municipalitySpinner.getSelectedItem().toString();

                // Update barangay list based on selected municipality
                List<String> barangayList = new ArrayList<>();
                barangayList.add("Select Barangay"); // Default option

                if (selectedMunicipality.equals("Alburquerque")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.alburquerque_barangays)));
                } else if (selectedMunicipality.equals("Alicia")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.alicia_barangays)));
                } else if (selectedMunicipality.equals("Anda")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.anda_barangays)));
                } else if (selectedMunicipality.equals("Antequera")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.antequera_barangays)));
                } else if (selectedMunicipality.equals("Baclayon")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.baclayon_barangays)));
                } else if (selectedMunicipality.equals("Balilihan")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.balilihan_barangays)));
                } else if (selectedMunicipality.equals("Batuan")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.batuan_barangays)));
                } else if (selectedMunicipality.equals("Bien Unido")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.bien_unido_barangays)));
                /*} else if (selectedMunicipality.equals("Bilar")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.bilar_barangays)));
                } else if (selectedMunicipality.equals("Buenavista")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.buenavista_barangays)));
                } else if (selectedMunicipality.equals("Calape")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.calape_barangays)));
                } else if (selectedMunicipality.equals("Candijay")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.candijay_barangays)));
                } else if (selectedMunicipality.equals("Carmen")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.carmen_barangays)));
                } else if (selectedMunicipality.equals("Catigbian")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.catigbian_barangays)));
                } else if (selectedMunicipality.equals("Clarin")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.clarin_barangays)));
                } else if (selectedMunicipality.equals("Corella")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.corella_barangays)));
                } else if (selectedMunicipality.equals("Cortes")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.cortes_barangays)));
                } else if (selectedMunicipality.equals("Dagohoy")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.dagohoy_barangays)));
                } else if (selectedMunicipality.equals("Danao")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.danao_barangays)));*/
                } else if (selectedMunicipality.equals("Dauis")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.dauis_barangays)));
                /*} else if (selectedMunicipality.equals("Dimiao")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.dimiao_barangays)));
                } else if (selectedMunicipality.equals("Duero")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.duero_barangays)));
                } else if (selectedMunicipality.equals("Garcia Hernandez")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.garcia_hernandez_barangays)));
                } else if (selectedMunicipality.equals("Guindulman")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.guindulman_barangays)));
                } else if (selectedMunicipality.equals("Inabanga")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.inabanga_barangays)));
                } else if (selectedMunicipality.equals("Jagna")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.jagna_barangays)));
                } else if (selectedMunicipality.equals("Lila")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.lila_barangays)));
                } else if (selectedMunicipality.equals("Loay")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.loay_barangays)));
                } else if (selectedMunicipality.equals("Loboc")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.loboc_barangays)));
                } else if (selectedMunicipality.equals("Loon")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.loon_barangays)));
                } else if (selectedMunicipality.equals("Mabini")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.mabini_barangays)));
                } else if (selectedMunicipality.equals("Maribojoc")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.maribojoc_barangays)));
                } else if (selectedMunicipality.equals("Pilar")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.pilar_barangays)));
                } else if (selectedMunicipality.equals("Pres. Carlos P. Garcia")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.pres_carlos_p_garcia_barangays)));
                } else if (selectedMunicipality.equals("Sagbayan")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.sagbayan_barangays)));
                } else if (selectedMunicipality.equals("San Isidro")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.san_isidro_barangays)));
                } else if (selectedMunicipality.equals("San Miguel")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.san_miguel_barangays)));
                } else if (selectedMunicipality.equals("Sevilla")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.sevilla_barangays)));
                } else if (selectedMunicipality.equals("Sierra Bullones")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.sierra_bullones_barangays)));
                } else if (selectedMunicipality.equals("Sikatuna")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.sikatuna_barangays)));*/
                } else if (selectedMunicipality.equals("Tagbilaran")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.tagbilaran_barangays)));
                /*} else if (selectedMunicipality.equals("Talibon")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.talibon_barangays)));
                } else if (selectedMunicipality.equals("Trinidad")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.trinidad_barangays)));
                } else if (selectedMunicipality.equals("Tubigon")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.tubigon_barangays)));*/
                } else if (selectedMunicipality.equals("Ubay")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.ubay_barangays)));
                /*} else if (selectedMunicipality.equals("Valencia")) {
                    barangayList.addAll(Arrays.asList(getResources().getStringArray(R.array.valencia_barangays)));*/
                } else {
                    barangayList.add("No Barangays Available");
                }

                // Update Barangay spinner adapter
                barangayAdapter.clear();
                barangayAdapter.addAll(barangayList);
                barangayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

        return view;
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Check EditText fields
        if (lnameInput.getText().toString().trim().isEmpty()) {
            lnameInput.setError("Last name is required!");
            isValid = false;
        }
        if (fnameInput.getText().toString().trim().isEmpty()) {
            fnameInput.setError("First name is required!");
            isValid = false;
        }
        if (middleInput.getText().toString().trim().isEmpty()) {
            middleInput.setError("Middle name is required!");
            isValid = false;
        }
        if (nicknameInput.getText().toString().trim().isEmpty()) {
            nicknameInput.setError("Nickname is required!");
            isValid = false;
        }
        if (occupationInput.getText().toString().trim().isEmpty()) {
            occupationInput.setError("Occupation is required!");
            isValid = false;
        }
        if (nationalityInput.getText().toString().trim().isEmpty()) {
            nationalityInput.setError("Nationality is required!");
            isValid = false;
        }
        if (religionInput.getText().toString().trim().isEmpty()) {
            religionInput.setError("Religion is required!");
            isValid = false;
        }
        if (purokInput.getText().toString().trim().isEmpty()) {
            purokInput.setError("Purok is required!");
            isValid = false;
        }
        if (fullnameInput.getText().toString().trim().isEmpty()) {
            fullnameInput.setError("Full name is required!");
            isValid = false;
        }
        if (phoneNumInput.getText().toString().trim().isEmpty()) {
            phoneNumInput.setError("Phone number is required!");
            isValid = false;
        }
        if (addressInput.getText().toString().trim().isEmpty()) {
            addressInput.setError("Address is required!");
            isValid = false;
        }

        // Check Spinner fields
        if (statusSpinner.getSelectedItem().toString().contains("Select")) {
            Toast.makeText(requireContext(), "Please select a valid status", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (sexSpinner.getSelectedItem().toString().contains("Select")) {
            Toast.makeText(requireContext(), "Please select a valid sex", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (provinceSpinner.getSelectedItem().toString().contains("Select")) {
            Toast.makeText(requireContext(), "Please select a valid province", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (municipalitySpinner.getSelectedItem().toString().contains("Select")) {
            Toast.makeText(requireContext(), "Please select a valid municipality", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (barangaySpinner.getSelectedItem().toString().contains("Select")) {
            Toast.makeText(requireContext(), "Please select a valid barangay", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        registerUserInFirebaseAuth(email, password);
        return isValid;
    }

    private void saveClientData() {
        // Get Firebase database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clientAccount");

        // Retrieve the userId passed from SignupActivity
        Bundle arguments = getArguments();
        if (arguments == null) {
            Toast.makeText(getContext(), "User ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = arguments.getString("userId");
        String email = arguments.getString("email");
        String password = arguments.getString("password");

        if (userId == null || email == null ||  password == null) {
            Toast.makeText(getContext(), "Required data is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect data from input fields
        String lastName = lnameInput.getText().toString().trim();
        String firstName = fnameInput.getText().toString().trim();
        String middleName = middleInput.getText().toString().trim();
        String nickname = nicknameInput.getText().toString().trim();
        String dateBirth = birthdateInput.getText().toString().trim();
        String occupation = occupationInput.getText().toString().trim();
        String civilStatus = statusSpinner.getSelectedItem().toString().trim();
        String sex = sexSpinner.getSelectedItem().toString().trim();
        String province = provinceSpinner.getSelectedItem().toString().trim();
        String municipality = municipalitySpinner.getSelectedItem().toString().trim();
        String barangay = barangaySpinner.getSelectedItem().toString().trim();
        String nationality = nationalityInput.getText().toString().trim();
        String religion = religionInput.getText().toString().trim();
        String purok = purokInput.getText().toString().trim();
        String personName = fullnameInput.getText().toString().trim();
        String personNumber = phoneNumInput.getText().toString().trim();
        String personAddress = addressInput.getText().toString().trim();

        // Create a new ClientAccount object
        ClientAccount clientAccount = new ClientAccount(userId, lastName, firstName, middleName, nickname, dateBirth, occupation, civilStatus, sex, province, municipality,
                barangay, nationality, religion, purok, personName, personNumber, personAddress, email);

        // Save data under the clientAccount node with userId as the key
        databaseReference.child(userId).setValue(clientAccount).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Account saved successfully!", Toast.LENGTH_SHORT).show();
                registerUserInFirebaseAuth(email, password);

                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getContext(), "Failed to save account. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format and display the selected date
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    birthdateInput.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void registerUserInFirebaseAuth(String email, String password) {
        // Retrieve userId from Bundle arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId"); // Retrieve the custom userId
        }

        // Register the user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && userId != null) {
                            // Set the custom userId
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userId) // Assign the custom userId
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Optional: Perform additional actions, e.g., save data
                                            saveClientData();
                                            Toast.makeText(getContext(), "User registered successfully with custom userId!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Failed to save custom userId in FirebaseAuth profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "User registration failed: userId is null.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle registration failure
                        Toast.makeText(getContext(), "Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}