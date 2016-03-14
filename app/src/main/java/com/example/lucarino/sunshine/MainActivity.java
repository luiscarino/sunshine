package com.example.lucarino.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ForecastFragment.OnForecastListener {

    private final String LOG_TAG = getClass().getName();
    private final static String FORECAST_DETAIL_FRAGMENT_TAG = "ForecastDetailFragmentTag";
    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (findViewById(R.id.forecast_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.forecast_detail_container, ForecastDetailFragment.newInstance(null))
                        .commit();
            }
        } else {
            mTwoPane = false;

        }

        ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.forecast_fragment);
        if (null != ff) {
            ff.setUseTodayLayout(!mTwoPane);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.forecast_fragment);
            if (null != ff) {
                ff.onLocationChanged();
            }
            ForecastDetailFragment df = (ForecastDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(FORECAST_DETAIL_FRAGMENT_TAG);
            if (null != df) {
                df.onLocationChanged(location);
            }

            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent mIntent = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(mIntent);
                break;
            case R.id.action_map_location:
                openPreferredLocationInMap();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    public void openPreferredLocationInMap() {

        String location = Utility.getPreferredLocation(this);

        Uri geoLocation = Uri.parse("geo:0,0?z=5").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location);
        }

    }

    @Override
    public void onItemClicked(Uri uri) {

        if (mTwoPane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.forecast_detail_container,
                            ForecastDetailFragment.newInstance(uri),
                            FORECAST_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, ForecastDetailActivity.class)
                    .setData(uri);
            startActivity(intent);

        }
    }
}
