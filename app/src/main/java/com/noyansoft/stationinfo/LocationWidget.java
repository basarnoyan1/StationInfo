package com.noyansoft.stationinfo;

import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Window;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class LocationWidget extends AppWidgetProvider {
    public static String adres;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = adres;
        // Construct the RemoteViews object
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.location_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Locator(context);
                Intent configIntent = new Intent(context, MainActivity.class);
                PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.xml.location_widget_info);
                remoteViews.setOnClickPendingIntent(R.layout.location_widget, configPendingIntent);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public void Locator(Context con){
        try {
            final Geocoder geocoder = new Geocoder(con, Locale.getDefault());
            LocationManager locationManager = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
            try {

                Location l = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
                if (l != null) {
                    double latitude = l.getLatitude();
                    double longitude = l.getLongitude();
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String mah = addresses.get(0).getSubLocality();
                        String ilce = addresses.get(0).getSubAdminArea();
                        String il = addresses.get(0).getAdminArea();
                        adres = mah + ", " + ilce + ", " + il;
                    } catch (IOException i) {
                        String m = i.getMessage();
                        adres = latitude+","+longitude;
                    }
                }
            } catch (SecurityException s) {
            }
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String mah = addresses.get(0).getSubLocality();
                        String ilce = addresses.get(0).getSubAdminArea();
                        String il = addresses.get(0).getAdminArea();
                        adres = mah + ", " + ilce + ", " + il;
                    } catch (IOException | SecurityException i) {
                        String m = i.getMessage();
                        adres = latitude+","+longitude;
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            String m = e.getMessage();
        }
    }
}

