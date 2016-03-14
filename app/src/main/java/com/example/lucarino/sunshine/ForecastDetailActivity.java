package com.example.lucarino.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ForecastDetailActivity extends AppCompatActivity {


    private final String TAG = getClass().getSimpleName();
    private Uri forecastInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // remove the title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // set up navigation icon enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (savedInstanceState == null) {
            if (getIntent() != null) {
                forecastInfo = getIntent().getData();
                Log.d(TAG, forecastInfo.toString());
            }


            Fragment fragment = ForecastDetailFragment.newInstance(forecastInfo);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.forecast_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_share);


        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (actionProvider != null) {
            actionProvider.setShareIntent(ForecastDetailFragment.getShareIntent());
        } else {
            Log.d(TAG, "Share action provider is null?");
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent mIntent = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(mIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
