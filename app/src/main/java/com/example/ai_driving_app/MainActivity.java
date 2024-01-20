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


        profileIcon.setOnClickListener(view -> {
            // Perform the redirection to the profile page here
            redirectToProfilePage();
        });

        // perform this step to redirect me to the LearnQuiz page
        bulbIcon.setOnClickListener(view -> {
            learnQuiz();
        });
    }
    private void redirectToProfilePage() {

        Intent intent = new Intent(MainActivity.this, profile.class);
        startActivity(intent);
    }

    private void learnQuiz(){

        Intent intent = new Intent(MainActivity.this, learnQuiz.class);
        startActivity(intent);
    }

//        Button quizButton = findViewById(R.id.submitButton);
//        quizButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Quiz.class);
//                startActivity(intent);
//            }
//        });

    }

