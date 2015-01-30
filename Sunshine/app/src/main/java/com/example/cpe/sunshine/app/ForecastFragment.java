package com.example.cpe.sunshine.app;

/**
 * @author by Christian Petersen <mailto:cpe@visiolink.com>
 * @version 1.0
 * @since 28/01/15
 */


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String FORECAST_CLICKED_DETAIL = "com.example.clicked.details";

  ArrayAdapter<String> adapter;
  public ForecastFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onStart() {
    super.onStart();
    updateWeatherData();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());
    ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
    listView.setAdapter(adapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String forecasteData = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class)
            .putExtra(FORECAST_CLICKED_DETAIL, forecasteData);
        startActivity(intent);
      }
    });

    return rootView;
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
    new FetchWeatherTask(getActivity(), adapter).execute(location);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.forecast_menu, menu);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {

  }
}
