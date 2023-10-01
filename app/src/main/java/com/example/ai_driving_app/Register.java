package com.example.ai_driving_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, confirmEmailEditText, passwordEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

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

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create a new user with email and password
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registration success, update UI accordingly
                                        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        // You can add further actions here, such as opening a new activity
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        // If registration fails, display a message to the user.
                                        Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

        });
    }
}
