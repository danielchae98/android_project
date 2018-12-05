package com.inti.student.quizgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartingScreenActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView text_Highscore;
    private int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_screen);

        text_Highscore = findViewById(R.id.textHighscore);
        //load the score
        load_score();

        Button buttonQuiz = findViewById(R.id.buttonQuiz);
        buttonQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            //if button is clicked, start the activity
            public void onClick(View v) {
                start_Quiz();
            }
        });
    }

    private void start_Quiz() {
        //start the activity
        Intent intent_activity = new Intent(StartingScreenActivity.this, QuizActivity.class);
        startActivityForResult(intent_activity,REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if request code is valid
        if(requestCode == REQUEST_CODE_QUIZ){
            if(resultCode == RESULT_OK){
                //retrieve the score from EXTRA_SCORE
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                //if the current score is higher than the high score
                if(score > highscore){
                    //pass the score and proceed to updateNewHighscore() method
                    updateNewHighscore(score);
                }
            }
        }
    }
    private void load_score(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        //take our highscore and set it to get the integer value - pass KEY_HIGHSCORE
        //highscore should be 0 by default
        highscore = prefs.getInt(KEY_HIGHSCORE,0);
        //display out highscore in the main page
        text_Highscore.setText("Highscore: " + highscore);


    }

    private void updateNewHighscore(int newHighScore){
        //assign newHighScore to highscore
        highscore = newHighScore;
        //update the highscore in the main page
        text_Highscore.setText("Highscore: " + highscore);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE,highscore);
        editor.apply();
    }
}