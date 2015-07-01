package com.bazaraa.snake;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.bazaraa.snake.data.ScoreContract;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Utility {

    public static String getGameControlValue(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPrefs.getString(
                context.getString(R.string.pref_control_key),
                context.getString(R.string.pref_control_value_touch)
        );
    }

    public static String getThemeValue(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPrefs.getString(
                context.getString(R.string.pref_theme_key),
                context.getString(R.string.pref_theme_value_orange)
        );
    }

    public static boolean showBorder(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPrefs.getBoolean(
                context.getString(R.string.pref_border_key),
                Boolean.parseBoolean(context.getString(R.string.pref_border_value_default))
        );
    }

    public static boolean enableNotifications(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPrefs.getBoolean(
                context.getString(R.string.pref_notification_key),
                Boolean.parseBoolean(context.getString(R.string.pref_notification_value_default))
        );
    }

    public static int getHighscore(Context context) {
        int highscore = 0;

        Cursor c = context.getContentResolver().query(
                ScoreContract.CONTENT_URI,
                null,
                null,
                null,
                ScoreContract.COLUMN_SCORE + " DESC, " + ScoreContract.COLUMN_DURATION + ", " + ScoreContract.COLUMN_DATE
        );

        if (c.moveToFirst()) {
            highscore = c.getInt(c.getColumnIndex(ScoreContract.COLUMN_SCORE));
        }

        c.close();

        return highscore;
    }

    public static String getFormattedDuration(Context context, long durationInSeconds) {
        long minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds);
        long seconds = durationInSeconds - TimeUnit.MINUTES.toSeconds(minutes);

        return context.getString(R.string.format_duration, minutes, seconds);
    }


    public static String getFormattedDate(long dateInMillis) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormatter.format(dateInMillis);
    }
}