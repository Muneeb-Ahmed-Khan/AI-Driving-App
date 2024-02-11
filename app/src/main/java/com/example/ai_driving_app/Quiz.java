package com.example.ai_driving_app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Quiz extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button submitButton;

    private final String[] questions = {
            "What does a red traffic light mean?",
            "What should you do when you see a yield sign?",
            "What does a stop sign indicate?",
            "What does a yellow traffic light indicate?",
            "When should you use your turn signals?",
            "What does a blue traffic sign usually indicate?",
            "What is the purpose of a crosswalk?",
            "What does a white, rectangular sign with black lettering usually indicate?",
            "What should you do when approaching a school bus with its lights flashing and a stop sign extended?",
            "What does a double solid yellow line on the road mean?"
    };

    private final String[] options = {
            "Stop", "Go", "Slow down",
            "Slow down", "Stop if necessary", "Continue without stopping",
            "Stop", "Proceed with caution", "Yield right of way",
            "Slow down", "Stop", "Proceed with caution",
            "Only when changing lanes", "Only when turning", "When changing lanes or turning",
            "Warning", "Regulatory information", "Information or guidance",
            "To park your vehicle", "To allow pedestrians to cross the road safely", "To stop your vehicle",
            "Speed limit", "Stop sign", "Yield sign",
            "Pass the bus carefully", "Stop and wait until the bus moves on", "Honk to alert the driver",
            "You can pass if no oncoming traffic is present", "Passing is not allowed in either direction", "You can pass, but only if it's safe"
    };

    private final int[] correctAnswers = {0, 1, 0, 0, 2, 2, 1, 0, 1, 1}; // Index of the correct options for each question
    private int currentQuestionIndex = 0;
    private int correctCount = 0;
    private final int totalQuestions = questions.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        submitButton = findViewById(R.id.submitButton);

        displayQuestion();

        submitButton.setOnClickListener(v -> {
            int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();

            if (selectedOptionId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedOptionId);
                int selectedOptionIndex = optionsRadioGroup.indexOfChild(selectedRadioButton);

                if (selectedOptionIndex == correctAnswers[currentQuestionIndex]) {
                    correctCount++;
                    showToast("Correct Answer");
                    selectedRadioButton.setTextColor(Color.GREEN);
                } else {
                    showToast("Wrong Answer");
                    selectedRadioButton.setTextColor(Color.RED);
                }

                // Pause for 2 seconds before moving to the next question
                submitButton.setEnabled(false);
                optionsRadioGroup.setEnabled(false);

                new Handler().postDelayed(() -> {
                    if (currentQuestionIndex < questions.length - 1) {
                        currentQuestionIndex++;
                        displayQuestion();
                        submitButton.setEnabled(true);
                        optionsRadioGroup.setEnabled(true);
                    } else {
                        // End of the quiz
                        displayResult();
                    }
                }, 2000);
            } else {
                Toast.makeText(Quiz.this, "Please select an option", Toast.LENGTH_SHORT).show();
            }
        });



    }
     private void showToast(String message) {
        Toast.makeText(Quiz.this, message, Toast.LENGTH_SHORT).show();
    }


    private void displayQuestion() {
        questionTextView.setText(questions[currentQuestionIndex]);

        for (int i = 0; i < 3; i++) {
            RadioButton radioButton = (RadioButton) optionsRadioGroup.getChildAt(i);
            radioButton.setText(options[currentQuestionIndex * 3 + i]);
            radioButton.setTextColor(Color.BLACK);
        }

        optionsRadioGroup.clearCheck();
    }

    @SuppressLint("SetTextI18n")
    private void displayResult() {
        double percentage = (double) correctCount / totalQuestions * 100;

        String resultMessage;
        if (percentage >= 60) {
            resultMessage = "Congratulations!!!!";
            questionTextView.setTextColor(Color.GREEN);
        } else {
            resultMessage = "You Failed";
            questionTextView.setTextColor(Color.RED);
        }

        questionTextView.setText(resultMessage + "\n\nYou got " + correctCount + " out of " + totalQuestions + " questions correct.");
        optionsRadioGroup.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }
}
