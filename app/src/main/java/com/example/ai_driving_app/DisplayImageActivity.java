package com.example.ai_driving_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class DisplayImageActivity extends AppCompatActivity {

    public DatabaseReference databaseReference;

    public FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Get a reference to the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("ai-driving-app");

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Get the image path from the intent
        String imagePath = getIntent().getStringExtra("imagePath");

        // Load the image into an ImageView
        ImageView imageView = findViewById(R.id.captureImage);
        if (imagePath != null) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);}
        else {
            Toast.makeText(this, "Error displaying image", Toast.LENGTH_SHORT).show();
        }

    }

    public void submitting(View view) {
        try {
            // Retrieve entered information
            String incidentName = ((TextInputEditText) findViewById(R.id.incident_name)).getText().toString();
            String incidentSummary = ((TextInputEditText) findViewById(R.id.incident_summary_msg)).getText().toString();
            String incidentTime = ((TextInputEditText) findViewById(R.id.incident_time_info)).getText().toString();

            // Check if any of the fields is empty
            if (incidentName.isEmpty() || incidentSummary.isEmpty() || incidentTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current user
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                // User is signed in, get the UID
                String uid = currentUser.getUid();

                // Create a data object or map
                Map<String, Object> incidentData = new HashMap<>();
                incidentData.put("incidentName", incidentName);
                incidentData.put("incidentSummary", incidentSummary);
                incidentData.put("incidentTime", incidentTime);
                incidentData.put("uid", uid); // Add the user's UID to the incident data

                // Log data before pushing to Firebase (for debugging purposes)
                Log.d("SubmitInfo", "Incident Data: " + incidentData.toString());

                // Push data to Firebase Realtime Database
                databaseReference.push().setValue(incidentData);

                // Optionally, show a success message or navigate to another screen
                Toast.makeText(this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // User is not signed in, handle the case appropriately
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("SubmitInfo", "Exception: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}