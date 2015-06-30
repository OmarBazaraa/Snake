package com.bazaraa.snake;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
                Boolean.getBoolean(context.getString(R.string.pref_border_value_default))
        );
    }

    public static boolean enableNotifications(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPrefs.getBoolean(
                context.getString(R.string.pref_notification_key),
                Boolean.getBoolean(context.getString(R.string.pref_notification_value_default))
        );
    }

    public static String getFormattedDate(long dateInMillis) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormatter.format(dateInMillis);
    }

    public static String getFormattedDuration(Context context, long durationInMillis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) - TimeUnit.MINUTES.toSeconds(minutes);

        return context.getString(R.string.format_duration, minutes, seconds);
    }
}