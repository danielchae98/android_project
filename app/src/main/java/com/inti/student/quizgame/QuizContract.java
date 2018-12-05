package com.inti.student.quizgame;

import android.provider.BaseColumns;

//this quiz contract class - need for sqlite operations
public final class QuizContract {

    //this private constructor is created to prevent creating object of QuizContract accidentally
    private QuizContract() {
    }

    //to create in a class for each different table in our database
    //BaseColumns automatically create and providing additional constants variable (ID)(Auto increment)
    public static class QuestionsTable implements BaseColumns {

        //public so that it can be accessed in other class
        //final because we dont want to change the name

        //sqlite database table name
        public static final String TABLE_NAME = "Math_questions";

        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "choice_1";
        public static final String COLUMN_OPTION2 = "choice_2";
        public static final String COLUMN_OPTION3 = "choice_3";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
    }
}


