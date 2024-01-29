package com.example.ai_driving_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        // Retrieve image file path from the intent
        String imagePath = getIntent().getStringExtra("imagePath");

        // Load the image into an ImageView
        ImageView imageView = findViewById(R.id.captureImage);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);
    }
}