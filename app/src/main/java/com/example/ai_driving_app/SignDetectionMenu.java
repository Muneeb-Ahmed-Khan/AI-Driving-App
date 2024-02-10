package com.example.ai_driving_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignDetectionMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_detection_menu);

        Button driverMode = findViewById(R.id.driver_btn);
        Button pictureMode = findViewById(R.id.pic_btn);

         driverMode.setOnClickListener(view -> {

                    Intent intent = new Intent(SignDetectionMenu.this, SignDetectionDriveMode.class);
                    startActivity(intent);
                });

                pictureMode.setOnClickListener(view -> {
                    Intent intent = new Intent(SignDetectionMenu.this, SignDetection.class);
                    startActivity(intent);
                });


    }
}