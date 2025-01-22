package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tumalonsmartdentalcare.Model.PhoneModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText otp_1, otp_2, otp_3, otp_4, otp_5, otp_6;
    private Button nextBtn;
    private TextView resendOtpTextView;
    private String phoneNumber, verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);

        // Initialize Firebase Auth once
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupOtpInput();

        // Get phone number from intent with validation
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("phone")) {
            phoneNumber = extras.getString("phone");
            sendOtp(phoneNumber, false);
        } else {
            Toast.makeText(this, "Phone number not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next button click listener
        nextBtn.setOnClickListener(v -> {
            String enteredOtp = getEnteredOtp();
            if (enteredOtp.length() == 6 && verificationCode != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
            } else {
                Toast.makeText(this, "Please enter a valid 6-digit OTP.", Toast.LENGTH_SHORT).show();
            }
        });

        // Resend OTP click listener
        resendOtpTextView.setOnClickListener(v -> sendOtp(phoneNumber, true));
    }

    // Function to send OTP
    private void sendOtp(String phone, boolean isResend) {
        PhoneAuthOptions.Builder optionsBuilder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signIn(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        String errorMessage = "Verification failed: " +
                                (e.getMessage() != null ? e.getMessage() : "Unknown error");
                        Toast.makeText(VerifyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        verificationCode = verificationId;
                        resendingToken = token;
                        Toast.makeText(VerifyActivity.this,
                                "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });

        if (isResend) {
            optionsBuilder.setForceResendingToken(resendingToken);
        }

        PhoneAuthOptions options = optionsBuilder.build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Function to set up OTP input navigation
    private void setupOtpInput() {
        EditText[] otpInputs = {otp_1, otp_2, otp_3, otp_4, otp_5, otp_6};

        for (int i = 0; i < otpInputs.length; i++) {
            final int currentIndex = i;

            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpInputs.length - 1) {
                        // Move to the next field when input is added
                        otpInputs[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (otpInputs[currentIndex].getText().toString().isEmpty() && currentIndex > 0) {
                        // Move to the previous field when backspace is pressed on an empty field
                        otpInputs[currentIndex - 1].requestFocus();
                    }
                }
                return false;
            });
        }
    }

    // Function to get entered OTP with validation
    private String getEnteredOtp() {
        StringBuilder otp = new StringBuilder();
        EditText[] otpInputs = {otp_1, otp_2, otp_3, otp_4, otp_5, otp_6};

        for (EditText input : otpInputs) {
            String digit = input.getText().toString().trim();
            if (digit.isEmpty()) {
                return "";
            }
            otp.append(digit);
        }
        return otp.toString();
    }

    private void signIn(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            // Get user details
                            String uid = user.getUid();
                            String identifier = user.getPhoneNumber();

                            // Proceed to next activity with UID
                            Intent intent = new Intent(VerifyActivity.this, SignupActivity.class);
                            intent.putExtra("phone", identifier); 
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Authentication failed";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("VerifyActivity", "Sign in failed", task.getException());
                    }
                });
    }

    private void initializeViews() {
        otp_1 = findViewById(R.id.otp_input_1);
        otp_2 = findViewById(R.id.otp_input_2);
        otp_3 = findViewById(R.id.otp_input_3);
        otp_4 = findViewById(R.id.otp_input_4);
        otp_5 = findViewById(R.id.otp_input_5);
        otp_6 = findViewById(R.id.otp_input_6);
        nextBtn = findViewById(R.id.verifyBtn);
        resendOtpTextView = findViewById(R.id.resend_otp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}