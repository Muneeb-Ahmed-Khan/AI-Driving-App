package com.example.ai_driving_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        // Use a Handler to delay the opening of the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Close the splash screen activity
            }
        }, SPLASH_TIMEOUT);
    }
}
