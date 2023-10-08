package com.example.ai_driving_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextInputEditText emailEditText, passwordEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // Check if user is already logged in
        if (currentUser != null) {
            // User is already logged in, go to the main activity
            startActivity(new Intent(Login.this, MainActivity.class));
            finish(); // finish() prevents the user from coming back to this screen using the back button
        }

        // Initialize EditText fields
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
    }

    public void onLoginClick(View view) {
        // Get the input values
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login success, update UI accordingly
                            Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            // You can add further actions here, such as opening a new activity
                        } else {
                            // If login fails, display a message to the user.
                            Toast.makeText(Login.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    // Method to handle "Create an Account" click event
    public void onCreateAccountClick(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
