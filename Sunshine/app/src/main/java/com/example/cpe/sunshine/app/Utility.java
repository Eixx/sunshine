package com.example.cpe.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

}

