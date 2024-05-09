package com.example.ai_driving_app;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisplayImageActivity extends AppCompatActivity {

    public DatabaseReference databaseReference;

    public FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    Uri imageUri;
    Button submitBtn;

    private static final String TAG = "DisplayImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        String imagePath = (getIntent().getExtras()).getString("imagePath");
        Log.d(TAG, imagePath);

        // Load the image into an ImageView
        if (imagePath != null) {
            ImageView imageView = findViewById(R.id.captureImage);
            imageUri = Uri.parse(imagePath);
            imageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Error displaying image", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error displaying image");
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Get a reference to the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("ai-driving-app");

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Get storage reference.
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();



        // set on click listener for the button to capture a photo
        // it calls a method which is implemented below
        submitBtn = findViewById(R.id.incident_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBtn.setEnabled(false);
                // Handle button click event here
                takePhoto(imageUri); // Replace with your desired functionality
            }
        });

    }

    public void takePhoto(Uri imageUri){

        Log.d(TAG, "Trying to upload image.");

        // Create a reference to the location where you want to upload the image
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        // Upload the image
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Image uploaded successfully.");
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Uri contains the download URL
                        String imageUrl = uri.toString();
                        // Now, you can store 'imageUrl' in Firebase or perform other actions
                        submitting(imageUrl);

                        submitBtn.setEnabled(true);

                    });
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.getMessage());
                });

    }

    public void submitting(String imageUrl) {
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
                String uid = currentUser.getEmail();

                // Create a data object or map
                Map<String, Object> incidentData = new HashMap<>();
                incidentData.put("incidentName", incidentName);
                incidentData.put("incidentSummary", incidentSummary);
                incidentData.put("incidentTime", incidentTime);
                incidentData.put("email", uid); // Add the user's UID to the incident data
                incidentData.put("incidentImageURL", imageUrl);


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