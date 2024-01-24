package com.example.ai_driving_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class chatbotmenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbotmenu);

        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView HomeIcon = findViewById(R.id.homePageIcon);
        Button askMeAnything = findViewById(R.id.askMeAnything);



        profileIcon.setOnClickListener(view -> {
            // Perform the redirection to the profile page here
            redirectToProfilePage();
        });

        HomeIcon.setOnClickListener(view -> {
            // Redirect the user to the home page of the application
            homepage();
        });

        askMeAnything.setOnClickListener(view -> {
            chatbot();
        });


    }

    public void redirectToProfilePage() {

        Intent intent = new Intent(chatbotmenu.this, profile.class);
        startActivity(intent);
    }

    public void homepage(){

        Intent intent = new Intent(chatbotmenu.this, MainActivity.class);
        startActivity(intent);

    }

    public void chatbot(){

        Intent intent = new Intent(chatbotmenu.this, chatbot.class);
        startActivity(intent);
    }

}