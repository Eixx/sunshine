package com.example.cpe.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.cpe.sunshine.app.data.WeatherContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author by Christian Petersen <mailto:cpe@visiolink.com>
 * @version 1.0
 * @since 30/01/15
 */

public class Utility {
  public static String getPreferredLocation(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(context.getString(R.string.pref_location_key),
        context.getString(R.string.pref_location_default));
  }

  public static boolean isMetric(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(context.getString(R.string.pref_temperature_units_key),
        context.getString(R.string.pref_temperature_default))
        .equals(context.getString(R.string.pref_temperature_default));
  }

  static String formatTemperature(double temperature, boolean isMetric) {
    double temp;
    if ( !isMetric ) {
      temp = 9*temperature/5+32;
    } else {
      temp = temperature;
    }
    return String.format("%.0f", temp);
  }

  static String formatDate(String dateString) {
    Date date = WeatherContract.getDateFromDb(dateString);
    return DateFormat.getDateInstance().format(date);
  }

}

