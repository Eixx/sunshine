package com.example.cpe.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cpe.sunshine.app.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

  public static final String DATE_KEY = "forecast_date";
  private static final String LOCATION_KEY = "location";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container_detail, new DetailFragment())
          .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();


    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DetailFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
        WeatherContract.WeatherEntry.COLUMN_DATETEXT,
        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    private ShareActionProvider mShareActionProvider;
    private String mLocation;
    private String mForecast;
    private String detailedData;
    private final String SUNSHINE_SHARE_HASHTAGE = " - #SUNSHINE";

    public DetailFragment() {
      setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.detail_fragment, menu);

      MenuItem menuItem = menu.findItem(R.id.menu_item_share);

      mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

      setShareIntent(createShareForecastIntent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
      detailedData = getActivity().getIntent().getExtras().getString(ForecastFragment.DATE_KEY);
      return rootView;
    }

    private void setShareIntent(Intent shareIntent) {
      if (mShareActionProvider != null) {
        mShareActionProvider.setShareIntent(shareIntent);
      }
    }

    private Intent createShareForecastIntent() {
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + SUNSHINE_SHARE_HASHTAGE);
      return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      getLoaderManager().initLoader(DETAIL_LOADER, null, this);
      if (savedInstanceState != null) {
        mLocation = savedInstanceState.getString(LOCATION_KEY);
      }
      super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      Log.v(TAG, "In onCreateLoader");
      Intent intent = getActivity().getIntent();
      if (intent == null || !intent.hasExtra(DATE_KEY)) {
        return null;
      }
      String forecastDate = intent.getStringExtra(DATE_KEY);

      // Sort order:  Ascending, by date.
      String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

      mLocation = Utility.getPreferredLocation(getActivity());
      Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
          mLocation, forecastDate);
      Log.v(TAG, weatherForLocationUri.toString());

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      Log.v(TAG, "In onLoadFinished");
      if (!data.moveToFirst()) { return; }

      String dateString = Utility.formatDate(
          data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));
      ((TextView) getView().findViewById(R.id.list_item_date_textview))
          .setText(dateString);

      String weatherDescription =
          data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
      ((TextView) getView().findViewById(R.id.list_item_forecast_textview))
          .setText(weatherDescription);

      boolean isMetric = Utility.isMetric(getActivity());

      String high = Utility.formatTemperature(
          data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
      ((TextView) getView().findViewById(R.id.list_item_high_textview)).setText(high);

      String low = Utility.formatTemperature(
          data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
      ((TextView) getView().findViewById(R.id.list_item_low_textview)).setText(low);

      // We still need this for the share intent
      mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

      Log.v(TAG, "Forecast String: " + mForecast);

      // If onCreateOptionsMenu has already happened, we need to update the share intent now.
      if (mShareActionProvider != null) {
        mShareActionProvider.setShareIntent(createShareForecastIntent());
      }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

  }
}
