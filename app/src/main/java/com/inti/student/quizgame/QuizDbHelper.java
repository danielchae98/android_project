package com.inti.student.quizgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.inti.student.quizgame.QuizContract.*;

import java.util.ArrayList;


public class QuizDbHelper extends SQLiteOpenHelper {
    //create two constants
    //This is to give the database name
    private static final String DATABASE_NAME = "MathQuiz.db";
    //version of the database
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        //it use the value to pass them to super class method
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        //create database, create table - actual SQL codes
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                //access the variable that we declared from QuizContract.java
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +  //specify the data type
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER" +
                ")";

        //pass the SQL codes and execute
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        //this method is created to create questions into the table
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //delete the table if the current table name exists in database
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        //after delete the table, will move on to onCreate(db) and create a new table
        onCreate(db);
    }

    private void fillQuestionsTable() {
        //create instance of our question class
        Question q1 = new Question("The average of first 50 natural numbers is", "25.5", "25.30", "12.25", 1);
        addQuestion(q1);
        Question q2 = new Question("The number of 3-digit numbers divisible by 6, is", "166", "150", "149", 2);
        addQuestion(q2);
        Question q3 = new Question("Which of the following numbers gives 240 when added to its own square?", "20", "18", "15", 3);
        addQuestion(q3);
        Question q4 = new Question("The simplest form of 1.5 : 2.5 is ", "3 : 5", "6 : 10", "0.75 : 1.25", 1);
        addQuestion(q4);
        Question q5 = new Question("What is 1004 divided by 2?", "520", "502", "522", 2);
        addQuestion(q5);
        Question q6 = new Question("The difference between the smallest number of four digits and the largest number of three digits is", "100", "1", "999", 2);
        addQuestion(q6);
        Question q7 = new Question("The sum of the least number of three digits and largest number of two digits is", "199", "101", "109", 1);
        addQuestion(q7);
        Question q8 = new Question("A number is divisible by 3 if the sum of its digits is divisible by ", "1", "5", "3", 3);
        addQuestion(q8);
        Question q9 = new Question("A number is divisible by 5 if its unit digit is", "2 or 0", "0 or 5", "10 or 0", 2);
        addQuestion(q9);
        Question q10 = new Question("Simplify: 26 + 32 - 12", "32", "46", "56", 2);
        addQuestion(q10);
    }

    //this method is created to insert the question data into database
    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        //insert the data according to the column name
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        //insert the value into database
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        //reference from database to get the data out
        db = getReadableDatabase();
        //select all the data from the table
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);

        //move the cursor to the first entry
        //return false if there is no entry
        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                //retrieve data from the database
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                //add question to questionList
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }
}