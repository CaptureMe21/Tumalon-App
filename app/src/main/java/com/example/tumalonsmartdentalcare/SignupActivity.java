package com.example.tumalonsmartdentalcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    TextView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

    loginBtn = findViewById(R.id.login);
    loginBtn.setOnClickListener(v -> {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    });

    }
}