package com.bazaraa.snake;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    private String mGameControl;
    private boolean mShowBorder;
    private boolean mTwoPane;

    public void buttonStartClicked(View view) {
        startActivity(new Intent(this, GameActivity.class));
    }

    public void buttonHighscoreClicked(View view) {
        startActivity(new Intent(this, HighscoreActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameControl = Utility.getGameControlValue(this);
        mShowBorder = Utility.showBorder(this);

        if (findViewById(R.id.fragment_game) != null) {
            mTwoPane = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTwoPane) {
            String gameControl = Utility.getGameControlValue(this);
            boolean showBorder = Utility.showBorder(this);

            if (!gameControl.equals(mGameControl) || showBorder != mShowBorder) {
                GameFragment gf = (GameFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_game);
                gf.updateExternalSettings(gameControl, showBorder);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
