package com.example.ai_driving_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class learnQuiz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_quiz);

        ImageView ready_button = findViewById(R.id.ready_button);

        ready_button.setOnClickListener(view -> {
            Intent intent = new Intent(learnQuiz.this, Quiz.class);
            startActivity(intent);
        });
    }


}