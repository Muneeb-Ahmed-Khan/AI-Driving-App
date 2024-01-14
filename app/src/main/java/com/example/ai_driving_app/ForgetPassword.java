package com.example.ai_driving_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private EditText emailEditText;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_password);

        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize EditText field
        emailEditText = findViewById(R.id.Email);
    }

    public void onResetPasswordClick(View view) {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your registered email address", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            finish(); // Close the ForgetPasswordActivity after sending the reset email
                        } else {
                            Toast.makeText(ForgetPassword.this, "Failed to send reset email. Check your email address.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
