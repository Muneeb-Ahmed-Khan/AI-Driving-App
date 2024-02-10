package com.example.ai_driving_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView bulbIcon = findViewById(R.id.learnQuizBulb);
        ImageView chatBotIcon = findViewById(R.id.chatBotIcon);
        ImageView roadGuard = findViewById(R.id.roadGuard);
        ImageView signDetectionOption = findViewById(R.id.signDetectionOption);
        ImageView nearByDrivingSchool = findViewById(R.id.nearby_driving_school);
        profileIcon.setOnClickListener(view -> {
            // Perform the redirection to the profile page here
            redirectToProfilePage();
        });

        // perform this step to redirect me to the LearnQuiz page
        bulbIcon.setOnClickListener(view -> {
            learnQuiz();
        });

        chatBotIcon.setOnClickListener(view -> {
            onChatBotIconClick();

        });

        roadGuard.setOnClickListener(view ->{

            // redirect to roadGuard
            onRoadGuardClick();
        });

        nearByDrivingSchool.setOnClickListener(view ->{

            // redirect to nearby driving schools on google maps

            Intent intent = new Intent(MainActivity.this, com.example.ai_driving_app.nearByDrivingSchool.class);
            startActivity(intent);


        });

        signDetectionOption.setOnClickListener(view -> {
              Intent intent = new Intent(MainActivity.this, SignDetectionMenu.class);
              startActivity(intent);

        });
    }
    public void redirectToProfilePage() {

        Intent intent = new Intent(MainActivity.this, profile.class);
        startActivity(intent);
    }

    private void learnQuiz(){

        Intent intent = new Intent(MainActivity.this, learnQuiz.class);
        startActivity(intent);
    }

    private  void onChatBotIconClick(){

        Intent intent = new Intent(MainActivity.this, chatbotmenu.class);
        startActivity(intent);
    }

    private void onRoadGuardClick(){

        Intent intent = new Intent(MainActivity.this, roadGuard.class);
        startActivity(intent);
    }

    }

