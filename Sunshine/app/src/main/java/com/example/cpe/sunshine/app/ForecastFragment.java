package com.example.cpe.sunshine.app;

/**
 * @author by Christian Petersen <mailto:cpe@visiolink.com>
 * @version 1.0
 * @since 28/01/15
 */


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cpe.sunshine.app.data.WeatherContract;

import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String FORECAST_CLICKED_DETAIL = "com.example.clicked.details";

  private static final int FORECAST_LOADER = 0;
  // For the forecast view we're showing only a small subset of the stored data.
  // Specify the columns we need.
  private static final String[] FORECAST_COLUMNS = {
      // In this case the id needs to be fully qualified with a table name, since
      // the content provider joins the location & weather tables in the background
      // (both have an _id column)
      // On the one hand, that's annoying.  On the other, you can search the weather table
      // using the location set by the user, which is only in the Location table.
      // So the convenience is worth it.
      WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
      WeatherContract.WeatherEntry.COLUMN_DATETEXT,
      WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
      WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
      WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
      WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS
  };


  // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
  // must change.
  public static final int COL_WEATHER_ID = 0;
  public static final int COL_WEATHER_DATE = 1;
  public static final int COL_WEATHER_DESC = 2;
  public static final int COL_WEATHER_MAX_TEMP = 3;
  public static final int COL_WEATHER_MIN_TEMP = 4;
  public static final int COL_LOCATION_SETTING = 5;

  private String mLocation;


  SimpleCursorAdapter adapter;
  public ForecastFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)) {
      getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    adapter = new SimpleCursorAdapter(
        getActivity(),
        R.layout.list_item_forecast,
        null,
        // the column names to use to fill the textviews
        new String[]{WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
        },
        // the textviews to fill with the data pulled from the columns above
        new int[]{R.id.list_item_date_textview,
            R.id.list_item_forecast_textview,
            R.id.list_item_high_textview,
            R.id.list_item_low_textview
        },
        0
    );
    adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
      @Override
      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        boolean isMetric = Utility.isMetric(getActivity());
        switch (columnIndex) {
          case COL_WEATHER_MAX_TEMP:
          case COL_WEATHER_MIN_TEMP: {
            // we have to do some formatting and possibly a conversion
            ((TextView) view).setText(Utility.formatTemperature(
                cursor.getDouble(columnIndex), isMetric));
            return true;
          }
          case COL_WEATHER_DATE: {
            String dateString = cursor.getString(columnIndex);
            TextView dateView = (TextView) view;
            dateView.setText(Utility.formatDate(dateString));
            return true;
          }
        }
        return false;
      }
    });

    ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
    listView.setAdapter(adapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SimpleCursorAdapter adapter1 = (SimpleCursorAdapter) parent.getAdapter();
        Cursor cursor = adapter1.getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
          boolean isMetric = Utility.isMetric(getActivity());
          String forecast = String.format("%s - %s - %s/%s",
              Utility.formatDate(cursor.getString(COL_WEATHER_DATE)),
              cursor.getString(COL_WEATHER_DESC),
              Utility.formatTemperature(cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric),
              Utility.formatTemperature(cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
          Intent intent = new Intent(getActivity(), DetailActivity.class)
              .putExtra(FORECAST_CLICKED_DETAIL, forecast);
          startActivity(intent);
        }
      }
    });

    return rootView;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // This is called when a new Loader needs to be created.  This
    // fragment only uses one loader, so we don't care about checking the id.

    // To only show current and future dates, get the String representation for today,
    // and filter the query to return weather only for dates after or including today.
    // Only return data after today.
    String startDate = WeatherContract.getDbDateString(new Date());

    // Sort order:  Ascending, by date.
    String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

    mLocation = Utility.getPreferredLocation(getActivity());
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
        mLocation, startDate);

    // Now create and return a CursorLoader that will take care of
    // creating a Cursor for the data being displayed.
    return new CursorLoader(
        getActivity(),
        weatherForLocationUri,
        FORECAST_COLUMNS,
        null,
        null,
        sortOrder
    );
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_refresh) {
      updateWeatherData();
    }
    return super.onOptionsItemSelected(item);
  }

  private void updateWeatherData() {
    String location = Utility.getPreferredLocation(getActivity());
    new FetchWeatherTask(getActivity()).execute(location);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.forecast_menu, menu);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    adapter.swapCursor(cursor);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {
    adapter.swapCursor(null);
  }
}
