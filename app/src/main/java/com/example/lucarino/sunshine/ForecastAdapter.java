package com.example.lucarino.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lucarino on 3/6/16.
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static final int VIEW_TYPE_TODAY = 0;
    public static final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean mUseTodayLayout = false;


    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int itemViewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (itemViewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if (itemViewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolderFutureItem viewHolder = new ViewHolderFutureItem(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolderFutureItem holder = (ViewHolderFutureItem) view.getTag();
        holder.tvForecast.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));
        holder.tvDay.setText(Utility.getDayName(mContext, cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));
        holder.tvMaxTemp.setText(Utility.formatTemperature(context, cursor.getLong(ForecastFragment.COL_WEATHER_MAX_TEMP), Utility.isMetric(mContext)));
        holder.tvMinTemp.setText(Utility.formatTemperature(context, cursor.getLong(ForecastFragment.COL_WEATHER_MIN_TEMP), Utility.isMetric(mContext)));
        int conditionId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        holder.ivForecast.setImageResource(Utility.getIconResourceForWeatherCondition(conditionId));
    }

    public static class ViewHolderFutureItem {
        public ImageView ivForecast;
        public TextView tvDay;
        public TextView tvForecast;
        public TextView tvMaxTemp;
        public TextView tvMinTemp;

        public ViewHolderFutureItem(View view) {
            ivForecast = (ImageView) view.findViewById(R.id.iv_forecast);
            tvDay = (TextView) view.findViewById(R.id.tv_day);
            tvForecast = (TextView) view.findViewById(R.id.tv_forecast_desc);
            tvMaxTemp = (TextView) view.findViewById(R.id.tv_max_temp);
            tvMinTemp = (TextView) view.findViewById(R.id.tv_min_temp);
        }
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }
}
