package com.bazaraa.snake;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bazaraa.snake.data.ScoreContract;


public class HighscoreFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ScoreAdapter mScoreAdapter;
    private ListView mListView;

    private static final int SCORE_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_highscore, container, false);

        mScoreAdapter = new ScoreAdapter(getActivity(), null, 0);
        mListView = (ListView) rootView.findViewById(R.id.listView_highscore);

        mListView.setAdapter(mScoreAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCORE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ScoreContract.CONTENT_URI,
                null,
                null,
                null,
                ScoreContract.COLUMN_SCORE + " DESC, " + ScoreContract.COLUMN_DURATION + ", " + ScoreContract.COLUMN_DATE
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mScoreAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mScoreAdapter.swapCursor(null);
    }
}