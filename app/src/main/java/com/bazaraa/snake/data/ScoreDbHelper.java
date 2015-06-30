package com.bazaraa.snake.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ScoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "score.db";
    private static final int DATABASE_VERSION = 1;

    public ScoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SCORE_TABLE = "CREATE TABLE " + ScoreContract.TABLE_NAME + " (" +
                ScoreContract._ID + " INTEGER PRIMARY KEY," +
                ScoreContract.COLUMN_USERNAME + " TEXT NOT NULL, " +
                ScoreContract.COLUMN_DATE + " INTEGER NOT NULL, " +
                ScoreContract.COLUMN_SCORE + " INTEGER NOT NULL, " +
                ScoreContract.COLUMN_LENGTH + " INTEGER NOT NULL, " +
                ScoreContract.COLUMN_DURATION + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ScoreContract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}