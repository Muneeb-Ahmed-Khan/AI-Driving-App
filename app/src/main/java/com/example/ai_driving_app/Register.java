package com.example.ai_driving_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, confirmEmailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize EditText fields and the Register button
        usernameEditText = findViewById(R.id.Username);
        emailEditText = findViewById(R.id.Email);
        confirmEmailEditText = findViewById(R.id.ConfirmEmail);
        passwordEditText = findViewById(R.id.Password);
        Button registerButton = findViewById(R.id.btn_Register);

        // Set a click listener for the register button
        registerButton.setOnClickListener(v -> {
            // Get the input values
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String confirmEmail = confirmEmailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Check if any field is empty
            if (username.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email and confirm email match
            if (!email.equals(confirmEmail)) {
                Toast.makeText(Register.this, "Emails do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check password criteria
            if (password.length() < 6 ||
                    !password.matches(".*[A-Z].*") ||
                    !password.matches(".*\\d.*") ||
                    !password.matches(".*[!@#$%^&*()].*")) {
                Toast.makeText(Register.this, "Password must be at least 6 characters long and contain at least one uppercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show();
                return;
            }


            // Send data to Firebase
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            String userId = databaseReference.push().getKey();
            User user = new User(userId, username, email, password);

            assert userId != null;
            databaseReference.child(userId).setValue(user);

            // Redirect to Login activity
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish(); // Optional, this will finish the current activity to prevent going back to it from the login screen

            // Display confirmation message
            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
        });
    }
}
