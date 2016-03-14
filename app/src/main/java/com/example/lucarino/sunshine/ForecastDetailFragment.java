package com.example.lucarino.sunshine;

import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.lucarino.sunshine.data.WeatherContract;
import com.example.lucarino.sunshine.data.WeatherContract.WeatherEntry;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


/**
 * Displays Forecast details.
 */
public class ForecastDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SHARE_FORECAST_KEY = "forecast.info";
    private static final String hashTagSunshineApp = "#SunshineApp";
    private static final String ARG_FORECAST_INFO = "argForecastInfo";

    private static Uri mForecastInfo;

    private final String TAG = getClass().getSimpleName();

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    TextView mForeCastDescTv;
    TextView mDayTv;
    TextView mDateTv;
    TextView mMaxTempTv;
    TextView mMinTempTv;
    ImageView mForecastIv;
    TextView mHumidityTv;
    TextView mWindTv;
    TextView mPressureTv;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND = 7;
    private static final int COL_WEATHER_DESC_ID =8;


    public static Fragment newInstance(Uri detailUri) {
        ForecastDetailFragment forecastDetailFragment = new ForecastDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FORECAST_INFO, detailUri);
        forecastDetailFragment.setArguments(args);
        return forecastDetailFragment;
    }

    public ForecastDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_FORECAST_INFO)) {
            mForecastInfo = arguments.getParcelable(ARG_FORECAST_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mForecastIv = (ImageView) rootView.findViewById(R.id.iv_forecast_desc);
        mForeCastDescTv = (TextView) rootView.findViewById(R.id.tv_forecast_desc);
        mDayTv = (TextView) rootView.findViewById(R.id.tv_day);
        mDateTv = (TextView) rootView.findViewById(R.id.tv_date);
        mMaxTempTv = (TextView) rootView.findViewById(R.id.tv_max_temp);
        mMinTempTv = (TextView) rootView.findViewById(R.id.tv_min_temp);
        mHumidityTv = (TextView) rootView.findViewById(R.id.tv_humidity);
        mWindTv = (TextView) rootView.findViewById(R.id.tv_wind);
        mPressureTv = (TextView) rootView.findViewById(R.id.tv_max_pressure);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public static Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.putExtra(SHARE_FORECAST_KEY, mForecastInfo + hashTagSunshineApp);
        return intent;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(TAG, "In onCreateLoader");

        if(null == mForecastInfo) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                mForecastInfo,
                FORECAST_COLUMNS,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }


        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));


        String weatherDescription =
                data.getString(COL_WEATHER_DESC);


        boolean isMetric = Utility.isMetric(getActivity());


        String high = Utility.formatTemperature(getContext(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);


        String low = Utility.formatTemperature(getContext(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);


        String humidity =
                data.getString(COL_WEATHER_HUMIDITY);

        String wind =
                data.getString(COL_WEATHER_WIND);

        String pressure =
                data.getString(COL_WEATHER_PRESSURE);

        int weatherDescId = data.getInt(COL_WEATHER_DESC_ID);

        mForecastIv.setImageResource(Utility.getIconResourceForWeatherCondition(weatherDescId));
        mForeCastDescTv.setText(weatherDescription);
        mDayTv.setText(Utility.getDayName(getContext(), data.getLong(ForecastFragment.COL_WEATHER_DATE)));
        mDateTv.setText(dateString);
        mMaxTempTv.setText(high);
        mMinTempTv.setText(low);
        mHumidityTv.setText(getString(R.string.format_humidity, humidity));
        mPressureTv.setText(getString(R.string.format_pressure, pressure));
        mWindTv.setText(getString(R.string.format_wind, wind));


        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(getShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged(String newLocation) {
        Uri uri = mForecastInfo;

        if(null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mForecastInfo = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
