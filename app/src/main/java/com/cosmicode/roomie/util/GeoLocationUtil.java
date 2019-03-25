package com.cosmicode.roomie.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocationUtil {
    private static final String TAG = "GeoLocationUtil";

    public static String[] getLocationText(LatLng loc, Context context) {
        String[] location = {"",""};

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
            /*
             * Return 1 address.
             */
            addresses = geocoder.getFromLocation(loc.latitude,
                    loc.longitude, 1);
        } catch (IOException e1) {
            Log.e(TAG, "IO Exception in getLocationText()");
            location[0] = "N/A";
            location[1] = "N/A";
        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " +
                    Double.toString(loc.latitude) +
                    " , " +
                    Double.toString(loc.longitude) +
                    " passed to address service";
            Log.e(TAG, errorString);
            location[0] = "N/A";
            location[1] = "N/A";
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);

            location[0] = (address.getLocality() != null) ? address.getLocality() : ((address.getAdminArea() != null) ? address.getAdminArea() : "N/A");
            location[1] = (address.getAdminArea() != null) ? address.getAdminArea() : ((address.getLocality() != null) ? address.getLocality() : "N/A");
            Log.d(TAG, String.format("City: %s , State: %s", location[0], location[1]));
        } else {
            location[0] = "N/A";
            location[1] = "N/A";
        }

        return location;
    }

    public static String[] getLocationText(Location location, Context context) {
        return getLocationText(new LatLng(location.getLatitude(), location.getLongitude()), context);
    }
}
