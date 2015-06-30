package com.bazaraa.snake.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ScoreProvider extends ContentProvider {

    private ScoreDbHelper mSqlHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int SCORE = 100;
    private static final int SCORE_WITH_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(ScoreContract.CONTENT_AUTHORITY, ScoreContract.PATH_HIGHSCORE, SCORE);
        matcher.addURI(ScoreContract.CONTENT_AUTHORITY, ScoreContract.PATH_HIGHSCORE + "/#", SCORE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mSqlHelper = new ScoreDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;

        switch (sUriMatcher.match(uri)) {
            case SCORE:
                returnCursor = mSqlHelper.getReadableDatabase().query(
                        ScoreContract.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
//            case SCORE_WITH_ID:
//
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case SCORE:
                long id = mSqlHelper.getReadableDatabase().insert(ScoreContract.TABLE_NAME, null, values);

                if (id > -1) {
                    returnUri = ScoreContract.buildScoreUri(id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
//            case SCORE_WITH_ID:
//
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        if (selection == null) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {
            case SCORE:
                rowsDeleted = mSqlHelper.getWritableDatabase().delete(ScoreContract.TABLE_NAME, selection, selectionArgs);
                break;
//            case SCORE_WITH_ID:
//
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case SCORE:
                rowsUpdated = mSqlHelper.getWritableDatabase().update(ScoreContract.TABLE_NAME, values, selection, selectionArgs);
                break;
//            case SCORE_WITH_ID:
//
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SCORE:
                return ScoreContract.CONTENT_TYPE_DIR;
            case SCORE_WITH_ID:
                return ScoreContract.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }
}