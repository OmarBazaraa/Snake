package com.bazaraa.snake;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bazaraa.snake.data.ScoreContract;


public class ScoreAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    public ScoreAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST : VIEW_TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = -1;
        int viewType = getItemViewType(cursor.getPosition());

        switch (viewType) {
            case VIEW_TYPE_FIRST:
                layoutId = R.layout.list_item_highscore_first;
                break;
            case VIEW_TYPE_NORMAL:
                layoutId = R.layout.list_item_highscore;
                break;
        }

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        itemView.setTag(viewHolder);

        return itemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int index = cursor.getPosition() + 1;
        viewHolder.indexView.setText(String.valueOf(index));

        String username = cursor.getString(cursor.getColumnIndex(ScoreContract.COLUMN_USERNAME));
        viewHolder.usernameView.setText(username);

        long date = cursor.getLong(cursor.getColumnIndex(ScoreContract.COLUMN_DATE));
        String formattedDate = Utility.getFormattedDate(date);
        viewHolder.dateView.setText(formattedDate);

        int score = cursor.getInt(cursor.getColumnIndex(ScoreContract.COLUMN_SCORE));
        viewHolder.scoreView.setText(String.valueOf(score));

        int duration = cursor.getInt(cursor.getColumnIndex(ScoreContract.COLUMN_DURATION));
        String formattedDuration = Utility.getFormattedDuration(context, duration);
        viewHolder.durationView.setText(formattedDuration);
    }

    private static class ViewHolder {

        public final TextView indexView;
        public final TextView usernameView;
        public final TextView dateView;
        public final TextView scoreView;
        public final TextView durationView;

        public ViewHolder(View view) {
            indexView = (TextView) view.findViewById(R.id.listItem_textView_index);
            usernameView = (TextView) view.findViewById(R.id.listItem_textView_username);
            dateView = (TextView) view.findViewById(R.id.listItem_textView_date);
            scoreView = (TextView) view.findViewById(R.id.listItem_textView_score);
            durationView = (TextView) view.findViewById(R.id.listItem_textView_duration);
        }
    }
}