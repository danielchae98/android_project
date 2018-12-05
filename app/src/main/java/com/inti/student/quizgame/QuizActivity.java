package com.inti.student.quizgame;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    //declare a 30 seconds
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView txt_ViewQuestion;
    private TextView txt_ViewScore;
    private TextView txt_ViewQuestionCount;
    private TextView txt_ViewCountDown;
    private RadioGroup radioButtonGroup;
    private RadioButton radioButton_1;
    private RadioButton radioButton_2;
    private RadioButton radioButton_3;
    private Button buttonConfirmNext;

    private ColorStateList txt_DefaultColorRb;
    private ColorStateList txt_DefaultColorCd;

    private CountDownTimer countDownTimer;
    private long time_left;

    private ArrayList<Question> questionList;
    private int question_Counter;
    private int total_count;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //assign all the variables by the id name from layout folder
        txt_ViewQuestion = findViewById(R.id.viewQuestion);
        txt_ViewScore = findViewById(R.id.viewScore);
        txt_ViewQuestionCount = findViewById(R.id.viewQuestionCount);
        txt_ViewCountDown = findViewById(R.id.viewCountdown);
        radioButtonGroup = findViewById(R.id.radioGroup);
        radioButton_1 = findViewById(R.id.radioButton_1);
        radioButton_2 = findViewById(R.id.radioButton_2);
        radioButton_3 = findViewById(R.id.radioButton_3);
        buttonConfirmNext = findViewById(R.id.confirmNext);

        txt_DefaultColorRb = radioButton_1.getTextColors();
        txt_DefaultColorCd = txt_ViewCountDown.getTextColors();

        if (savedInstanceState == null) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            //assign and retrieve all the data from database and pass it to questionList
            questionList = dbHelper.getAllQuestions();
            //calculate the size of questionList
            total_count = questionList.size();
            //shuffle the questions
            Collections.shuffle(questionList);
            //move to the next question method()
            nextQuestion();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            total_count = questionList.size();
            question_Counter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(question_Counter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            time_left = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            //if question was not answered yet
            if (!answered) {
                //proceed count_down() method
                count_down();
            } else {
                //otherwise, proceed to update_cDownText() and solution() method
                update_cDownText();
                solution();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    //if radio button answer is selected
                    if (radioButton_1.isChecked() || radioButton_2.isChecked() || radioButton_3.isChecked()) {
                        //proceed to checkAnswer() method to check the answer
                        checkAnswer();
                    } else {
                        //if no radio button answer is selected, print out a message
                        Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //if the question is already answered
                    //proceed to nextQuestion() method
                    nextQuestion();
                }
            }
        });
    }

    private void nextQuestion() {
        //after move to the next question, set the radio button back to default colour
        radioButton_1.setTextColor(txt_DefaultColorRb);
        radioButton_2.setTextColor(txt_DefaultColorRb);
        radioButton_3.setTextColor(txt_DefaultColorRb);
        //set the radio button back to uncheck
        radioButtonGroup.clearCheck();

        if (question_Counter < total_count) {
            //if the current questions is lesser than the total questions
            currentQuestion = questionList.get(question_Counter);

            //set the new question, option1, option2 and option3
            txt_ViewQuestion.setText(currentQuestion.getQuestion());
            radioButton_1.setText(currentQuestion.getOption1());
            radioButton_2.setText(currentQuestion.getOption2());
            radioButton_3.setText(currentQuestion.getOption3());
            //auto increment by 1
            question_Counter++;
            //update the current question number and total question
            txt_ViewQuestionCount.setText("Question: " + question_Counter + "/" + total_count);
            answered = false;
            buttonConfirmNext.setText("Confirm");

            time_left = COUNTDOWN_IN_MILLIS;
            //proceed to count_down() method
            count_down();
        } else {
            //if user finish the total questions
            finishQuiz();
        }
    }

    private void count_down() {
        //pass the timer and interval by 1000 (1 second)
        countDownTimer = new CountDownTimer(time_left, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_left = millisUntilFinished;
                update_cDownText();
            }

            @Override
            public void onFinish() {
                time_left = 0;
                //proceed to update_cDownText() and checkAnswer() method
                update_cDownText();
                checkAnswer();
            }
        }.start();
    }

    private void update_cDownText() {
        //set the timer format
        int minutes = (int) (time_left / 1000) / 60;
        int seconds = (int) (time_left / 1000) % 60;
        //set the timer format
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        txt_ViewCountDown.setText(timeFormatted);

        //if the timer is lesser than 10 seconds, set the text of timer into red
        if (time_left < 10000) {
            txt_ViewCountDown.setTextColor(Color.RED);
        } else {
            //otherwise, the colour follow by default
            txt_ViewCountDown.setTextColor(txt_DefaultColorCd);
        }
    }

    private void checkAnswer() {
        answered = true;

        //terminate the timer
        countDownTimer.cancel();

        //pass the radio button that user selected to rbSelected
        RadioButton rbSelected = findViewById(radioButtonGroup.getCheckedRadioButtonId());
        //assign the rbSelected to answerNr, + 1 because the index of answer starts from 1 in the database
        int answerNr = radioButtonGroup.indexOfChild(rbSelected) + 1;

        //if the selected radio button equals to the answer
        if (answerNr == currentQuestion.getAnswerNr()) {
            //add 1 mark
            score++;
            //update score marks
            txt_ViewScore.setText("Score: " + score);
        }
        //if selected radio button answer is wrong
        //show solution() method
        solution();
    }

    private void solution() {
        //set all the text in radio button into red
        radioButton_1.setTextColor(Color.RED);
        radioButton_2.setTextColor(Color.RED);
        radioButton_3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNr()) {
            case 1:
                //if the first answer is the correct answer, turns the text colour into green
                radioButton_1.setTextColor(Color.GREEN);
                txt_ViewQuestion.setText("The first one is the correct answer");
                break;
            case 2:
                //if the second answer is the correct answer, turns the text colour into green
                radioButton_2.setTextColor(Color.GREEN);
                txt_ViewQuestion.setText("The second one is the correct answer");
                break;
            case 3:
                //if the third answer is the correct answer, turns the text colour into green
                radioButton_3.setTextColor(Color.GREEN);
                txt_ViewQuestion.setText("The third one is the correct answer");
                break;
        }

        if (question_Counter < total_count) {
            //button text display Next
            buttonConfirmNext.setText("Next");
        } else {
            //if finished the total questions, the button text display Finish
            buttonConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        //pass back the score value
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //if click on the back button + 2 seconds is larger than currentTimeMillis()
        //which means if less than 2 seconds left, proceed to finishQuiz() method
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            //if back button longer than 2 seconds, display a message
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if timer is not null
        if (countDownTimer != null) {
            //terminate the timer
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, question_Counter);
        outState.putLong(KEY_MILLIS_LEFT, time_left);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}

