package com.example.cpe.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author by Christian Petersen <mailto:cpe@visiolink.com>
 * @version 1.0
 * @since 30/01/15
 */

public class ForecastAdapter extends CursorAdapter {

  private static final int VIEW_TYPE_TODAY = 0;
  private static final int VIEW_TYPE_FUTURE_DAY = 1;
  private static final int VIEW_TYPE_COUNT = 2;

  public ForecastAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  /**
   * Copy/paste note: Replace existing newView() method in ForecastAdapter with this one.
   */
  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    // Choose the layout type
    int viewType = getItemViewType(cursor.getPosition());
    int layoutId = -1;
    if(viewType == VIEW_TYPE_TODAY) {
      layoutId = R.layout.list_item_forecast_today;
    } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
      layoutId = R.layout.list_item_forecast;
    }

    View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
    ViewHolder viewHolder = new ViewHolder(view);
    view.setTag(viewHolder);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    ViewHolder viewHolder = (ViewHolder)view.getTag();

    // Read weather icon ID from cursor
    int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
    viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

    // Read date from cursor
    String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
    viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

    // Read weather forecast from cursor
    // Find TextView and set weather forecast on it
    String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
    viewHolder.descriptionView.setText(description);

    // Read user preference for metric or imperial temperature units
    // Read high temperature from cursor
    boolean isMetric = Utility.isMetric(context);
    float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
    viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

    // Read low temperature from cursor
    float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
    viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPE_COUNT;
  }


}