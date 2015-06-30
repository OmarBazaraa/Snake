package com.bazaraa.snake.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class ScoreContract implements BaseColumns {

    public static final String CONTENT_AUTHORITY = "com.bazaraa.snake";
    public static final String PATH_HIGHSCORE = "highscore";
    public static final String TABLE_NAME = "highscore";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_DURATION = "duration";

    public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY).buildUpon().appendPath(PATH_HIGHSCORE).build();

    public static Uri buildScoreUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}