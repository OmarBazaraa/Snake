package com.bazaraa.snake;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bazaraa.snake.custom.SnakeView;
import com.bazaraa.snake.data.ScoreContract;


public class GameFragment extends Fragment {

    private SnakeView mSnake;
    private MenuItem menuItemPlayPause;

    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    private static final int SNAKE_NOTIFICATION_ID = 5115;
    private static final String KEY_SNAKE_STATES = "snake_states";
    private static final String KEY_EXTRA_HIGHSCORE = "extra_highscore";

    private void openScoreDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_score, null);

        TextView scoreView = (TextView) view.findViewById(R.id.dialog_textView_score);

        Cursor c = getActivity().getContentResolver().query(
                ScoreContract.CONTENT_URI,
                null,
                null,
                null,
                ScoreContract.COLUMN_SCORE + " DESC, " + ScoreContract.COLUMN_DURATION + ", " + ScoreContract.COLUMN_DATE
        );

        if (c.moveToFirst()) {
            int highscore = c.getInt(c.getColumnIndex(ScoreContract.COLUMN_SCORE));
            scoreView.setText(mSnake.getScore() + " (" + highscore + ")");
        }
        else {
            scoreView.setText(String.valueOf(mSnake.getScore()));
        }

        c.close();

        TextView lengthView = (TextView) view.findViewById(R.id.dialog_textView_length);
        lengthView.setText(String.valueOf(mSnake.getLength()));

        TextView durationView = (TextView) view.findViewById(R.id.dialog_textView_duration);
        durationView.setText(Utility.getFormattedDuration(getActivity(), mSnake.getDuration()));

        final EditText editText_username = (EditText) view.findViewById(R.id.dialog_editText_username);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);

        final AlertDialog dialog = builder.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_username.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.warning_username), Toast.LENGTH_LONG).show();
                } else {
                    ContentValues scoreValues = new ContentValues();
                    scoreValues.put(ScoreContract.COLUMN_USERNAME, editText_username.getText().toString());
                    scoreValues.put(ScoreContract.COLUMN_DATE, System.currentTimeMillis());
                    scoreValues.put(ScoreContract.COLUMN_SCORE, mSnake.getScore());
                    scoreValues.put(ScoreContract.COLUMN_LENGTH, mSnake.getLength());
                    scoreValues.put(ScoreContract.COLUMN_DURATION, mSnake.getDuration());

                    getActivity().getContentResolver().insert(ScoreContract.CONTENT_URI, scoreValues);

                    if (Utility.enableNotifications(getActivity())) {
                        notifyToPlayAgain();
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    private void notifyToPlayAgain() {
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        Cursor c = getActivity().getContentResolver().query(
                ScoreContract.CONTENT_URI,
                null,
                null,
                null,
                ScoreContract.COLUMN_SCORE + " DESC, " + ScoreContract.COLUMN_DURATION + ", " + ScoreContract.COLUMN_DATE
        );

        if (c.moveToFirst()) {
            int highscore = c.getInt(c.getColumnIndex(ScoreContract.COLUMN_SCORE));
            alarmIntent.putExtra(KEY_EXTRA_HIGHSCORE, highscore);
        }

        c.close();

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DAY_IN_MILLIS, alarmPendingIntent);
    }

    public void updateExternalSettings(String controlMode, boolean showBorder) {
        if (controlMode.equals(getActivity().getString(R.string.pref_control_value_touch))) {
            mSnake.setControlMode(SnakeView.CONTROL_MODE_TOUCH);
        }
        else if (controlMode.equals(getActivity().getString(R.string.pref_control_value_swipe))) {
            mSnake.setControlMode(SnakeView.CONTROL_MODE_SWIPE);
        }

        mSnake.setShowBorder(showBorder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

        mSnake = (SnakeView) rootView.findViewById(R.id.snakeView);

        if (savedInstanceState != null) {
            mSnake.restoreStates(savedInstanceState.getBundle(KEY_SNAKE_STATES));
        }

        if (Utility.getGameControlValue(getActivity()).equals(getActivity().getString(R.string.pref_control_value_swipe))) {
            mSnake.setControlMode(SnakeView.CONTROL_MODE_SWIPE);
        }

        mSnake.setShowBorder(Utility.showBorder(getActivity()));
        mSnake.setOnSnakeModeChangedListener(new SnakeView.OnSnakeModeChangedListener() {
            @Override
            public void onSnakeModeChanged(int mode) {
                switch (mode) {
                    case SnakeView.MODE_RUNNING:
                        if (menuItemPlayPause != null) {
                            menuItemPlayPause.setIcon(R.drawable.ic_pause);
                        }
                        break;
                    case SnakeView.MODE_PAUSED:
                        if (menuItemPlayPause != null) {
                            menuItemPlayPause.setIcon(R.drawable.ic_play);
                        }
                        break;
                    case SnakeView.MODE_DEAD:
                        openScoreDialog();
                        break;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSnake.getMode() == SnakeView.MODE_RUNNING) {
            mSnake.setMode(SnakeView.MODE_PAUSED);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSnake != null) {
            outState.putBundle(KEY_SNAKE_STATES, mSnake.saveStates());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game, menu);
        menuItemPlayPause = menu.findItem(R.id.action_play);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                switch (mSnake.getMode()) {
                    case SnakeView.MODE_RUNNING:
                        mSnake.setMode(SnakeView.MODE_PAUSED);
                        break;
                    case SnakeView.MODE_PAUSED:
                        mSnake.setMode(SnakeView.MODE_RUNNING);
                        break;
                }
                return true;
            case R.id.action_restart:
                mSnake.restart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context).addNextIntent(new Intent(context, MainActivity.class));
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            String message = context.getString(R.string.format_notification, intent.getIntExtra(KEY_EXTRA_HIGHSCORE, 0));

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(context.getString(R.string.ticker_notification))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.game_title))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(SNAKE_NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}