package com.bazaraa.snake;

import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setElevation(0.0f);

        TextView versionView = (TextView) findViewById(R.id.textView_version);

        try {
            versionView.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}