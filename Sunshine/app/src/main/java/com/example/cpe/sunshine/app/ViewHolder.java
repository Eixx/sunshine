package com.example.cpe.sunshine.app;

/**
 * @author by Christian Petersen <mailto:cpe@visiolink.com>
 * @version 1.0
 * @since 30/01/15
 */

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Cache of the children views for a forecast list item.
 */
public class ViewHolder {
  public final ImageView iconView;
  public final TextView dateView;
  public final TextView descriptionView;
  public final TextView highTempView;
  public final TextView lowTempView;

  public ViewHolder(View view) {
    iconView = (ImageView) view.findViewById(R.id.list_item_icon);
    dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
    descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
    highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
    lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
  }

}
