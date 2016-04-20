package com.example.lucarino.sunshine;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lucarino.sunshine.data.WeatherContract;
import com.example.lucarino.sunshine.sync.SunshineSyncAdapter;


/**
 * Fragment used to show the forecast fetched data from the server.
 */
public class ForecastFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_FORECAST_INFO = "extra.forecast.info";
    public static final int FORECAST_LOADER_ID = 0;
    private static final String KEY_BUNDLE_LAST_POSITION = "lastItemPosition";
    public static ForecastAdapter mForecastAdapter;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private OnForecastListener mListener;
    private ListView mForecastListView;
    private int mLastSelectedPosition = -1;

    public ForecastFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnForecastListener) {
            mListener = (OnForecastListener) context;
        } else {
            throw new IllegalStateException("Activity must implement "+OnForecastListener.class.getSimpleName());
        }
    }

    @Override
    public void onStart() {
       super.onStart();
       // updateForecastWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_BUNDLE_LAST_POSITION, mLastSelectedPosition);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_BUNDLE_LAST_POSITION))
        {
            mLastSelectedPosition = savedInstanceState.getInt(KEY_BUNDLE_LAST_POSITION);
        }


        final String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri, null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);



        View parentView;
        parentView = inflater.inflate(R.layout.fragment_main, container, false);
        mForecastListView = (ListView) parentView.findViewById(R.id.list_view_forecast);
        mForecastListView.setDivider(null);
        mForecastListView.setAdapter(mForecastAdapter);
        mForecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                mLastSelectedPosition = position;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                mListener.onItemClicked(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting, cursor.getLong(COL_WEATHER_DATE)));

            }
        });

        return parentView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            // final String url =
            // "http://api.openweathermap.org/data/2.5/forecast/daily?lat=28.30468&lon=-81.416672&units=metric&cnt=7";

            updateForecastWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    /**
     * Executes an async call to fetch forecast.
     */
    public void updateForecastWeather() {
        SunshineSyncAdapter.syncImmediately(getContext());
//        final String location = Utility.getPreferredLocation(getActivity());
//        final int TRIGGER_AT_MILLIS = 5*1000;
//        // create the intent to be triggered by the AM.
//        Intent intentReceiver = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        intentReceiver.putExtra(SunshineService.LOCATION_QUERY_EXTRA, location);
//        // wrapping the intent in a PendingIntent
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 0 , intentReceiver, 0);
//        // set the alarm
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, TRIGGER_AT_MILLIS, alarmIntent);

    }

    /**
     * Gets the stored value for location in the application's preferences.
     * 
     * @return - the zip code for looking the location, or the default value if not value is stored.
     */
    public String getLocationFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
    }

    /**
     * Gets the stored value for units in the application's preferences.
     * 
     * @return - the unit code <code>metric</code> for Celsius units or <code>imperial</code> for
     *         Fahrenheit.
     */
    public String getUnitFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences
                .getString(getString(R.string.pref_unit_key), getString(R.string.pref_unit_default));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSettings = getLocationFromPreferences();

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        final Uri weatherLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSettings, System.currentTimeMillis());


        return new CursorLoader(getActivity(),
                weatherLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(mLastSelectedPosition != -1) {
            mForecastListView.setSelection(mLastSelectedPosition);
            mForecastListView.smoothScrollToPosition(mLastSelectedPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
    }

    private void updateWeather() {
        updateForecastWeather();
    }

    public interface OnForecastListener {
        void onItemClicked(Uri dateUri);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mForecastAdapter.setUseTodayLayout(useTodayLayout);
    }
}
