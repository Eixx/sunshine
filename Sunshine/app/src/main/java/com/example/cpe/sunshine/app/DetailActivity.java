package com.example.cpe.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container_detail, new PlaceholderFragment())
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
  public static class PlaceholderFragment extends Fragment {

    private ShareActionProvider mShareActionProvider;
    private String detailedData;
    private final String SUNSHINE_SHARE_HASHTAGE = " - #SUNSHINE";

    public PlaceholderFragment() {
      setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.detail_fragment, menu);

      MenuItem menuItem = menu.findItem(R.id.menu_item_share);

      mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_TEXT, detailedData + SUNSHINE_SHARE_HASHTAGE);
      setShareIntent(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
      detailedData = getActivity().getIntent().getExtras().getString(ForecastFragment.FORECAST_CLICKED_DETAIL);
      TextView textView = (TextView) rootView.findViewById(R.id.forecast_data);
      textView.setText(detailedData);
      return rootView;
    }

    private void setShareIntent(Intent shareIntent) {
      if (mShareActionProvider != null) {
        mShareActionProvider.setShareIntent(shareIntent);
      }
    }
  }
}
